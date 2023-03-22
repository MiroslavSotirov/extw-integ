package com.dashur.integration.commons.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.auth.HashToken;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.domain.model.AccountBalance;
import com.dashur.integration.commons.domain.model.Transaction;
import com.dashur.integration.commons.domain.model.TransactionCreateRequest;
import com.dashur.integration.commons.rest.AccountClientService;
import com.dashur.integration.commons.rest.TransactionClientService;
import com.dashur.integration.commons.testhelpers.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.*;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(BackendServer.class)
@QuarkusTestResource(RedisResource.class)
public class DomainServiceTest {
  @Inject TestHelperAssistService assistService;

  @Inject @RestClient TransactionClientService transactionClientService;

  @Inject @RestClient AccountClientService accountClientService;

  @Inject DomainService domainService;

  public BackendServer backendServer;

  @BeforeClass
  public void init() {
    backendServer.resetQueue();
  }

  @Test
  public void testDomainServiceRefreshToken() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken =
        domainService.refreshToken(
            RequestContext.instance(),
            refreshToken,
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            TestHelperAssistService.INTEG_APP_CRED.getPassword());
    assertThat(refreshedToken, is(IsNull.notNullValue()));
  }

  @Test
  public void testDomainServiceLoginAppClient() {
    backendServer.authAppClientLoginRs();

    Token appToken =
        domainService.loginAppClient(
            RequestContext.instance(),
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());
    assertThat(appToken, is(IsNull.notNullValue()));
  }

  @Test
  public void testDomainServiceLoginAsMember() {
    backendServer.authAppClientLoginRs();
    backendServer.authMemberLoginRs();

    Token appToken =
        domainService.loginAppClient(
            RequestContext.instance(),
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());

    Token memberToken =
        domainService.loginAsMember(
            RequestContext.instance().withAccessToken(appToken.getAccessToken()),
            TestHelperAssistService.COMPANY_MEMBER_ACCOUNT.getUserId());

    assertThat(memberToken, is(IsNull.notNullValue()));
  }

  @Test
  public void testGetAccountBalance() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken =
        domainService.refreshToken(
            RequestContext.instance(),
            refreshToken,
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            TestHelperAssistService.INTEG_APP_CRED.getPassword());

    RequestContext ctx =
        RequestContext.instance()
            .withAccessToken(refreshedToken.getAccessToken())
            .withCurrency(Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY)
            .withTimezone(Constant.REST_HEADER_VALUE_DEFAULT_TZ)
            .withLanguage(Constant.REST_HEADER_VALUE_DEFAULT_LANG);

    AccountBalance bal = domainService.getAccountBalance(ctx);
    assertThat(bal, is(IsNull.notNullValue()));
  }

  @Test
  public void testCreateTransaction() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken =
        domainService.refreshToken(
            RequestContext.instance(),
            refreshToken,
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            TestHelperAssistService.INTEG_APP_CRED.getPassword());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());

    RequestContext ctx =
        RequestContext.instance()
            .withAccessToken(refreshedToken.getAccessToken())
            .withCurrency(Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY)
            .withTimezone(Constant.REST_HEADER_VALUE_DEFAULT_TZ)
            .withLanguage(Constant.REST_HEADER_VALUE_DEFAULT_LANG);

    AccountBalance bal = domainService.getAccountBalance(ctx);

    String txId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
    metadata.put("session_id", sessionId);

    TransactionCreateRequest createTx =
        new TransactionCreateRequest(
            hashToken.getAccountId(),
            "WAGER",
            "",
            new TransactionCreateRequest.Money(bal.getCurrency(), new BigDecimal(10)),
            txId,
            TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
            new TransactionCreateRequest.Metadata(roundId, metadata));

    List<TransactionCreateRequest> createTxs = new ArrayList<>();
    createTxs.add(createTx);

    List<Transaction> txes = domainService.createTransaction(ctx, createTxs);
    assertThat(txes, is(IsNull.notNullValue()));
    assertThat(txes.size(), is(1));
  }

  @Test
  public void testCreateTransaction2() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();
    backendServer.txRs();
    backendServer.txRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    Token refreshedToken =
        domainService.refreshToken(
            RequestContext.instance(),
            refreshToken,
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            TestHelperAssistService.INTEG_APP_CRED.getPassword());
    HashToken hashToken =
        HashToken.fromRefreshToken("1", Locale.ENGLISH, refreshedToken.getRefreshToken());

    RequestContext ctx =
        RequestContext.instance()
            .withAccessToken(refreshedToken.getAccessToken())
            .withCurrency(Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY)
            .withTimezone(Constant.REST_HEADER_VALUE_DEFAULT_TZ)
            .withLanguage(Constant.REST_HEADER_VALUE_DEFAULT_LANG)
            .withHashToken(hashToken)
            .withAccountId(hashToken.getAccountId())
            .withUserId(hashToken.getUserId());

    AccountBalance bal = domainService.getAccountBalance(ctx);

    String txId = UUID.randomUUID().toString();
    String roundId = UUID.randomUUID().toString();
    String sessionId = UUID.randomUUID().toString();

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("game_id", TestHelperAssistService.GAME_ITEM_EXTERNAL_ID);
    metadata.put("session_id", sessionId);

    TransactionCreateRequest createTx =
        new TransactionCreateRequest(
            hashToken.getAccountId(),
            "WAGER",
            "",
            new TransactionCreateRequest.Money(bal.getCurrency(), new BigDecimal(10)),
            txId,
            TestHelperAssistService.GAME_ITEM_EXTERNAL_ID,
            new TransactionCreateRequest.Metadata(roundId, metadata));

    List<TransactionCreateRequest> createTxs = new ArrayList<>();
    createTxs.add(createTx);
    List<Transaction> txes = domainService.createTransaction(ctx, createTxs);
    assertThat(txes, is(IsNull.notNullValue()));
    assertThat(txes.size(), is(1));

    List<Transaction> txes2 = domainService.createTransaction(ctx, createTxs);
    assertThat(txes2, is(IsNull.notNullValue()));
    assertThat(txes2.size(), is(1));
    assertThat(txes.get(0).getId(), is(txes2.get(0).getId()));
  }
}
