package com.dashur.integration.extw.connectors.qt;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.qt.data.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;

/** EveryMatrix Dispatcher */
@Slf4j
public class QtDispatcher extends Dispatcher {
  public static final String PASS_KEY = "testpassword";
  public static final String PROVIDER_ID = "90043efb-53bd-40c4-b0ba-d6660775fddc";
  public static final String TOKEN_2976011 = UUID.randomUUID().toString();
  public static final String TOKEN_2976012 = UUID.randomUUID().toString();
  public static final String TOKEN_2976013 = UUID.randomUUID().toString();

  private static final Object LOCK = new Object();
  private static QtDispatcher instance = null;

  public static final QtDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new QtDispatcher();
        }
      }
    }

    return instance;
  }

  private Map<String, String> launchTokenToToken = new HashMap<>();
  private Map<String, MockPlayer> tokenToPlayer = new HashMap<>();
  private Map<String, MockTransaction> txIdToTx = new HashMap<>();

  /** constructor. */
  public QtDispatcher() {
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
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    MockResponse result = null;
    String body = request.getBody().readUtf8();
    log.info("body: [{}]", body);
    GenericRequestInfo info = parse(request, body);
    Optional<MockResponse> validateResponse = validateAndReturnIfError(info);

    if (!validateResponse.isPresent()) {
      if (ActionType.AUTH == info.getActionType()) {
        AuthRequest rq = CommonUtils.jsonRead(AuthRequest.class, info.getBody());
        result = authenticate(info, rq);
      } else if (ActionType.BALANCE == info.getActionType()) {
        result = balance(info);
      } else if (ActionType.WITHDRAWAL == info.getActionType()) {
        WithdrawalRequest rq = CommonUtils.jsonRead(WithdrawalRequest.class, info.getBody());
        result = withdrawal(info, rq);
      } else if (ActionType.DEPOSIT == info.getActionType()) {
        DepositRequest rq = CommonUtils.jsonRead(DepositRequest.class, info.getBody());
        result = deposit(info, rq);
      } else if (ActionType.ROLLBACK == info.getActionType()) {
        RollbackRequest rq = CommonUtils.jsonRead(RollbackRequest.class, info.getBody());
        result = cancel(info, rq);
      } else {

      }
    } else {
      result = validateResponse.get();
    }

    if (Objects.nonNull(result)) {
      return result;
    }

    return null;
  }

  static enum ActionType {
    AUTH,
    BALANCE,
    WITHDRAWAL,
    DEPOSIT,
    ROLLBACK;
  }

  @Data
  static class GenericRequestInfo {
    private String passKey;
    private String sessionToken;
    private String providerId;
    private String playerId;
    private ActionType actionType;
    private String body;
  }

  GenericRequestInfo parse(RecordedRequest request, String body) {
    GenericRequestInfo result = new GenericRequestInfo();

    result.setPassKey(request.getHeader("Pass-Key"));
    result.setSessionToken(request.getHeader("Session-Token"));
    String[] tokens = request.getPath().replace("/external/qt/providers/", "").split("/");

    if (isValidPath(request.getPath())) {
      result.setProviderId(tokens[0]);
    }

    if (request.getPath().endsWith("/token")) {
      result.setActionType(ActionType.AUTH);
    } else if (request.getPath().endsWith("/balance")) {
      result.setActionType(ActionType.BALANCE);
    } else if (request.getPath().endsWith("/withdrawal")) {
      result.setActionType(ActionType.WITHDRAWAL);
    } else if (request.getPath().endsWith("/deposit")) {
      result.setActionType(ActionType.DEPOSIT);
    } else if (request.getPath().endsWith("/rollback")) {
      result.setActionType(ActionType.ROLLBACK);
    }

    if (ActionType.BALANCE == result.getActionType()) {
      result.setPlayerId(tokens[2]);
    } else {
      result.setBody(body);
    }

    return result;
  }

  Boolean isValidPath(String path) {
    if (CommonUtils.isEmptyOrNull(path)) {
      return Boolean.FALSE;
    }

    if (path.endsWith("/token")
        || path.endsWith("/balance")
        || path.endsWith("/withdrawal")
        || path.endsWith("/deposit")
        || path.endsWith("/rollback")) {
      if (path.startsWith("/external/qt/providers")) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  Optional<MockResponse> validateAndReturnIfError(GenericRequestInfo info) {
    MockResponse response = new MockResponse();
    response.setHeader("Content-Type", "application/json; charset=utf-8");
    com.dashur.integration.extw.connectors.qt.data.Response err =
        new com.dashur.integration.extw.connectors.qt.data.Response();

    if (Objects.isNull(info)) {
      response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      err.setCode("UNKNOWN_ERROR");
      err.setMessage("Unable to parse GenericRequestInfo");
    } else if (CommonUtils.isEmptyOrNull(info.getPassKey())
        || !PASS_KEY.equals(info.getPassKey())) {
      response.setResponseCode(HttpStatus.SC_UNAUTHORIZED);
      err.setCode("LOGIN_FAILED");
      err.setMessage("The given pass-key is incorrect");
    } else if (CommonUtils.isEmptyOrNull(info.getSessionToken())
        || !launchTokenToToken.containsKey(info.getSessionToken())
        || !tokenToPlayer.containsKey(info.getSessionToken())) {
      response.setResponseCode(HttpStatus.SC_BAD_REQUEST);
      err.setCode("INVALID_TOKEN");
      err.setMessage("Missing, invalid or expired player (wallet) session token.");
    } else if (Objects.isNull(info.getActionType())) {
      response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      err.setCode("UNKNOWN_ERROR");
      err.setMessage("Unable to determine action-type");
    } else if (CommonUtils.isEmptyOrNull(info.getProviderId())
        || !PROVIDER_ID.equals(info.getProviderId())) {
      response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      err.setCode("UNKNOWN_ERROR");
      err.setMessage("Missing, invalid provider");
    } else if (CommonUtils.isEmptyOrNull(info.getBody())) {
      if (ActionType.BALANCE == info.getActionType()) {
        if (CommonUtils.isEmptyOrNull(info.getPlayerId())) {
          response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
          err.setCode("UNKNOWN_ERROR");
          err.setMessage("Missing, player id");
        }
      } else {
        response.setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        err.setCode("UNKNOWN_ERROR");
        err.setMessage("Missing, invalid body");
      }
    }

    if (!CommonUtils.isEmptyOrNull(err.getCode())) {
      response.setBody(CommonUtils.jsonToString(err));
      return Optional.of(response);
    }

    return Optional.empty();
  }

  MockResponse withdrawal(GenericRequestInfo info, WithdrawalRequest rq) {
    if (tokenToPlayer.containsKey(info.getSessionToken())) {
      MockPlayer player = tokenToPlayer.get(info.getSessionToken());

      if (!txIdToTx.containsKey(rq.getTxnId())) {
        MockTransaction tx = new MockTransaction();
        tx.setAmount(rq.getAmount());
        tx.setId(rq.getTxnId());
        tx.setCategory("BET");
        txIdToTx.put(tx.getId(), tx);

        if (player.getTotalBalance().compareTo(tx.getAmount()) < 0) {
          return error(400, "INSUFFICIENT_FUNDS", "unable to proceed, balance not enough");
        }
        substract(player, tx.getAmount());
      }

      WithdrawalResponse result = new WithdrawalResponse();
      result.setBalance(player.getTotalBalance());
      result.setReferenceId(rq.getTxnId());

      return response(result);
    }

    return error(400, "INVALID_TOKEN", "Token not found");
  }

  MockResponse deposit(GenericRequestInfo info, DepositRequest rq) {
    if (tokenToPlayer.containsKey(info.getSessionToken())) {
      if (!txIdToTx.containsKey(rq.getBetId())) {
        return error(400, "REQUEST_DECLINED", "original withdrawal request not found");
      } else {
        MockTransaction betTx = txIdToTx.get(rq.getBetId());
        if (!"BET".equals(betTx.getCategory())) {
          return error(400, "REQUEST_DECLINED", "original withdrawal request not found");
        }
      }
      MockPlayer player = tokenToPlayer.get(info.getSessionToken());

      if (!txIdToTx.containsKey(rq.getTxnId())) {
        MockTransaction tx = new MockTransaction();
        tx.setAmount(rq.getAmount());
        tx.setId(rq.getTxnId());
        tx.setCategory("WIN");
        tx.setIdRef(rq.getBetId());
        txIdToTx.put(tx.getId(), tx);
        add(player, tx.getAmount());
      }

      DepositResponse result = new DepositResponse();
      result.setBalance(player.getTotalBalance());
      result.setReferenceId(rq.getTxnId());

      return response(result);
    }

    return error(400, "INVALID_TOKEN", "Token not found");
  }

  MockResponse cancel(GenericRequestInfo info, RollbackRequest rq) {
    if (tokenToPlayer.containsKey(info.getSessionToken())) {
      //      if (!txIdToTx.containsKey(rq.getBetId())) {
      //        return error(400, "REQUEST_DECLINED", "original withdrawal request not found");
      //      }

      MockPlayer player = tokenToPlayer.get(info.getSessionToken());

      if (!txIdToTx.containsKey(rq.getTxnId())) {
        MockTransaction orgTx = txIdToTx.get(rq.getBetId());
        MockTransaction tx = new MockTransaction();
        tx.setId(rq.getTxnId());
        tx.setCategory("CANCEL");
        tx.setAmount(rq.getAmount());
        tx.setIdRef(rq.getBetId());
        txIdToTx.put(tx.getId(), tx);

        if (Objects.nonNull(orgTx)) {
          if (orgTx.getAmount().compareTo(rq.getAmount()) != 0) {
            return error(500, "UNKNOWN_ERROR", "amount mismatched");
          }

          if ("BET".equals(orgTx.getCategory())) { // refund a wager
            add(player, tx.getAmount());
          } else if ("WIN".equals(orgTx.getCategory())) { // refund a payout
            substract(player, tx.getAmount());
          } else {
            return error(500, "UNKNOWN_ERROR", "original request type not handle");
          }
        }
      }

      RollbackResponse result = new RollbackResponse();

      result.setBalance(player.getTotalBalance());
      result.setReferenceId(rq.getTxnId());

      return response(result);
    }

    return error(400, "INVALID_TOKEN", "Token not found");
  }

  MockResponse balance(GenericRequestInfo info) {
    if (tokenToPlayer.containsKey(info.getSessionToken())) {
      MockPlayer player = tokenToPlayer.get(info.getSessionToken());
      BalanceResponse result = new BalanceResponse();

      result.setCurrency(player.getCurrency());
      result.setBalance(player.getTotalBalance());

      return response(result);
    }

    return error(400, "INVALID_TOKEN", "Token not found");
  }

  MockResponse authenticate(GenericRequestInfo info, AuthRequest rq) {
    if (launchTokenToToken.containsKey(info.getSessionToken())) {
      String token = launchTokenToToken.get(info.getSessionToken());
      MockPlayer player = tokenToPlayer.get(token);

      AuthResponse result = new AuthResponse();

      result.setPlayerId(player.getUserId());
      result.setScreenName(player.getUsername());
      result.setLanguage("en");
      result.setCountry(player.getCountry());
      result.setBalance(player.getTotalBalance());
      result.setCurrency(player.getCurrency());

      return response(result);
    }

    return error(400, "INVALID_TOKEN", "Token not found");
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
   * @param httpCode
   * @param code
   * @param description
   * @return
   */
  MockResponse error(Integer httpCode, String code, String description) {
    Response rs = new Response();

    rs.setCode(code);
    rs.setMessage(description);

    MockResponse response = response(rs);
    response.setResponseCode(httpCode);
    return response;
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
