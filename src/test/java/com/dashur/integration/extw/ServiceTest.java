package com.dashur.integration.extw;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurAuthDispatcher;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixConfiguration;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixDispatcher;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixMockServer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(BackendServer.class)
@QuarkusTestResource(EveryMatrixMockServer.class)
public class ServiceTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_EVERYMATRIX;
  static final String LAUNCH_TOKEN = EveryMatrixDispatcher.LAUNCH_TOKEN_2976013;

  @Inject Service service;

  @Inject ExtwIntegConfiguration configuration;

  public BackendServer backendServer;

  public EveryMatrixMockServer everyMatrixServer;

  @BeforeEach
  public void b4Test() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(DashurAuthDispatcher.instance());
    dispatcher.register(DashurLauncherDispatcher.instance());
    dispatcher.register(DashurFeedTransactionDispatcher.instance());
    backendServer.resetQueue(dispatcher);

    dispatcher = new ServerDispatcher();
    dispatcher.register(new EveryMatrixDispatcher());
    everyMatrixServer.resetQueue(dispatcher);
  }

  @Test
  public void testLaunchUrl() {
    EveryMatrixConfiguration config =
        configuration.configuration(Constant.OPERATOR_EVERYMATRIX, EveryMatrixConfiguration.class);
    EveryMatrixConfiguration.CompanySetting setting =
        config.getCompanySettings().get(config.getDefaultCompanyId());

    String url =
        service.launchUrl(
            RequestContext.instance(),
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            setting.getLauncherItemApplicationId(),
            1000L,
            Boolean.FALSE,
            LAUNCH_TOKEN,
            null,
            null);
    assertThat(url, is(IsNull.notNullValue()));

    assertThrows(
        ApplicationException.class,
        () ->
            service.launchUrl(
                RequestContext.instance(),
                setting.getLauncherAppClientId(),
                setting.getLauncherAppClientCredential(),
                setting.getLauncherAppApiId(),
                setting.getLauncherAppApiCredential(),
                setting.getLauncherItemApplicationId(),
                1001L,
                Boolean.FALSE,
                LAUNCH_TOKEN,
                null,
                null));
  }

  @Test
  public void testPlaycheckURL() {
    EveryMatrixConfiguration config =
        configuration.configuration(Constant.OPERATOR_EVERYMATRIX, EveryMatrixConfiguration.class);
    EveryMatrixConfiguration.CompanySetting setting =
        config.getCompanySettings().get(config.getDefaultCompanyId());

    String url =
        service.playcheckUrl(
            RequestContext.instance(),
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential(),
            "123456789");
    assertThat(url, is(IsNull.notNullValue()));

    assertThrows(
        ApplicationException.class,
        () ->
            service.playcheckUrl(
                RequestContext.instance(),
                setting.getLauncherAppClientId(),
                setting.getLauncherAppClientCredential(),
                setting.getLauncherAppApiId(),
                setting.getLauncherAppApiCredential(),
                "123456"));
  }
}
