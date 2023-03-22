package com.dashur.integration.commons.testhelpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.auth.GrantType;
import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.rest.AuthClientService;
import com.dashur.integration.commons.rest.model.TokenModel;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;

@Slf4j
@ApplicationScoped
public class TestHelperAssistService {
  public static final Credentials COMPANY_APP =
      new Credentials("HDTestHO_CO1_clientid", "HDTestHO_CO1");
  public static final Credentials COMPANY_APP_API =
      new Credentials("HDTestHO_CO1_api", "HDTestHO_CO1");
  public static final Credentials COMPANY_MEMBER =
      new Credentials("HDTestHO_CO1_M01", "HDTestHO_CO1_M01");
  public static final Credentials INTEG_APP_CRED =
      new Credentials("PNGDesktop_client_id", "PNGDesktop_client_password");
  public static final Credentials INTEG_APP_CRED_2 = new Credentials("integ_ne_t2", "v8(rPKZxXB@k");

  public static final Credentials GNRC_INTEG_APP_CRED =
      new Credentials("gnrc.api.t2", "b7FwChAzEXumMxTPC8QH2Zci");

  public static final AccountUser COMPANY_MEMBER_ACCOUNT = new AccountUser(2343353L, 2354707L);

  public static final AccountUser HDTestHO_CO1_M01_ACCOUNT = new AccountUser(2536547L, 2550198L);

  public static final String GAME_APPS_ID = "9114";
  public static final String GAME_ITEM_ID = "12135";
  public static final String GAME_ITEM_EXTERNAL_ID = "287"; // towerquest

  @Inject @RestClient AuthClientService authClientService;

  @Inject @RestClient AuthClientTestService authClientTestService;

  @Inject @RestClient LauncherTestService launcherTestService;

  @Inject CommonsConfig config;

  /** @return company app login token */
  public AuthClientTestService.Token loginCompany() {
    return authClientTestService.loginPassword(
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
  }

  /**
   * @param coAppToken
   * @return member refresh token, return by launch service.
   */
  public String memberShortTokenByLaunchItem(AuthClientTestService.Token coAppToken) {
    LauncherTestService.LaunchReq launchReq = new LauncherTestService.LaunchReq();
    launchReq.setMbrExtRef(TestHelperAssistService.COMPANY_MEMBER.getId());
    launchReq.setGameAppId(TestHelperAssistService.GAME_APPS_ID);
    launchReq.setGameItemId(TestHelperAssistService.GAME_ITEM_ID);

    String launchRes = null;

    try {
      launchRes =
          launcherTestService.launchItem(
              CommonUtils.authorizationBearer(coAppToken.getAccessToken()),
              Constant.REST_HEADER_VALUE_DEFAULT_TZ,
              Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
              UUID.randomUUID().toString(),
              Constant.REST_HEADER_VALUE_DEFAULT_LANG,
              launchReq);
    } catch (WebApplicationException e) {
      log.error("Unable to launch item => {}", e.getResponse().readEntity(String.class));
    }

    assertThat(launchRes, is(IsNull.notNullValue()));
    String[] arrs = launchRes.split("&");

    for (String arr : arrs) {
      if (arr.startsWith("user=")) {
        return arr.replaceFirst("user=", "").replaceFirst("\"}", "");
      }
    }

    log.error("Unable to find authToken => {}", launchRes);
    throw new IllegalArgumentException("Unable to find authToken");
  }

  public Token refreshToken(String refreshToken) {
    TokenModel token =
        authClientService.refreshToken(
            CommonUtils.authorizationBasic(INTEG_APP_CRED.getId(), INTEG_APP_CRED.getPassword()),
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            GrantType.REFRESH_TOKEN.toString(),
            INTEG_APP_CRED.getId(),
            refreshToken);
    log.info("AuthClientServiceTest.testAuthClientServiceRefreshToken().token :=> {}", token);
    assertThat(token, is(IsNull.notNullValue()));
    assertThat(token.getAccessToken(), is(IsNull.notNullValue()));
    assertThat(token.getRefreshToken(), is(IsNull.notNullValue()));
    return new Token(token.getAccessToken(), token.getRefreshToken());
  }

  @Getter
  @AllArgsConstructor
  public static final class Credentials {
    private String id;
    private String password;
  }

  @Getter
  @AllArgsConstructor
  public static final class AccountUser {
    private Long accountId;
    private Long userId;
  }
}
