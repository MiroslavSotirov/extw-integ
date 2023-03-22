package com.dashur.integration.commons.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.auth.HashToken;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.rest.model.TransactionCreateModel;
import com.dashur.integration.commons.rest.model.TransactionModel;
import com.dashur.integration.commons.testhelpers.*;
import com.dashur.integration.commons.utils.CommonUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(BackendServer.class)
@QuarkusTestResource(RedisResource.class)
public class TransactionClientServiceTest {
  @Inject CommonsConfig config;

  @Inject TestHelperAssistService assistService;

  @Inject @RestClient TransactionClientService transactionClientService;

  @Inject @RestClient AccountClientService accountClientService;

  public BackendServer backendServer;

  @BeforeClass
  public void init() {
    backendServer.resetQueue();
  }

  @Test
  public void testReserve() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken = assistService.refreshToken(refreshToken);
    String authorization = CommonUtils.authorizationBearer(refreshedToken.getAccessToken());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());
    String currency =
        accountClientService
            .balance(
                authorization,
                Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                UUID.randomUUID().toString(),
                Constant.REST_HEADER_VALUE_DEFAULT_LANG)
            .getData()
            .getCurrency();

    String txId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
    metadata.put("session_id", sessionId);

    TransactionCreateModel createModel =
        new TransactionCreateModel(
            hashToken.getAccountId(),
            "WAGER",
            "",
            new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10)),
            txId,
            TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
            new TransactionCreateModel.MetadataModel(roundId, metadata),
            null,
            null);

    log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

    TransactionModel model =
        transactionClientService.transaction(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            currency,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            new TransactionCreateModel[] {createModel});

    log.info("response :=> {}", model);

    assertThat(model, is(IsNull.notNullValue()));
    assertThat(model.getDatas(), is(IsNull.notNullValue()));
    assertThat(model.getDatas().size(), is(1));
    log.info("TransactionClientServiceTest.", model.getDatas().get(0));
  }

  @Test
  public void testReserveError() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txErrRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken = assistService.refreshToken(refreshToken);
    String authorization = CommonUtils.authorizationBearer(refreshedToken.getAccessToken());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());
    String currency =
        accountClientService
            .balance(
                authorization,
                Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                UUID.randomUUID().toString(),
                Constant.REST_HEADER_VALUE_DEFAULT_LANG)
            .getData()
            .getCurrency();

    String txId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
    metadata.put("session_id", sessionId);

    TransactionCreateModel createModel =
        new TransactionCreateModel(
            hashToken.getAccountId(),
            "WAGER",
            "",
            new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10000)),
            txId,
            TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
            new TransactionCreateModel.MetadataModel(roundId, metadata),
            null,
            null);

    log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

    Response err =
        assertThrows(
                WebApplicationException.class,
                () ->
                    transactionClientService.transaction(
                        authorization,
                        Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                        currency,
                        UUID.randomUUID().toString(),
                        Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                        new TransactionCreateModel[] {createModel}))
            .getResponse();

    log.info("err.response.status :=> {}", err.getStatus());
    log.info("err.response.body :=> {}", err.readEntity(String.class));
  }

  @Test
  public void testRelease() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txRs();
    backendServer.txRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken = assistService.refreshToken(refreshToken);
    String authorization = CommonUtils.authorizationBearer(refreshedToken.getAccessToken());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());
    String currency =
        accountClientService
            .balance(
                authorization,
                Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                UUID.randomUUID().toString(),
                Constant.REST_HEADER_VALUE_DEFAULT_LANG)
            .getData()
            .getCurrency();

    String txId = UUID.randomUUID().toString();
    String payoutTxId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
      metadata.put("session_id", sessionId);

      TransactionCreateModel createModel =
          new TransactionCreateModel(
              hashToken.getAccountId(),
              "WAGER",
              "",
              new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10)),
              txId,
              TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
              new TransactionCreateModel.MetadataModel(roundId, metadata),
              null,
              null);

      log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

      TransactionModel model =
          transactionClientService.transaction(
              authorization,
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              currency,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              new TransactionCreateModel[] {createModel});

      log.info("response :=> {}", model);

      assertThat(model, is(IsNull.notNullValue()));
      assertThat(model.getDatas(), is(IsNull.notNullValue()));
      assertThat(model.getDatas().size(), is(1));
      log.info("TransactionClientServiceTest.", model.getDatas().get(0));
    }

    {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
      metadata.put("session_id", sessionId);

      TransactionCreateModel createModel =
          new TransactionCreateModel(
              hashToken.getAccountId(),
              "PAYOUT",
              "",
              new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10)),
              payoutTxId,
              TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
              new TransactionCreateModel.MetadataModel(roundId, metadata),
              null,
              null);

      log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

      TransactionModel model =
          transactionClientService.transaction(
              authorization,
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              currency,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              new TransactionCreateModel[] {createModel});

      log.info("response :=> {}", model);

      assertThat(model, is(IsNull.notNullValue()));
      assertThat(model.getDatas(), is(IsNull.notNullValue()));
      assertThat(model.getDatas().size(), is(1));
      log.info("TransactionClientServiceTest.", model.getDatas().get(0));
    }
  }

  @Test
  public void testRefund() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txRs();
    backendServer.txRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken = assistService.refreshToken(refreshToken);
    String authorization = CommonUtils.authorizationBearer(refreshedToken.getAccessToken());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());
    String currency =
        accountClientService
            .balance(
                authorization,
                Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                UUID.randomUUID().toString(),
                Constant.REST_HEADER_VALUE_DEFAULT_LANG)
            .getData()
            .getCurrency();

    String txId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
      metadata.put("session_id", sessionId);

      TransactionCreateModel createModel =
          new TransactionCreateModel(
              hashToken.getAccountId(),
              "WAGER",
              "",
              new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10)),
              txId,
              TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
              new TransactionCreateModel.MetadataModel(roundId, metadata),
              null,
              null);

      log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

      TransactionModel model =
          transactionClientService.transaction(
              authorization,
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              currency,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              new TransactionCreateModel[] {createModel});

      log.info("response :=> {}", model);

      assertThat(model, is(IsNull.notNullValue()));
      assertThat(model.getDatas(), is(IsNull.notNullValue()));
      assertThat(model.getDatas().size(), is(1));
      log.info("TransactionClientServiceTest.", model.getDatas().get(0));
    }

    {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
      metadata.put("session_id", sessionId);

      TransactionCreateModel createModel =
          new TransactionCreateModel(
              hashToken.getAccountId(),
              "REFUND",
              "",
              new TransactionCreateModel.MoneyModel(currency, new BigDecimal(10)),
              txId,
              TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
              new TransactionCreateModel.MetadataModel(roundId, metadata),
              null,
              null);

      log.info("json create model :=> {}", CommonUtils.jsonToString(createModel));

      TransactionModel model =
          transactionClientService.transaction(
              authorization,
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              currency,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              new TransactionCreateModel[] {createModel});

      log.info("response :=> {}", model);

      assertThat(model, is(IsNull.notNullValue()));
      assertThat(model.getDatas(), is(IsNull.notNullValue()));
      assertThat(model.getDatas().size(), is(1));
      log.info("TransactionClientServiceTest.", model.getDatas().get(0));
    }
  }
}
