package com.dashur.integration.extw.connectors.parimatch;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceRequest;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceResponse;
import com.dashur.integration.extw.connectors.parimatch.data.BetRequest;
import com.dashur.integration.extw.connectors.parimatch.data.CancelRequest;
import com.dashur.integration.extw.connectors.parimatch.data.ErrorResponse;
import com.dashur.integration.extw.connectors.parimatch.data.TransactionResponse;
import com.dashur.integration.extw.connectors.parimatch.data.WinRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;

/** PariMatch Dispatcher */
@Slf4j
public class PariMatchDispatcher extends Dispatcher {

  public static final String CASINO_ID = "parimatch";
  public static final String CONSUMER_ID = "maverick";

  public static final String LAUNCH_TOKEN_2976011 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1061";
  public static final String TOKEN_2976011 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1061";
  public static final String LAUNCH_TOKEN_2976012 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1062";
  public static final String TOKEN_2976012 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1062";
  public static final String LAUNCH_TOKEN_2976013 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1063";
  public static final String TOKEN_2976013 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1063";

  private static final Object LOCK = new Object();
  private static PariMatchDispatcher instance = null;

  public static final PariMatchDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new PariMatchDispatcher();
        }
      }
    }

    return instance;
  }

  private Map<String, String> launchTokenToToken = new HashMap<>();
  private Map<String, MockPlayer> tokenToPlayer = new HashMap<>();
  private Map<String, MockPlayer> idToPlayer = new HashMap<>();
  private Map<String, MockTransaction> txIdToTx = new HashMap<>();
  private Map<String, MockTransaction> txIdRefToTx = new HashMap<>();

  /** constructor. */
  public PariMatchDispatcher() {
    initData();
  }

  /** initialize parimatch data. */
  void initData() {
    synchronized (LOCK) {
      launchTokenToToken = new HashMap<>();
      tokenToPlayer = new HashMap<>();
      idToPlayer = new HashMap<>();
      txIdToTx = new HashMap<>();
      txIdRefToTx = new HashMap<>();

      // It is a good habit to ensure each test classes has its own member, to avoid balance check
      // issues, however read only shouldn't be issues, can be shared.
      launchTokenToToken.put(LAUNCH_TOKEN_2976011, TOKEN_2976011);
      launchTokenToToken.put(LAUNCH_TOKEN_2976012, TOKEN_2976012);
      launchTokenToToken.put(LAUNCH_TOKEN_2976013, TOKEN_2976013);

      {
        MockPlayer player = new MockPlayer();
        player.setPlayerId("2976011");
        player.setDisplayName("Andrew");
        player.setCountry("Ukraine");
        player.setCurrency("EUR");
        player.setBalance(100000);
        tokenToPlayer.put(TOKEN_2976011, player);
        idToPlayer.put(player.getPlayerId(), player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setPlayerId("2976012");
        player.setDisplayName("Nguyen");
        player.setCountry("Vietnam");
        player.setCurrency("USD");
        player.setBalance(100000);
        tokenToPlayer.put(TOKEN_2976012, player);
        idToPlayer.put(player.getPlayerId(), player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setPlayerId("2976013");
        player.setDisplayName("George");
        player.setCountry("Georgia");
        player.setCurrency("GEL");
        player.setBalance(100000);
        tokenToPlayer.put(TOKEN_2976013, player);
        idToPlayer.put(player.getPlayerId(), player);
      }
    }
  }

  /** reseting data into clean slate. */
  public void reset() {
    initData();
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    MockResponse result = null;

    if (request.getPath().startsWith("/external/parimatch/slots/wallet/playerInfo")) {
      BalanceRequest req = CommonUtils.jsonRead(BalanceRequest.class, request.getBody().readUtf8());
      result = balance(req);
    } else if (request.getPath().startsWith("/external/parimatch/slots/wallet/bet")) {
      BetRequest req = CommonUtils.jsonRead(BetRequest.class, request.getBody().readUtf8());
      result = bet(req);
    } else if (request.getPath().startsWith("/external/parimatch/slots/wallet/win")) {
      WinRequest req = CommonUtils.jsonRead(WinRequest.class, request.getBody().readUtf8());
      result = win(req);
    } else if (request.getPath().startsWith("/external/parimatch/slots/wallet/cancel")) {
      CancelRequest req = CommonUtils.jsonRead(CancelRequest.class, request.getBody().readUtf8());
      result = cancel(req);
    }

    if (Objects.nonNull(result)) {
      return result;
    }

    return null;
  }

  MockResponse balance(BalanceRequest req) {
    if (launchTokenToToken.containsKey(req.getToken())) {
      String token = launchTokenToToken.get(req.getToken());
      MockPlayer player = tokenToPlayer.get(token);

      BalanceResponse result = new BalanceResponse();
      result.setPlayerId(player.getPlayerId());
      result.setPlayerName(player.getDisplayName());
      result.setCountry(player.getCountry());
      result.setCurrency(player.getCurrency());
      result.setBalance(player.getBalance());
      return response(result);
    }

    return error("invalid.session.key", "Session key is invalid or expired");
  }

  MockResponse bet(BetRequest req) {
    if (launchTokenToToken.containsKey(req.getToken())) {
      String token = launchTokenToToken.get(req.getToken());
      MockPlayer player = tokenToPlayer.get(token);

      if (!txIdToTx.containsKey(req.getTxId())) {
        MockTransaction tx = new MockTransaction();
        tx.setAmount(req.getAmount());
        tx.setId(req.getTxId());
        tx.setCategory("BET");
        tx.setIdRef(req.getRoundId());
        txIdToTx.put(tx.getId(), tx);
        txIdRefToTx.put(tx.getIdRef(), tx);

        if (player.getBalance() < tx.getAmount()) {
          return error("insufficient.balance", "Insufficient player balance");
        }
        substract(player, tx.getAmount());
      }

      TransactionResponse result = new TransactionResponse();
      result.setTxId(req.getTxId());
      result.setExtwTxId(UUID.randomUUID().toString());
      result.setCreatedAt(Instant.now().toString());
      result.setBalance(player.getBalance());
      result.setProcessed(txIdToTx.containsKey(req.getTxId()));

      return response(result);
    }

    return error("invalid.session.key", "Session key is invalid or expired");
  }

  MockResponse win(WinRequest req) {
    if (!txIdRefToTx.containsKey(req.getRoundId())) {
      return error("invalid.casino.logic", "Unexpected casino logic behavior. Win without bet");
    }

    MockPlayer player = idToPlayer.get(req.getPlayerId());

    if (!txIdToTx.containsKey(req.getTxId())) {
      MockTransaction tx = new MockTransaction();
      tx.setAmount(req.getAmount());
      tx.setId(req.getTxId());
      tx.setCategory("WIN");
      tx.setIdRef(req.getRoundId());
      txIdToTx.put(tx.getId(), tx);
      txIdRefToTx.put(tx.getIdRef(), tx);

      add(player, tx.getAmount());
    }

    TransactionResponse result = new TransactionResponse();
    result.setTxId(req.getTxId());
    result.setExtwTxId(UUID.randomUUID().toString());
    result.setCreatedAt(Instant.now().toString());
    result.setBalance(player.getBalance());
    result.setProcessed(txIdToTx.containsKey(req.getTxId()));

    return response(result);
  }

  MockResponse cancel(CancelRequest req) {
    if (txIdRefToTx.containsKey(req.getRoundId())) {
      MockTransaction betTx = txIdRefToTx.get(req.getRoundId());
      if (!"BET".equals(betTx.getCategory())) {
        return error(
            "invalid.casino.logic", "Unexpected casino logic behavior. Refund winning bet");
      }
    }

    MockPlayer player = idToPlayer.get(req.getPlayerId());

    if (!txIdToTx.containsKey(req.getTxId())) {
      MockTransaction tx = new MockTransaction();
      tx.setAmount(req.getAmount());
      tx.setId(req.getTxId());
      tx.setCategory("CANCEL");
      tx.setIdRef(req.getRoundId());
      txIdToTx.put(tx.getId(), tx);

      MockTransaction orgTx = txIdRefToTx.get(req.getRoundId());
      if (Objects.isNull(orgTx) || "BET".equals(orgTx.getCategory())) { // refund a wager
        add(player, tx.getAmount());
      } else {
        return error(
            "invalid.casino.logic", "Unexpected casino logic behavior. Unhandled category");
      }
    }

    TransactionResponse result = new TransactionResponse();
    result.setTxId(req.getTxId());
    result.setExtwTxId(UUID.randomUUID().toString());
    result.setCreatedAt(Instant.now().toString());
    result.setBalance(player.getBalance());
    result.setProcessed(txIdToTx.containsKey(req.getTxId()));

    return response(result);
  }

  /**
   * deduct amount from balance.
   *
   * @param player
   * @param amount
   */
  void substract(MockPlayer player, Integer amount) {
    synchronized (player) {
      player.setBalance(player.getBalance() - amount);
    }
  }

  /**
   * add amount to balance.
   *
   * @param player
   * @param amount
   */
  void add(MockPlayer player, Integer amount) {
    synchronized (player) {
      player.setBalance(player.getBalance() + amount);
    }
  }

  MockResponse response(Object result) {
    MockResponse response = new MockResponse();
    response.setHeader("Content-Type", "application/json; charset=utf-8");
    response.setBody(CommonUtils.jsonToString(result));
    return response;
  }

  MockResponse error(String code, String message) {
    ErrorResponse error = new ErrorResponse();
    error.setCode(code);
    error.setMessage(message);
    error.setTimestamp(Instant.now().toString());

    MockResponse response = response(error);
    response.setResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    return response;
  }

  /** Mock Player */
  @Data
  static final class MockPlayer {
    private String playerId;
    private String displayName;
    private String country;
    private String currency;
    private Integer balance;
  }

  /** Mock Transaction */
  @Data
  static final class MockTransaction {
    private String id;
    private String category; // BET, WIN, CANCEL
    private String idRef;
    private Integer amount;
  }
}
