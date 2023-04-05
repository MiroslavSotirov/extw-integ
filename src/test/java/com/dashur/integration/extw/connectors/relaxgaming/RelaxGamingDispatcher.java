package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WithdrawRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.RollbackRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.DepositRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.ErrorResponse;
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

/** relaxgaming Dispatcher */
@Slf4j
public class RelaxGamingDispatcher extends Dispatcher {

  public static final Integer PARTNER_ID = 67890;
  public static final String PARTNER_ID_STR = PARTNER_ID.toString();
  public static final String CHANNEL = "web";
  public static final String AUTH = "Basic ZW06dGVzdA==";

  public static final String LAUNCH_TOKEN_2976011 = "11111061";
  public static final String TOKEN_2976011 = "22221061";
  public static final String LAUNCH_TOKEN_2976012 = "11111062";
  public static final String TOKEN_2976012 = "22221062";
  public static final String LAUNCH_TOKEN_2976013 = "11111063";
  public static final String TOKEN_2976013 = "22221063";

  private static final Object LOCK = new Object();
  private static RelaxGamingDispatcher instance = null;

  public static final RelaxGamingDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new RelaxGamingDispatcher();
        }
      }
    }

    return instance;
  }

  private Map<String, String> launchTokenToToken = new HashMap<>();
  private Map<String, MockPlayer> tokenToPlayer = new HashMap<>();
  private Map<Long, MockPlayer> idToPlayer = new HashMap<>();
  private Map<String, MockTransaction> txIdToTx = new HashMap<>();
  private Map<String, MockTransaction> txIdRefToTx = new HashMap<>();

  /** constructor. */
  public RelaxGamingDispatcher() {
    initData();
  }

  /** initialize relaxgaming data. */
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
        player.setPlayerId(2976011L);
        player.setCustomerId("86587851");
        player.setCountryCode("UA");
        player.setCurrency("EUR");
        player.setJurisdiction("IM");
        player.setBalance(100000L);
        tokenToPlayer.put(TOKEN_2976011, player);
        idToPlayer.put(player.getPlayerId(), player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setPlayerId(2976012L);
        player.setCustomerId("86587852");
        player.setCountryCode("VN");
        player.setCurrency("USD");
        player.setJurisdiction("IM");
        player.setBalance(100000L);
        tokenToPlayer.put(TOKEN_2976012, player);
        idToPlayer.put(player.getPlayerId(), player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setPlayerId(2976013L);
        player.setCustomerId("86587853");
        player.setCountryCode("GE");
        player.setCurrency("GEL");
        player.setJurisdiction("IM");
        player.setBalance(100000L);
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

    if (request.getPath().startsWith("/external/relaxgaming/" + PARTNER_ID_STR + "/verifytoken")) {
      VerifyTokenRequest req = CommonUtils.jsonRead(VerifyTokenRequest.class, request.getBody().readUtf8());
      result = verifyToken(req);
    } else if (request.getPath().startsWith("/external/relaxgaming/" + PARTNER_ID_STR + "/getbalance")) {
      BalanceRequest req = CommonUtils.jsonRead(BalanceRequest.class, request.getBody().readUtf8());
      result = getBalance(req);
    } else if (request.getPath().startsWith("/external/relaxgaming/" + PARTNER_ID_STR + "/withdraw")) {
      WithdrawRequest req = CommonUtils.jsonRead(WithdrawRequest.class, request.getBody().readUtf8());
      result = withdraw(req);
    } else if (request.getPath().startsWith("/external/relaxgaming/" + PARTNER_ID_STR + "/deposit")) {
      DepositRequest req = CommonUtils.jsonRead(DepositRequest.class, request.getBody().readUtf8());
      result = deposit(req);
    } else if (request.getPath().startsWith("/external/relaxgaming/" + PARTNER_ID_STR + "/rollback")) {
      RollbackRequest req = CommonUtils.jsonRead(RollbackRequest.class, request.getBody().readUtf8());
      result = rollback(req);
    }

    if (Objects.nonNull(result)) {
      log.info("dispatching {} INVALID_TOKEN?", result);
      return result;
    }

    log.info("dispatching null INVALID_TOKEN? path {}", request.getPath());
    return null;
  }

  MockResponse verifyToken(VerifyTokenRequest req) {
    if (launchTokenToToken.containsKey(req.getToken())) {
      String token = launchTokenToToken.get(req.getToken());
      MockPlayer player = tokenToPlayer.get(token);

      VerifyTokenResponse result = new VerifyTokenResponse();
      result.setPlayerId(player.getPlayerId());
      result.setCustomerId(player.getCustomerId());
      result.setSessionId(Long.parseLong(req.getToken()));
      result.setJurisdiction(player.getJurisdiction());
      result.setCountryCode(player.getCountryCode());
      result.setCurrency(player.getCurrency());
      result.setBalance(player.getBalance());
      return response(result);
    }
    log.error("verifyToken INVALID_TOKEN {} Session key is invalid or expired", req.getToken());
    return error("INVALID_TOKEN", "Session key is invalid or expired");
  }

  MockResponse getBalance(BalanceRequest req) {
    String token = req.getSessionId().toString();
    if (launchTokenToToken.containsKey(token)) {
      token = launchTokenToToken.get(token);
      MockPlayer player = tokenToPlayer.get(token);

      BalanceResponse result = new BalanceResponse();
      result.setSessionId(req.getSessionId());
      result.setCurrency(player.getCurrency());
      result.setBalance(player.getBalance());
      return response(result);
    }
    log.error("INVALID_TOKEN Session key is invalid or expired");
    return error("INVALID_TOKEN", "Session key is invalid or expired");
  }

  MockResponse withdraw(WithdrawRequest req) {
    String token = req.getSessionId().toString();
    if (launchTokenToToken.containsKey(token)) {
      token = launchTokenToToken.get(token);
      MockPlayer player = tokenToPlayer.get(token);

      if (!txIdToTx.containsKey(req.getTxId())) {
        MockTransaction tx = new MockTransaction();
        tx.setAmount(Long.valueOf(req.getAmount().intValue()));
        tx.setId(req.getTxId());
        tx.setCategory("BET");
        tx.setIdRef(req.getRoundId());
        txIdToTx.put(tx.getId(), tx);
        txIdRefToTx.put(tx.getIdRef(), tx);

        if (player.getBalance() < tx.getAmount()) {
          return error("INSUFFICIENT_FUNDS", "Insufficient player balance");
        }
        substract(player, tx.getAmount());
      }

      TransactionResponse result = new TransactionResponse();
      result.setTxId(req.getTxId());
      result.setRelaxTxId(UUID.randomUUID().toString());
      result.setSessionId(req.getSessionId());
      result.setBalance(player.getBalance());

      return response(result);
    }

    return error("INVALID_TOKEN", "Session key is invalid or expired");
  }

  MockResponse deposit(DepositRequest req) {
    if (!txIdRefToTx.containsKey(req.getRoundId())) {
      return error("UNHANDLED", "Unexpected casino logic behavior. Win without bet");
    }

    MockPlayer player = idToPlayer.get(req.getPlayerId());

    if (!txIdToTx.containsKey(req.getTxId())) {
      MockTransaction tx = new MockTransaction();
      tx.setAmount(Long.valueOf(req.getAmount().intValue()));
      tx.setId(req.getTxId());
      tx.setCategory("WIN");
      tx.setIdRef(req.getRoundId());
      txIdToTx.put(tx.getId(), tx);
      txIdRefToTx.put(tx.getIdRef(), tx);

      add(player, tx.getAmount());
    }

    TransactionResponse result = new TransactionResponse();
    result.setTxId(req.getTxId());
    result.setRelaxTxId(UUID.randomUUID().toString());
    result.setBalance(player.getBalance());

    return response(result);
  }

  MockResponse rollback(RollbackRequest req) {
    if (txIdRefToTx.containsKey(req.getRoundId())) {
      MockTransaction betTx = txIdRefToTx.get(req.getRoundId());
      if (!"BET".equals(betTx.getCategory())) {
        return error("UNHANDLED", "Unexpected casino logic behavior. Refund winning bet");
      }
    }

    MockPlayer player = idToPlayer.get(req.getPlayerId());

    if (!txIdToTx.containsKey(req.getTxId())) {
      MockTransaction tx = new MockTransaction();
      tx.setAmount(Long.valueOf(req.getAmount().intValue()));
      tx.setId(req.getTxId());
      tx.setCategory("CANCEL");
      tx.setIdRef(req.getRoundId());
      txIdToTx.put(tx.getId(), tx);

      MockTransaction orgTx = txIdRefToTx.get(req.getRoundId());
      if (Objects.isNull(orgTx) || "BET".equals(orgTx.getCategory())) { // refund a wager
        add(player, tx.getAmount());
      } else {
        return error("UNHANDLED", "Unexpected casino logic behavior. Unhandled category");
      }
    }

    TransactionResponse result = new TransactionResponse();
    result.setTxId(req.getTxId());
    result.setRelaxTxId(UUID.randomUUID().toString());
    result.setBalance(player.getBalance());

    return response(result);
  }

  /**
   * deduct amount from balance.
   *
   * @param player
   * @param amount
   */
  void substract(MockPlayer player, Long amount) {
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
  void add(MockPlayer player, Long amount) {
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
    MockResponse response = response(error);
    response.setResponseCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    return response;
  }

  /** Mock Player */
  @Data
  static final class MockPlayer {
    private Long playerId;
    private String customerId;
    private String countryCode;
    private String currency;
    private String jurisdiction;
    private Long balance;
  }

  /** Mock Transaction */
  @Data
  static final class MockTransaction {
    private String id;
    private String category; // BET, WIN, CANCEL
    private String idRef;
    private Long amount;
  }
}
