package com.dashur.integration.extw.connectors.parimatch;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurAuthDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurDispatcherData;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.DashurFeedTransactionDispatcher;
import com.dashur.integration.extw.DashurLauncherDispatcher;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
public class PariMatchControllerTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_PARIMATCH;
  static final String LAUNCH_TOKEN = PariMatchDispatcher.LAUNCH_TOKEN_2976013;

  private DashurDispatcherData data;

  public PariMatchMockServer pariMatchServer;
  public ServerDispatcher pariMatchDispatcher;

  public BackendServer backendServer;
  public ServerDispatcher backendDispatcher;

  @BeforeEach
  public void setup() {
    backendDispatcher = new ServerDispatcher();
    backendDispatcher.register(DashurAuthDispatcher.instance());
    backendDispatcher.register(DashurLauncherDispatcher.instance());
    backendDispatcher.register(DashurFeedTransactionDispatcher.instance());
    backendServer.resetQueue(backendDispatcher);

    pariMatchDispatcher = new ServerDispatcher();
    pariMatchDispatcher.register(new PariMatchDispatcher());
    pariMatchServer.resetQueue(pariMatchDispatcher);

    data = DashurDispatcherData.instance();
  }

  @Test
  public void testLaunch1() {
    Map<String, Object> params = new HashMap<>();
    params.put("cid", OPERATOR_CODE);
    params.put("productId", "1000");
    params.put("sessionToken", LAUNCH_TOKEN);
    params.put("lang", "en");
    params.put("targetChannel", "desktop");
    params.put("consumerId", "maverick");

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/parimatch/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));
  }

  @Test
  public void testLaunch2() {
    Map<String, Object> params = new HashMap<>();
    params.put("cid", OPERATOR_CODE);
    params.put("productId", "1001");
    params.put("sessionToken", LAUNCH_TOKEN);
    params.put("lang", "en");
    params.put("targetChannel", "desktop");
    params.put("consumerId", "maverick");

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/parimatch/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  @Test
  public void testLaunch3() {
    Map<String, Object> params = new HashMap<>();
    params.put("cid", Constant.OPERATOR_EVERYMATRIX);
    params.put("productId", "1000");
    params.put("sessionToken", LAUNCH_TOKEN);
    params.put("lang", "en");
    params.put("targetChannel", "desktop");
    params.put("consumerId", "maverick");

    // Invalid casino-id
    Response response =
        given().when().formParams(params).get("/v1/extw/exp/parimatch/launch").andReturn();
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));
    log.info("Error result : [{}]", response.getBody().prettyPrint());

    params.put("cid", OPERATOR_CODE);
    params.put("consumerId", OPERATOR_CODE);

    // Invalid consumer-id
    response = given().when().formParams(params).get("/v1/extw/exp/parimatch/launch").andReturn();
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));
    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  //  @Test
  public void testPlaycheck() {
    // TODO: to be implemented after finalize method
  }
}
