package com.dashur.integration.commons.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.auth.GrantType;
import com.dashur.integration.commons.rest.model.AppIdModel;
import com.dashur.integration.commons.rest.model.TokenModel;
import com.dashur.integration.commons.testhelpers.*;
import com.dashur.integration.commons.utils.CommonUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(BackendServer.class)
@QuarkusTestResource(RedisResource.class)
public class AuthClientServiceTest {
  @Inject @RestClient AuthClientService authClientService;

  @Inject @RestClient AuthClientTestService authClientTestService;

  @Inject @RestClient LauncherTestService launcherTestService;

  @Inject TestHelperAssistService assistService;

  public BackendServer backendServer;

  @BeforeClass
  public void init() {
    backendServer.resetQueue();
  }

  @Test
  public void testAuthAppId() {
    backendServer.authAppId();

    AppIdModel model = authClientService.authAppId();
    log.info("model:=>{}", model);
    assertThat(model, is(IsNull.notNullValue()));
    assertThat(model.getTrustedFacets().get(0).getVersion().getMajor(), is(1));
    assertThat(model.getTrustedFacets().get(0).getVersion().getMinor(), is(0));
  }

  @Test
  public void testCompanyLogin() {
    backendServer.authCompanyLoginRs();

    AuthClientTestService.Token token =
        authClientTestService.loginPassword(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.COMPANY_APP.getId(),
                TestHelperAssistService.COMPANY_APP.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_PASSWORD,
            TestHelperAssistService.COMPANY_APP_API.getId(),
            TestHelperAssistService.COMPANY_APP_API.getPassword());
    log.info("AuthClientServiceTest.testCompanyLogin() => {}", token);
    assertThat(token, is(IsNull.notNullValue()));
  }

  @Test
  public void testLauncher() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    LauncherTestService.LaunchReq launchReq = new LauncherTestService.LaunchReq();
    launchReq.setMbrExtRef(TestHelperAssistService.COMPANY_MEMBER.getId());
    launchReq.setGameAppId(TestHelperAssistService.GAME_APPS_ID);
    launchReq.setGameItemId(TestHelperAssistService.GAME_ITEM_ID);

    String launchRes = null;

    try {
      launchRes =
          launcherTestService.launchItem(
              AuthClientTestService.Utils.bearer(coAppToken.getAccessToken()),
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              launchReq);
    } catch (WebApplicationException e) {
      log.error("Unable to launch item => {}", e.getResponse().readEntity(String.class));
    }

    log.info("AuthClientServiceTest.testLauncher() => {}", launchRes);
    assertThat(launchRes, is(IsNull.notNullValue()));
  }

  @Test
  public void testAuthClientServiceRefreshToken() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.authMemberRefreshTokenRs();

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    assertThat(coAppToken, is(IsNull.notNullValue()));

    String memberShortRefreshToken = assistService.memberShortTokenByLaunchItem(coAppToken);
    assertThat(memberShortRefreshToken, is(IsNull.notNullValue()));
    log.info("memberShortRefreshToken => {}", memberShortRefreshToken);

    TokenModel token =
        authClientService.refreshToken(
            CommonUtils.authorizationBasic(
                TestHelperAssistService.INTEG_APP_CRED.getId(),
                TestHelperAssistService.INTEG_APP_CRED.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            GrantType.REFRESH_TOKEN.toString(),
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            memberShortRefreshToken);

    log.info("AuthClientServiceTest.testAuthClientServiceRefreshToken().token :=> {}", token);
    assertThat(token, is(IsNull.notNullValue()));
    assertThat(token.getAccessToken(), is(IsNull.notNullValue()));
    assertThat(token.getRefreshToken(), is(IsNull.notNullValue()));
  }

  @Test
  public void testAuthClientServiceRefreshTokenError1() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":400,\"message\":\"Error\"}}"));

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    assertThat(coAppToken, is(IsNull.notNullValue()));

    String memberShortRefreshToken = assistService.memberShortTokenByLaunchItem(coAppToken);
    assertThat(memberShortRefreshToken, is(IsNull.notNullValue()));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientService.refreshToken(
                    CommonUtils.authorizationBasic(
                        TestHelperAssistService.INTEG_APP_CRED.getId(),
                        TestHelperAssistService.INTEG_APP_CRED.getPassword() + "abc"),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    GrantType.REFRESH_TOKEN.toString(),
                    TestHelperAssistService.INTEG_APP_CRED.getId(),
                    memberShortRefreshToken));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError1() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError1().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceRefreshTokenError2() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":400,\"message\":\"Error\"}}"));

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    assertThat(coAppToken, is(IsNull.notNullValue()));

    String memberShortRefreshToken = assistService.memberShortTokenByLaunchItem(coAppToken);
    assertThat(memberShortRefreshToken, is(IsNull.notNullValue()));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientService.refreshToken(
                    CommonUtils.authorizationBasic(
                        TestHelperAssistService.INTEG_APP_CRED.getId() + "abc",
                        TestHelperAssistService.INTEG_APP_CRED.getPassword()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    GrantType.REFRESH_TOKEN.toString(),
                    TestHelperAssistService.INTEG_APP_CRED.getId(),
                    memberShortRefreshToken));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError2() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError2().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceRefreshTokenError3() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":400,\"message\":\"Error\"}}"));

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    assertThat(coAppToken, is(IsNull.notNullValue()));

    String memberShortRefreshToken = assistService.memberShortTokenByLaunchItem(coAppToken);
    assertThat(memberShortRefreshToken, is(IsNull.notNullValue()));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientService.refreshToken(
                    CommonUtils.authorizationBasic(
                        TestHelperAssistService.INTEG_APP_CRED.getId(),
                        TestHelperAssistService.INTEG_APP_CRED.getPassword()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    GrantType.REFRESH_TOKEN.toString(),
                    TestHelperAssistService.INTEG_APP_CRED.getId() + "abc",
                    memberShortRefreshToken));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError3() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError3().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceRefreshTokenError4() {
    backendServer.authCompanyLoginRs();
    backendServer.launcherRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":400,\"message\":\"Error\"}}"));

    AuthClientTestService.Token coAppToken = assistService.loginCompany();
    assertThat(coAppToken, is(IsNull.notNullValue()));

    String memberShortRefreshToken = assistService.memberShortTokenByLaunchItem(coAppToken);
    assertThat(memberShortRefreshToken, is(IsNull.notNullValue()));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientService.refreshToken(
                    CommonUtils.authorizationBasic(
                        TestHelperAssistService.INTEG_APP_CRED.getId(),
                        TestHelperAssistService.INTEG_APP_CRED.getPassword()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    GrantType.REFRESH_TOKEN.toString(),
                    TestHelperAssistService.INTEG_APP_CRED.getId(),
                    memberShortRefreshToken + "abcd"));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError4() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceRefreshTokenError4().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceLoginAppClient() {
    backendServer.authAppClientLoginRs();

    AuthClientTestService.Token appToken =
        authClientTestService.loginClientCredentials(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());

    log.info("AuthClientServiceTest.testAuthClientServiceLoginAppClient() => {}", appToken);
    assertThat(appToken, is(IsNull.notNullValue()));
  }

  @Test
  public void testAuthClientServiceLoginAppClientError1() {
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"15786f73-2a52-4b3a-8f28-841d249c0988\",\"processing_time\":-1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":401,\"message\":\"Authentication failed\"}}"));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientTestService.loginClientCredentials(
                    AuthClientTestService.Utils.auth(
                        TestHelperAssistService.INTEG_APP_CRED_2.getId() + "abc",
                        TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
                    TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                    TestHelperAssistService.INTEG_APP_CRED_2.getPassword()));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError1() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError1().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceLoginAppClientError2() {
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"15786f73-2a52-4b3a-8f28-841d249c0988\",\"processing_time\":-1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":401,\"message\":\"Authentication failed\"}}"));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientTestService.loginClientCredentials(
                    AuthClientTestService.Utils.auth(
                        TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                        TestHelperAssistService.INTEG_APP_CRED_2.getPassword() + "abc"),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
                    TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                    TestHelperAssistService.INTEG_APP_CRED_2.getPassword()));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError2() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError2().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceLoginAppClientError3() {
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"15786f73-2a52-4b3a-8f28-841d249c0988\",\"processing_time\":-1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":401,\"message\":\"Authentication failed\"}}"));

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientTestService.loginClientCredentials(
                    AuthClientTestService.Utils.auth(
                        TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                        TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
                    TestHelperAssistService.INTEG_APP_CRED_2.getId() + "abc",
                    TestHelperAssistService.INTEG_APP_CRED_2.getPassword()));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError3() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAppClientError3().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceLoginAsMember() {
    backendServer.authAppClientLoginRs();
    backendServer.authMemberLoginRs();

    AuthClientTestService.Token appToken =
        authClientTestService.loginClientCredentials(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());
    assertThat(appToken, is(IsNull.notNullValue()));

    AuthClientTestService.LoginMemberModel loginMemberModel =
        new AuthClientTestService.LoginMemberModel();
    loginMemberModel.setUserId(TestHelperAssistService.COMPANY_MEMBER_ACCOUNT.getUserId());

    AuthClientTestService.MemberToken memberToken =
        authClientTestService.loginAsMember(
            AuthClientTestService.Utils.bearer(appToken.getAccessToken()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            loginMemberModel);
    log.info("AuthClientServiceTest.testAuthClientServiceLoginAsMember() => {}", memberToken);
    assertThat(memberToken, is(IsNull.notNullValue()));
  }

  @Test
  public void testAuthClientServiceLoginAsMemberError1() {
    backendServer.authAppClientLoginRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"6f7c0380-b10b-482a-9131-8e01a6c89c6d\",\"processing_time\":-1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":400,\"message\":\"Cannot convert access token to JSON\"}}"));

    AuthClientTestService.Token appToken =
        authClientTestService.loginClientCredentials(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());
    assertThat(appToken, is(IsNull.notNullValue()));

    AuthClientTestService.LoginMemberModel loginMemberModel =
        new AuthClientTestService.LoginMemberModel();
    loginMemberModel.setUserId(TestHelperAssistService.COMPANY_MEMBER_ACCOUNT.getUserId());

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientTestService.loginAsMember(
                    AuthClientTestService.Utils.bearer(appToken.getAccessToken() + "abc"),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    loginMemberModel));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(401));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAsMemberError1() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAsMemberError1().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceLoginAsMemberError2() {
    backendServer.authAppClientLoginRs();
    backendServer.put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_NOT_FOUND)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"104b359b-8565-44af-9c4a-f3e478fd5828\",\"processing_time\":5},\"error\":{\"type\":\"ENTITY_NOT_FOUND\",\"code\":-20,\"message\":\"Entity not found for User(2,354,708)\"}}"));

    AuthClientTestService.Token appToken =
        authClientTestService.loginClientCredentials(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());
    assertThat(appToken, is(IsNull.notNullValue()));

    AuthClientTestService.LoginMemberModel loginMemberModel =
        new AuthClientTestService.LoginMemberModel();
    loginMemberModel.setUserId(TestHelperAssistService.COMPANY_MEMBER_ACCOUNT.getUserId() + 1);

    WebApplicationException errResp =
        assertThrows(
            WebApplicationException.class,
            () ->
                authClientTestService.loginAsMember(
                    AuthClientTestService.Utils.bearer(appToken.getAccessToken()),
                    Constant.REST_HEADER_VALUE_DEFAULT_TZ,
                    Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
                    UUID.randomUUID().toString(),
                    Constant.REST_HEADER_VALUE_DEFAULT_LANG,
                    loginMemberModel));
    assertThat(errResp, is(IsNull.notNullValue()));
    assertThat(errResp.getResponse().getStatus(), is(404));
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAsMemberError2() => {}",
        errResp.getResponse());
    log.info(
        "AuthClientServiceTest.testAuthClientServiceLoginAsMemberError2().hasEntity => {}",
        errResp.getResponse().readEntity(String.class));
  }

  @Test
  public void testAuthClientServiceAppRefreshToken() {
    backendServer.authAppClientLoginRs();
    backendServer.authMemberLoginRs();
    backendServer.authMemberRefreshTokenRs();

    AuthClientTestService.Token appToken =
        authClientTestService.loginClientCredentials(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED_2.getId(),
                TestHelperAssistService.INTEG_APP_CRED_2.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            Constant.REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS,
            TestHelperAssistService.INTEG_APP_CRED_2.getId(),
            TestHelperAssistService.INTEG_APP_CRED_2.getPassword());
    assertThat(appToken, is(IsNull.notNullValue()));

    AuthClientTestService.LoginMemberModel loginMemberModel =
        new AuthClientTestService.LoginMemberModel();
    loginMemberModel.setUserId(TestHelperAssistService.COMPANY_MEMBER_ACCOUNT.getUserId());

    AuthClientTestService.MemberToken memberToken =
        authClientTestService.loginAsMember(
            AuthClientTestService.Utils.bearer(appToken.getAccessToken()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            loginMemberModel);
    assertThat(memberToken, is(IsNull.notNullValue()));

    TokenModel appRefreshToken =
        authClientService.refreshToken(
            AuthClientTestService.Utils.auth(
                TestHelperAssistService.INTEG_APP_CRED.getId(),
                TestHelperAssistService.INTEG_APP_CRED.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            GrantType.REFRESH_TOKEN.toString(),
            TestHelperAssistService.INTEG_APP_CRED.getId(),
            memberToken.getTokenData().getRefreshToken());

    log.info(
        "AuthClientServiceTest.testAuthClientServiceAppRefreshToken().token :=> {}",
        appRefreshToken);
    assertThat(appRefreshToken, is(IsNull.notNullValue()));
    assertThat(appRefreshToken.getAccessToken(), is(IsNull.notNullValue()));
    assertThat(appRefreshToken.getRefreshToken(), is(IsNull.notNullValue()));
  }
}
