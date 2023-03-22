package com.dashur.integration.extw;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.RedisResource;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixConfiguration;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixDispatcher;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixMockServer;
import com.dashur.integration.extw.data.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(BackendServer.class)
@QuarkusTestResource(EveryMatrixMockServer.class)
@QuarkusTestResource(RedisResource.class)
public class ExtwConnectorsControllerTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_EVERYMATRIX;
  static final String LAUNCH_TOKEN = EveryMatrixDispatcher.LAUNCH_TOKEN_2976013;
  static final String TOKEN = EveryMatrixDispatcher.TOKEN_2976013;
  static final AtomicLong TX_ID = new AtomicLong(110000L);

  @Inject ExtwIntegConfiguration configuration;
  private Long companyId;

  public BackendServer backendServer;

  public EveryMatrixMockServer everyMatrixServer;

  @PostConstruct
  public void initQuarkusTest() {
    companyId =
        configuration
            .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
            .getDefaultCompanyId();
  }

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new EveryMatrixDispatcher());
    everyMatrixServer.resetQueue(dispatcher);
  }

  @Test
  public void testAuth() {
    DasAuthRequest rq = new DasAuthRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());

    String json = CommonUtils.jsonToString(rq);
    String hash =
        HmacUtil.hash(
            configuration
                .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                .getDefaultCompanySetting()
                .getHmacKey(),
            json);
    Response response =
        given()
            .when()
            .header("X-DAS-HMAC", hash)
            .body(json)
            .post(String.format("/v1/extw/connect/everymatrix/%s/v1/auth", companyId))
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));

    DasAuthResponse rs = CommonUtils.jsonRead(DasAuthResponse.class, response.getBody().asString());
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getAccountExtRef(), is("2976013"));
  }

  @Test
  public void testBalance() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976013");
    rq.setCurrency("EUR");

    String json = CommonUtils.jsonToString(rq);
    String hash =
        HmacUtil.hash(
            configuration
                .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                .getDefaultCompanySetting()
                .getHmacKey(),
            json);
    Response response =
        given()
            .when()
            .header("X-DAS-HMAC", hash)
            .body(json)
            .post(String.format("/v1/extw/connect/everymatrix/%s/v1/balance", companyId))
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));

    DasBalanceResponse rs =
        CommonUtils.jsonRead(DasBalanceResponse.class, response.getBody().asString());
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getBalance(), is(new BigDecimal("10000")));
  }

  @Test
  public void testTransactionWager() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance = getBalance();

    DasTransactionRequest rq = new DasTransactionRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");
    rq.setAmount(new BigDecimal("10"));
    rq.setApplicationId(1L);
    rq.setCategory(DasTransactionCategory.WAGER);
    rq.setItemId(1L);
    rq.setRoundId(txId.toString());
    rq.setTxId(txId);

    String json = CommonUtils.jsonToString(rq);
    String hash =
        HmacUtil.hash(
            configuration
                .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                .getDefaultCompanySetting()
                .getHmacKey(),
            json);

    Response response =
        given()
            .when()
            .header("X-DAS-HMAC", hash)
            .body(json)
            .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));

    DasTransactionResponse rs =
        CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
  }

  @Test
  public void testTransactionPayout() {
    Long wagerTxId = TX_ID.incrementAndGet();
    Long payoutTxId = TX_ID.incrementAndGet();
    BigDecimal balance = getBalance();

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976013");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setTxId(wagerTxId);

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasTransactionResponse rs =
          CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976013");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.PAYOUT);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setTxId(payoutTxId);

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasTransactionResponse rs =
          CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance));
    }
  }

  @Test
  public void testTransactionRefund() {
    Long wagerTxId = TX_ID.incrementAndGet();
    BigDecimal balance = getBalance();

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976013");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setTxId(wagerTxId);

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasTransactionResponse rs =
          CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.REFUND);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setRefundTxId(wagerTxId);
      rq.setTxId(TX_ID.incrementAndGet());

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasTransactionResponse rs =
          CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance));
    }
  }

  @Test
  public void testEndRound() {
    Long wagerTxId = TX_ID.incrementAndGet();
    BigDecimal balance = getBalance();

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976013");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setTxId(wagerTxId);

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/transaction", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasTransactionResponse rs =
          CommonUtils.jsonRead(DasTransactionResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
    }

    {
      DasEndRoundRequest rq = new DasEndRoundRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setApplicationId(1L);
      rq.setItemId(1L);
      rq.setRoundId(wagerTxId.toString());
      rq.setTxId(wagerTxId);

      String json = CommonUtils.jsonToString(rq);
      String hash =
          HmacUtil.hash(
              configuration
                  .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                  .getDefaultCompanySetting()
                  .getHmacKey(),
              json);

      Response response =
          given()
              .when()
              .header("X-DAS-HMAC", hash)
              .body(json)
              .post(String.format("/v1/extw/connect/everymatrix/%s/v1/endround", companyId))
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));

      DasEndRoundResponse rs =
          CommonUtils.jsonRead(DasEndRoundResponse.class, response.getBody().asString());
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
    }
  }

  private BigDecimal getBalance() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976013");
    rq.setCurrency("EUR");

    String json = CommonUtils.jsonToString(rq);
    String hash =
        HmacUtil.hash(
            configuration
                .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
                .getDefaultCompanySetting()
                .getHmacKey(),
            json);
    Response response =
        given()
            .when()
            .header("X-DAS-HMAC", hash)
            .body(json)
            .post(String.format("/v1/extw/connect/everymatrix/%s/v1/balance", companyId))
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));

    DasBalanceResponse rs =
        CommonUtils.jsonRead(DasBalanceResponse.class, response.getBody().asString());
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));

    return rs.getBalance();
  }
}
