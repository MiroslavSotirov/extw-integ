package com.dashur.integration.extw.connectors.everymatrix;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.AuthenticateResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BalanceResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.BetRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.BetResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.CancelResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.Request;
import com.dashur.integration.extw.connectors.everymatrix.data.Response;
import com.dashur.integration.extw.connectors.everymatrix.data.WinRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.WinResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/** EveryMatrix Dispatcher */
public class EveryMatrixDispatcher extends Dispatcher {
  public static final String PROVIDER = "Provider";
  public static final String HASH_PASSWORD = "testpassword";
  public static final String LAUNCH_TOKEN_2976011 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1061";
  public static final String TOKEN_2976011 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1061";
  public static final String LAUNCH_TOKEN_2976012 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1062";
  public static final String TOKEN_2976012 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1062";
  public static final String LAUNCH_TOKEN_2976013 = "KUN7CYYpu0WzlzgpY7lAMws2p6z~1063";
  public static final String TOKEN_2976013 = "mJCSlfW5JE6lnA94XeVxKAU2E98~1063";

  private static final Object LOCK = new Object();
  private static EveryMatrixDispatcher instance = null;

  public static EveryMatrixDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new EveryMatrixDispatcher();
        }
      }
    }

    return instance;
  }

  private Map<String, String> launchTokenToToken = new HashMap<>();
  private Map<String, MockPlayer> tokenToPlayer = new HashMap<>();
  private Map<String, MockTransaction> txIdToTx = new HashMap<>();

  /** constructor. */
  public EveryMatrixDispatcher() {
    initData();
  }

  /** initialize everymatrix data. */
  void initData() {
    synchronized (LOCK) {
      launchTokenToToken = new HashMap<>();
      tokenToPlayer = new HashMap<>();
      txIdToTx = new HashMap<>();

      // It is a good habit to ensure each test classes has its own member, to avoid balance check
      // issues
      // however read only shouldn't be issues, can be shared.

      launchTokenToToken.put(LAUNCH_TOKEN_2976011, TOKEN_2976011);
      launchTokenToToken.put(LAUNCH_TOKEN_2976012, TOKEN_2976012);
      launchTokenToToken.put(LAUNCH_TOKEN_2976013, TOKEN_2976013);

      {
        MockPlayer player = new MockPlayer();
        player.setAge(26);
        player.setCountry("Ukraine");
        player.setCurrency("EUR");
        player.setSex("female");
        player.setTotalBalance(new BigDecimal("10000"));
        player.setUserId("2976011");
        player.setUsername("Andrew");
        tokenToPlayer.put(TOKEN_2976011, player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setAge(26);
        player.setCountry("Ukraine");
        player.setCurrency("EUR");
        player.setSex("female");
        player.setTotalBalance(new BigDecimal("10000"));
        player.setUserId("2976012");
        player.setUsername("Andrew-2");
        tokenToPlayer.put(TOKEN_2976012, player);
      }

      {
        MockPlayer player = new MockPlayer();
        player.setAge(26);
        player.setCountry("Ukraine");
        player.setCurrency("EUR");
        player.setSex("female");
        player.setTotalBalance(new BigDecimal("10000"));
        player.setUserId("2976013");
        player.setUsername("Andrew-3");
        tokenToPlayer.put(TOKEN_2976013, player);
      }
    }
  }

  /** reseting data into clean slate. */
  public void reset() {
    initData();
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) {
    Response result = null;

    if (request.getPath().startsWith("/external/everymatrix/Provider/Authenticate")) {
      AuthenticateRequest rq =
          CommonUtils.jsonRead(AuthenticateRequest.class, request.getBody().readUtf8());
      result = authenticate(rq);
    } else if (request.getPath().endsWith("/external/everymatrix/Provider/GetBalance")) {
      BalanceRequest rq = CommonUtils.jsonRead(BalanceRequest.class, request.getBody().readUtf8());
      result = balance(rq);
    } else if (request.getPath().endsWith("/external/everymatrix/Provider/Bet")) {
      BetRequest rq = CommonUtils.jsonRead(BetRequest.class, request.getBody().readUtf8());
      result = bet(rq);
    } else if (request.getPath().endsWith("/external/everymatrix/Provider/Win")) {
      WinRequest rq = CommonUtils.jsonRead(WinRequest.class, request.getBody().readUtf8());
      result = win(rq);
    } else if (request.getPath().endsWith("/external/everymatrix/Provider/Cancel")) {
      CancelRequest rq = CommonUtils.jsonRead(CancelRequest.class, request.getBody().readUtf8());
      result = cancel(rq);
    }

    if (Objects.nonNull(result)) {
      return response(result);
    }

    return null;
  }

  BetResponse bet(BetRequest rq) {
    if (!isValidHash(rq)) {
      return error(BetResponse.class, 111, "Invalid hash");
    }

    if (tokenToPlayer.containsKey(rq.getToken())) {
      if (txIdToTx.containsKey(rq.getExternalId())) {
        return error(BetResponse.class, 110, "Double transaction");
      }

      MockTransaction tx = new MockTransaction();
      tx.setAmount(rq.getAmount());
      tx.setId(rq.getExternalId());
      tx.setCategory("BET");
      txIdToTx.put(tx.getId(), tx);

      MockPlayer player = tokenToPlayer.get(rq.getToken());
      if (player.getTotalBalance().compareTo(tx.getAmount()) < 0) {
        return error(BetResponse.class, 105, "Insufficient funds");
      }
      substract(player, tx.getAmount());

      BetResponse result = new BetResponse();

      result.setStatus("Ok");
      result.setCurrency(player.getCurrency());
      result.setTotalBalance(player.getTotalBalance());

      return result;
    }

    return error(BetResponse.class, 102, "Token not found");
  }

  WinResponse win(WinRequest rq) {
    if (!isValidHash(rq)) {
      return error(WinResponse.class, 111, "Invalid hash");
    }

    if (tokenToPlayer.containsKey(rq.getToken())) {
      if (txIdToTx.containsKey(rq.getExternalId())) {
        return error(WinResponse.class, 110, "Double transaction");
      }
      if (!txIdToTx.containsKey(rq.getBetExternalId())) {
        return error(WinResponse.class, 109, "Transaction not found");
      } else {
        MockTransaction betTx = txIdToTx.get(rq.getBetExternalId());
        if (!"BET".equals(betTx.getCategory())) {
          return error(WinResponse.class, 101, "Unknown error - betExternalId is not BET type");
        }
      }

      MockTransaction tx = new MockTransaction();
      tx.setAmount(rq.getAmount());
      tx.setId(rq.getExternalId());
      tx.setCategory("WIN");
      tx.setIdRef(rq.getBetExternalId());
      txIdToTx.put(tx.getId(), tx);

      MockPlayer player = tokenToPlayer.get(rq.getToken());
      add(player, tx.getAmount());

      WinResponse result = new WinResponse();

      result.setStatus("Ok");
      result.setCurrency(player.getCurrency());
      result.setTotalBalance(player.getTotalBalance());

      return result;
    }

    return error(WinResponse.class, 102, "Token not found");
  }

  CancelResponse cancel(CancelRequest rq) {
    if (!isValidHash(rq)) {
      return error(CancelResponse.class, 111, "Invalid hash");
    }

    if (tokenToPlayer.containsKey(rq.getToken())) {
      if (txIdToTx.containsKey(rq.getExternalId())) {
        return error(CancelResponse.class, 110, "Double transaction");
      }
      if (!txIdToTx.containsKey(rq.getCanceledExternalId())) {
        return error(CancelResponse.class, 109, "Transaction not found");
      }

      MockTransaction orgTx = txIdToTx.get(rq.getCanceledExternalId());

      MockTransaction tx = new MockTransaction();
      tx.setId(rq.getExternalId());
      tx.setCategory("CANCEL");
      tx.setAmount(orgTx.getAmount());
      tx.setIdRef(rq.getCanceledExternalId());
      txIdToTx.put(tx.getId(), tx);

      MockPlayer player = tokenToPlayer.get(rq.getToken());
      if ("BET".equals(orgTx.getCategory())) { // refund a wager
        add(player, tx.getAmount());
      } else if ("WIN".equals(orgTx.getCategory())) { // refund a payout
        substract(player, tx.getAmount());
      } else {
        return error(
            CancelResponse.class, 101, "Unknown error - Unable to cancel unknown category");
      }

      CancelResponse result = new CancelResponse();

      result.setStatus("Ok");
      result.setCurrency(player.getCurrency());
      result.setTotalBalance(player.getTotalBalance());

      return result;
    }

    return error(CancelResponse.class, 102, "Token not found");
  }

  BalanceResponse balance(BalanceRequest rq) {
    if (!isValidHash(rq)) {
      return error(BalanceResponse.class, 111, "Invalid hash");
    }

    if (tokenToPlayer.containsKey(rq.getToken())) {
      MockPlayer player = tokenToPlayer.get(rq.getToken());
      BalanceResponse result = new BalanceResponse();

      result.setStatus("Ok");
      result.setCurrency(player.getCurrency());
      result.setTotalBalance(player.getTotalBalance());

      return result;
    }

    return error(BalanceResponse.class, 102, "Token not found");
  }

  AuthenticateResponse authenticate(AuthenticateRequest rq) {
    if (!isValidHash(rq)) {
      return error(AuthenticateResponse.class, 111, "Invalid hash");
    }

    if (launchTokenToToken.containsKey(rq.getLaunchToken())) {
      String token = launchTokenToToken.get(rq.getLaunchToken());
      MockPlayer player = tokenToPlayer.get(token);

      AuthenticateResponse result = new AuthenticateResponse();
      result.setStatus("Ok");
      result.setToken(token);

      result.setAge(player.getAge());
      result.setCountry(player.getCountry());
      result.setCurrency(player.getCurrency());
      result.setSex(player.getSex());
      result.setTotalBalance(player.getTotalBalance());
      result.setUserId(player.getUserId());
      result.setUsername(player.getUsername());

      return result;
    }

    return error(AuthenticateResponse.class, 102, "Token not found");
  }

  /**
   * add amount to balance.
   *
   * @param player
   * @param amount
   */
  void add(MockPlayer player, BigDecimal amount) {
    synchronized (player) {
      player.setTotalBalance(player.getTotalBalance().add(amount));
    }
  }

  /**
   * deduct amount from balance.
   *
   * @param player
   * @param amount
   */
  void substract(MockPlayer player, BigDecimal amount) {
    synchronized (player) {
      player.setTotalBalance(player.getTotalBalance().subtract(amount));
    }
  }

  /**
   * validate if hash is valid.
   *
   * @param request
   * @return
   */
  Boolean isValidHash(Request request) {
    if (Objects.isNull(request.getHash())) {
      // no hash means can skip validation, will return true;
      return Boolean.TRUE;
    }

    return request
        .getHash()
        .equals(EveryMatrixConnectorServiceImpl.Utils.hash(HASH_PASSWORD, request));
  }

  /**
   * Simple & generic error handling.
   *
   * @param clazz
   * @param code
   * @param description
   * @param <T>
   * @return
   */
  <T extends Response> T error(Class<T> clazz, Integer code, String description) {
    T result;
    if (clazz.isAssignableFrom(AuthenticateResponse.class)) {
      result = (T) new AuthenticateResponse();
    } else if (clazz.isAssignableFrom(BalanceResponse.class)) {
      result = (T) new BalanceResponse();
    } else if (clazz.isAssignableFrom(BetResponse.class)) {
      result = (T) new BetResponse();
    } else if (clazz.isAssignableFrom(WinResponse.class)) {
      result = (T) new WinResponse();
    } else if (clazz.isAssignableFrom(CancelResponse.class)) {
      result = (T) new CancelResponse();
    } else {
      throw new ApplicationException("clazz [%s] is not mapped", clazz.getName());
    }

    result.setStatus("Failed");
    result.setErrorCode(code);
    result.setErrorDescription(description);
    result.setLogId(UUID.randomUUID().toString());

    return result;
  }

  /**
   * @param result
   * @return
   */
  MockResponse response(Object result) {
    MockResponse response = new MockResponse();
    response.setHeader("Content-Type", "application/json; charset=utf-8");
    response.setBody(CommonUtils.jsonToString(result));
    return response;
  }

  /** Mock Player */
  @Data
  static final class MockPlayer {
    private String userId;
    private String username;
    private Integer age;
    private String country;
    private String currency;
    private BigDecimal totalBalance;
    private String sex;
  }

  /** Mock Transaction */
  @Data
  static final class MockTransaction {
    private String id;
    private String category; // BET, WIN, CANCEL
    private String idRef;
    private BigDecimal amount;
  }
}
