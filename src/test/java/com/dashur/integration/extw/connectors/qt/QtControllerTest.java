package com.dashur.integration.extw.connectors.qt;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurAuthDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurDispatcherData;
import com.dashur.integration.extw.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
public class QtControllerTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_QTECH;
  static final String LAUNCH_TOKEN = QtDispatcher.TOKEN_2976013;

  @Inject Service service;

  @Inject ExtwIntegConfiguration configuration;

  private DashurDispatcherData data;

  public QtMockServer qtServer;
  public ServerDispatcher qtDispatcher;

  public BackendServer backendServer;
  public ServerDispatcher backendDispatcher;

  @BeforeEach
  public void setup() {
    backendDispatcher = new ServerDispatcher();
    backendDispatcher.register(DashurAuthDispatcher.instance());
    backendDispatcher.register(DashurLauncherDispatcher.instance());
    backendDispatcher.register(DashurFeedTransactionDispatcher.instance());
    backendServer.resetQueue(backendDispatcher);

    qtDispatcher = new ServerDispatcher();
    qtDispatcher.register(new QtDispatcher());
    qtServer.resetQueue(qtDispatcher);

    data = DashurDispatcherData.instance();
  }

  @Test
  public void testLaunchUrl() {
    Map<String, Object> params = new HashMap<>();
    params.put("gameId", "1000");
    params.put("language", "en_US");
    params.put("demo", Boolean.FALSE);
    params.put("token", LAUNCH_TOKEN);

    Response response = given().when().formParams(params).get("/v1/extw/exp/qt/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));
  }

  @Test
  public void testLaunchUrl2() {
    Map<String, Object> params = new HashMap<>();
    params.put("gameId", "1001");
    params.put("language", "en_US");
    params.put("demo", Boolean.FALSE);
    params.put("token", LAUNCH_TOKEN);

    Response response = given().when().formParams(params).get("/v1/extw/exp/qt/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  @Test
  public void testPlaycheckURL() {
    Map<String, Object> params = new HashMap<>();
    params.put("roundId", "123456789");
    params.put("userId", "12345678989");
    params.put("gameId", "1234");
    String hash =
        QtConnectorServiceImpl.Utils.hash(
            null,
            null,
            params.get("roundId").toString(),
            params.get("userId").toString(),
            params.get("gameId").toString(),
            ((QtConfiguration) configuration.configuration(OPERATOR_CODE)).getHashKey());
    params.put("hash", hash);

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/qt/game-state").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));
  }

  @Test
  public void testPlaycheckURL2() {
    Map<String, Object> params = new HashMap<>();
    params.put("roundId", "123456789123");
    params.put("userId", "12345678989");
    params.put("gameId", "1234");
    String hash =
        QtConnectorServiceImpl.Utils.hash(
            null,
            null,
            params.get("roundId").toString(),
            params.get("userId").toString(),
            params.get("gameId").toString(),
            ((QtConfiguration) configuration.configuration(OPERATOR_CODE)).getHashKey());
    params.put("hash", hash);

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/qt/game-state").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }
}
