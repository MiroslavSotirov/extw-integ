package com.dashur.integration.extw.connectors.vgs;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.vgs.data.*;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.util.*;
import lombok.Data;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.codec.digest.DigestUtils;

/** EveryMatrix Dispatcher */
public class VgsDispatcher extends Dispatcher {
  public static final String PASS_KEY = "testpassword";
  public static final String TOKEN_2976011 = UUID.randomUUID().toString();
  public static final String TOKEN_2976012 = UUID.randomUUID().toString();
  public static final String TOKEN_2976013 = UUID.randomUUID().toString();

  private static final Object LOCK = new Object();
  private static VgsDispatcher instance = null;

  public static final VgsDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new VgsDispatcher();
        }
      }
    }

    return instance;
  }

  private Map<String, String> launchTokenToToken = new HashMap<>();
  private Map<String, MockPlayer> tokenToPlayer = new HashMap<>();
  private Map<String, String> idToToken = new HashMap<>();
  private Map<String, MockTransaction> txIdToTx = new HashMap<>();

  /** constructor. */
  public VgsDispatcher() {
    initData();
  }

  /** initialize everymatrix data. */
  void initData() {
    synchronized (LOCK) {
      launchTokenToToken = new HashMap<>();
      tokenToPlayer = new HashMap<>();
      idToToken = new HashMap<>();
      txIdToTx = new HashMap<>();

      // It is a good habit to ensure each test classes has its own member, to avoid balance check
      // issues
      // however read only shouldn't be issues, can be shared.

      launchTokenToToken.put(TOKEN_2976011, TOKEN_2976011);
      launchTokenToToken.put(TOKEN_2976012, TOKEN_2976012);
      launchTokenToToken.put(TOKEN_2976013, TOKEN_2976013);

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
        idToToken.put(player.getUserId(), TOKEN_2976011);
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
        idToToken.put(player.getUserId(), TOKEN_2976012);
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
        idToToken.put(player.getUserId(), TOKEN_2976013);
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
    GenericRequestInfo info = parse(request);
    Optional<MockResponse> validateResponse = validateAndReturnIfError(info);

    if (!validateResponse.isPresent()) {
      if (ActionType.AUTH == info.getActionType()) {
        result = authenticate(info);
      } else if (ActionType.BALANCE == info.getActionType()) {
        result = balance(info);
      } else if (ActionType.CHANGE_BALANCE == info.getActionType()) {
        result = transaction(info);
      }
    } else {
      result = validateResponse.get();
    }

    if (Objects.nonNull(result)) {
      return result;
    }

    return null;
  }

  enum ActionType {
    AUTH,
    CHANGE_BALANCE,
    BALANCE;
  }

  @Data
  static class GenericRequestInfo {
    private ActionType actionType;
    private Map<String, Object> params = Maps.newHashMap();
    private String hash;
  }

  GenericRequestInfo parse(RecordedRequest request) {
    GenericRequestInfo result = new GenericRequestInfo();

    if (request.getPath().contains("/authenticate.do")) {
      result.setActionType(ActionType.AUTH);
    } else if (request.getPath().contains("/ChangeBalance.aspx")) {
      result.setActionType(ActionType.CHANGE_BALANCE);
    } else if (request.getPath().contains("/getbalance.do")) {
      result.setActionType(ActionType.BALANCE);
    }

    result.setHash(request.getRequestUrl().queryParameter("hash"));
    for (String key : request.getRequestUrl().queryParameterNames()) {
      result.getParams().put(key, request.getRequestUrl().queryParameter(key));
    }

    return result;
  }

  Boolean isValidPath(String path) {
    if (CommonUtils.isEmptyOrNull(path)) {
      return Boolean.FALSE;
    }

    if (path.endsWith("/authenticate.do")
        || path.endsWith("/ChangeBalance.aspx")
        || path.endsWith("/getbalance.do")) {
      if (path.startsWith("/external/vgs")) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  Optional<MockResponse> validateAndReturnIfError(GenericRequestInfo info) {
    MockResponse response = new MockResponse();
    response.setHeader("Content-Type", "application/xml");
    Response err = new Response();
    err.setResult("FAILED");

    if (Objects.isNull(info)) {
      err.setCode("399");
    }
    if (Objects.isNull(info.getActionType())) {
      err.setCode("399");
    }

    String hash = info.getParams().getOrDefault("hash", "").toString();

    if (CommonUtils.isEmptyOrNull(hash)) {
      err.setCode("500");
    } else {
      if (ActionType.AUTH == info.getActionType()) {
        String token = info.getParams().getOrDefault("token", "").toString();
        String computedHash = DigestUtils.md5Hex(String.format("%s%s", token, PASS_KEY));
        if (!hash.equals(computedHash)) {
          err.setCode("500");
        }
      } else if (ActionType.BALANCE == info.getActionType()) {
        String userId = info.getParams().getOrDefault("userId", "").toString();
        String computedHash = DigestUtils.md5Hex(String.format("%s%s", userId, PASS_KEY));
        if (!hash.equals(computedHash)) {
          err.setCode("500");
        }
      } else if (ActionType.CHANGE_BALANCE == info.getActionType()) {
        String userId = info.getParams().getOrDefault("userId", "").toString();
        String amount = info.getParams().getOrDefault("Amount", "").toString();
        String trnType = info.getParams().getOrDefault("TrnType", "").toString();
        String trnDescription = info.getParams().getOrDefault("TrnDescription", "").toString();
        String roundId = info.getParams().getOrDefault("roundId", "").toString();
        String gameId = info.getParams().getOrDefault("gameId", "").toString();
        String history = info.getParams().getOrDefault("History", "").toString();
        String computedHash =
            DigestUtils.md5Hex(
                String.format(
                    "%s%s%s%s%s%s%s%s",
                    userId, amount, trnType, trnDescription, roundId, gameId, history, PASS_KEY));
        if (!hash.equals(computedHash)) {
          err.setCode("500");
        }
      } else {
        err.setCode("399");
      }
    }

    if (!CommonUtils.isEmptyOrNull(err.getCode())) {
      response.setBody(CommonUtils.xmlToString(err));
      return Optional.of(response);
    }

    return Optional.empty();
  }

  MockResponse transaction(GenericRequestInfo info) {
    String userId = info.getParams().getOrDefault("userId", "").toString();
    BigDecimal amount = new BigDecimal(info.getParams().getOrDefault("Amount", "0").toString());
    String trnId = info.getParams().getOrDefault("TransactionId", "").toString();
    String trnType = info.getParams().getOrDefault("TrnType", "").toString();
    String trnDescription = info.getParams().getOrDefault("TrnDescription", "").toString();
    String roundId = info.getParams().getOrDefault("roundId", "").toString();
    String gameId = info.getParams().getOrDefault("gameId", "").toString();
    String history = info.getParams().getOrDefault("History", "").toString();
    String sessionId = info.getParams().getOrDefault("sessionId", "").toString();
    Boolean isRoundFinished =
        new Boolean(info.getParams().getOrDefault("isRoundFinished", "false").toString());

    VgsSystemChangeBalanceResponse response = new VgsSystemChangeBalanceResponse();
    ChangeBalanceRequest txRequest = new ChangeBalanceRequest();
    txRequest.setUserId(userId);
    txRequest.setAmount(amount);
    txRequest.setTransactionId(trnId);
    txRequest.setTransactionType(trnType);
    txRequest.setGameId(gameId);
    txRequest.setRoundId(roundId);
    txRequest.setTransactionDescription(trnDescription);
    txRequest.setHistory(history);
    txRequest.setRoundFinished(isRoundFinished);
    txRequest.setHash(info.getHash());

    response.setTime(new Date());
    response.setRequest(txRequest);
    response.setResponse(new ChangeBalanceResponse());

    if (idToToken.containsKey(userId)) {
      String token = idToToken.get(userId);
      MockPlayer player = tokenToPlayer.get(token);
      String category = "BET";

      if ("BET".equals(trnType)) {
        category = "BET";
      } else if ("WIN".equals(trnType)) {
        category = "WIN";
      } else if ("CANCELED_BET".equals(trnType)) {
        category = "REFUND";
      } else {
        return error(response, "399");
      }

      if (!txIdToTx.containsKey(trnId)) {
        MockTransaction tx = new MockTransaction();
        tx.setAmount(amount);
        tx.setId(trnId);
        tx.setCategory(category);
        txIdToTx.put(tx.getId(), tx);

        if ("BET".equals(category)) {
          if (player.getTotalBalance().compareTo(tx.getAmount()) < 0) {
            return error(response, "300");
          }
          substract(player, tx.getAmount());
        } else {
          add(player, tx.getAmount());
        }
      }

      response.response().setResult("OK");
      ((ChangeBalanceResponse) response.response()).setEcSystemTransactionId(trnId);
      ((ChangeBalanceResponse) response.response()).setBalance(player.getTotalBalance());

      return response(response);
    }

    return error(response, "400");
  }

  MockResponse balance(GenericRequestInfo info) {
    String userId = info.getParams().getOrDefault("userId", "").toString();
    VgsSystemGetBalanceResponse response = new VgsSystemGetBalanceResponse();
    GetBalanceRequest balanceRequest = new GetBalanceRequest();
    balanceRequest.setHash(info.getHash());
    balanceRequest.setUserId(userId);

    response.setTime(new Date());
    response.setRequest(balanceRequest);
    response.setResponse(new GetBalanceResponse());

    if (idToToken.containsKey(userId)) {
      String token = idToToken.get(userId);
      MockPlayer player = tokenToPlayer.get(token);

      response.getResponse().setResult("OK");
      response.getResponse().setBalance(player.getTotalBalance());

      return response(response);
    }

    return error(response, "400");
  }

  MockResponse authenticate(GenericRequestInfo info) {
    String launchToken = info.getParams().getOrDefault("token", "").toString();
    VgsSystemAuthResponse response = new VgsSystemAuthResponse();
    AuthRequest authRequest = new AuthRequest();
    authRequest.setToken(launchToken);
    authRequest.setHash(info.getHash());

    response.setTime(new Date());
    response.setRequest(authRequest);
    response.setResponse(new AuthResponse());

    if (launchTokenToToken.containsKey(launchToken)) {
      String token = launchTokenToToken.get(launchToken);
      MockPlayer player = tokenToPlayer.get(token);

      response.getResponse().setResult("OK");
      response.getResponse().setUserId(player.getUserId());
      response.getResponse().setUsername(player.getUsername());
      response.getResponse().setFirstname(player.getUsername() + "-first");
      response.getResponse().setLastname(player.getUsername() + "-last");
      response.getResponse().setEmail("email@email.com");
      response.getResponse().setCurrency(player.getCurrency());
      response.getResponse().setBalance(player.getTotalBalance());
      response.getResponse().setGameSessionId(UUID.randomUUID().toString());

      return response(response);
    }

    return error(response, "400");
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
   * Simple & generic error handling.
   *
   * @param response
   * @param code
   * @return
   */
  MockResponse error(VgsSystemResponse response, String code) {
    response.response().setResult("FAILED");
    response.response().setCode(code);
    return response(response);
  }

  /**
   * @param result
   * @return
   */
  MockResponse response(Object result) {
    MockResponse response = new MockResponse();
    response.setHeader("Content-Type", "application/xml");
    response.setBody(CommonUtils.xmlToString(result));
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
