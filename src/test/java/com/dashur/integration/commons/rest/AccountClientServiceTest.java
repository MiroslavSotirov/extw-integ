package com.dashur.integration.commons.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.*;
import com.dashur.integration.commons.testhelpers.*;
import com.dashur.integration.commons.utils.CommonUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import java.util.UUID;
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
public class AccountClientServiceTest {
  @Inject @RestClient AuthClientService authClientService;

  @Inject @RestClient AccountClientService accountClientService;

  @Inject CommonsConfig config;

  public BackendServer backendServer;

  @Inject TestHelperAssistService assistService;

  @BeforeClass
  public void init() {
    backendServer.resetQueue();
  }

  @Test
  public void testMemberAccountBalance() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();
    backendServer.accountBalanceRs();

    AuthClientTestService.Token token = assistService.loginCompany();
    String refreshToken = assistService.memberShortTokenByLaunchItem(token);
    String accessToken = assistService.refreshToken(refreshToken).getAccessToken();
    String authorization = CommonUtils.authorizationBearer(accessToken);
    AccountBalanceModel accountBalanceModel =
        accountClientService.balance(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG);
    log.info("AccountClientServiceTest.testMemberAccountBalance() => {}", accountBalanceModel);
    assertThat(accountBalanceModel, is(IsNull.notNullValue()));
  }

  @Test
  public void testMemberAccount() {
    backendServer.authAppClientLoginRs();
    backendServer.accountRs();

    TokenModel token =
        authClientService.loginAppClient(
            CommonUtils.authorizationBasic(
                TestHelperAssistService.GNRC_INTEG_APP_CRED.getId(),
                TestHelperAssistService.GNRC_INTEG_APP_CRED.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            "client_credentials",
            TestHelperAssistService.GNRC_INTEG_APP_CRED.getId(),
            TestHelperAssistService.GNRC_INTEG_APP_CRED.getPassword());
    assertThat(token, is(IsNull.notNullValue()));
    assertThat(token.getAccessToken(), is(IsNull.notNullValue()));

    String authorization = CommonUtils.authorizationBearer(token.getAccessToken());

    RestResponseWrapperModel<SimpleAccountModel> accountModel =
        accountClientService.account(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            TestHelperAssistService.HDTestHO_CO1_M01_ACCOUNT.getAccountId());
    assertThat(accountModel, is(IsNull.notNullValue()));
    assertThat(
        accountModel.getData().getId(),
        is(TestHelperAssistService.HDTestHO_CO1_M01_ACCOUNT.getAccountId()));
    log.info("AccountClientServiceTest.accountModel => {}", accountModel);
  }

  @Test
  public void testMemberAccountAppSettings() {
    backendServer.authAppClientLoginRs();
    backendServer.accountAppSettingsRs();

    TokenModel token =
        authClientService.loginAppClient(
            CommonUtils.authorizationBasic(
                TestHelperAssistService.GNRC_INTEG_APP_CRED.getId(),
                TestHelperAssistService.GNRC_INTEG_APP_CRED.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            "client_credentials",
            TestHelperAssistService.GNRC_INTEG_APP_CRED.getId(),
            TestHelperAssistService.GNRC_INTEG_APP_CRED.getPassword());
    assertThat(token, is(IsNull.notNullValue()));
    assertThat(token.getAccessToken(), is(IsNull.notNullValue()));

    String authorization = CommonUtils.authorizationBearer(token.getAccessToken());

    RestResponseWrapperModel<List<SimplifyAccountAppSettingsModel>> model =
        accountClientService.accountAppSettings(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            TestHelperAssistService.HDTestHO_CO1_M01_ACCOUNT.getAccountId());
    assertThat(model, is(IsNull.notNullValue()));
    assertThat(model.getData().size() > 0, is(Boolean.TRUE));
    log.info("AccountClientServiceTest.accountAppSettings => {}", model);
  }
}
