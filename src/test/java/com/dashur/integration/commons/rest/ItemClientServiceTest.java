package com.dashur.integration.commons.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.CommonsConfig;
import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.SimpleApplicationItemModel;
import com.dashur.integration.commons.rest.model.SimpleItemModel;
import com.dashur.integration.commons.rest.model.TokenModel;
import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.RedisResource;
import com.dashur.integration.commons.testhelpers.TestHelperAssistService;
import com.dashur.integration.commons.utils.CommonUtils;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
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
public class ItemClientServiceTest {
  @Inject @RestClient AuthClientService authClientService;

  @Inject @RestClient ItemClientService itemClientService;

  @Inject CommonsConfig config;

  public BackendServer backendServer;

  @BeforeClass
  public void init() {
    backendServer.resetQueue();
  }

  @Test
  public void testApplication() {
    backendServer.authAppClientLoginRs();
    backendServer.itemRs();

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

    RestResponseWrapperModel<SimpleItemModel> model =
        itemClientService.item(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            new Long(TestHelperAssistService.GAME_ITEM_ID));
    assertThat(model, is(IsNull.notNullValue()));
    assertThat(model.getData().getId(), is(new Long(TestHelperAssistService.GAME_ITEM_ID)));
    log.info("ItemClientServiceTest.model => {}", model);
  }

  @Test
  public void testAppItem() {
    backendServer.authAppClientLoginRs();
    backendServer.appItemRs();

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

    RestResponseWrapperModel<SimpleApplicationItemModel> model =
        itemClientService.appItem(
            authorization,
            Constant.REST_HEADER_VALUE_DEFAULT_TZ,
            Constant.REST_HEADER_VALUE_DEFAULT_CURRENCY,
            UUID.randomUUID().toString(),
            Constant.REST_HEADER_VALUE_DEFAULT_LANG,
            13563L);
    assertThat(model, is(IsNull.notNullValue()));
    assertThat(model.getData().getId(), is(13563L));
    log.info("AppItemClientServiceTest.model => {}", model);
  }
}
