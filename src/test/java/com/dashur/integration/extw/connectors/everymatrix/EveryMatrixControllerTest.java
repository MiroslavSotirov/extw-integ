package com.dashur.integration.extw.connectors.everymatrix;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignMemberModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.SimpleItemModel;
import com.dashur.integration.commons.testhelpers.BackendServer;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurAuthDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurDispatcherData;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.DashurFeedTransactionDispatcher;
import com.dashur.integration.extw.DashurLauncherDispatcher;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.AwardBonusRequest;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.BonusResponse;
import com.dashur.integration.extw.connectors.everymatrix.data.campaign.ForfeitBonusRequest;
import com.google.common.collect.Lists;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
public class EveryMatrixControllerTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_EVERYMATRIX;
  static final String LAUNCH_TOKEN = EveryMatrixDispatcher.LAUNCH_TOKEN_2976013;

  @Inject Service service;

  @Inject ExtwIntegConfiguration configuration;

  private DashurDispatcherData data;

  public EveryMatrixMockServer everyMatrixServer;
  public ServerDispatcher everyMatrixDispatcher;

  public BackendServer backendServer;
  public ServerDispatcher backendDispatcher;

  @BeforeEach
  public void setup() {
    backendDispatcher = new ServerDispatcher();
    backendDispatcher.register(DashurAuthDispatcher.instance());
    backendDispatcher.register(DashurLauncherDispatcher.instance());
    backendDispatcher.register(DashurFeedTransactionDispatcher.instance());
    backendServer.resetQueue(backendDispatcher);

    everyMatrixDispatcher = new ServerDispatcher();
    everyMatrixDispatcher.register(new EveryMatrixDispatcher());
    everyMatrixServer.resetQueue(everyMatrixDispatcher);

    data = DashurDispatcherData.instance();
  }

  @Test
  public void testLaunchUrl() {
    Map<String, Object> params = new HashMap<>();
    params.put("gameId", "1000");
    params.put("language", "en_US");
    params.put("freePlay", Boolean.FALSE);
    params.put("mobile", Boolean.FALSE);
    params.put("mode", "dev");
    params.put("token", LAUNCH_TOKEN);
    params.put("currencyCode", "EUR");

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/everymatrix/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));
  }

  @Test
  public void testLaunchUrl2() {
    Map<String, Object> params = new HashMap<>();
    params.put("gameId", "1001");
    params.put("language", "en_US");
    params.put("freePlay", Boolean.FALSE);
    params.put("mobile", Boolean.FALSE);
    params.put("mode", "dev");
    params.put("token", LAUNCH_TOKEN);
    params.put("currencyCode", "EUR");

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/everymatrix/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  @Test
  public void testLaunchUrl3() {
    Map<String, Object> params = new HashMap<>();
    params.put("gameId", "1000");
    params.put("language", "en_US");
    params.put("freePlay", Boolean.FALSE);
    params.put("mobile", Boolean.FALSE);
    params.put("mode", "blabla");
    params.put("token", LAUNCH_TOKEN);
    params.put("currencyCode", "EUR");

    Response response =
        given().when().formParams(params).get("/v1/extw/exp/everymatrix/launch").andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  @Test
  public void testPlaycheckURL() {
    Map<String, Object> params = new HashMap<>();
    params.put("RoundId", "123456789");
    params.put("UserId", "12345678989");
    params.put("game", "1234");

    Response response =
        given()
            .when()
            .contentType(ContentType.JSON)
            .body(params)
            .post("/v1/extw/exp/everymatrix/game-state")
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(200));
  }

  @Test
  public void testPlaycheckURL2() {
    Map<String, Object> params = new HashMap<>();
    params.put("RoundId", "123456789123");
    params.put("UserId", "12345678989");
    params.put("game", "1234");

    Response response =
        given()
            .when()
            .contentType(ContentType.JSON)
            .body(params)
            .post("/v1/extw/exp/everymatrix/game-state")
            .andReturn();

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatusCode(), is(500));

    log.info("Error result : [{}]", response.getBody().prettyPrint());
  }

  @Test
  public void testBonus() {
    final Long campaignId = 1001L;
    final Long userAccountId = 1002L;
    final String bonusId = UUID.randomUUID().toString();
    final String userId = UUID.randomUUID().toString();
    final String gameId = "10001";
    final String campaignExtRef = String.format("%s-%s", OPERATOR_CODE, bonusId);

    backendDispatcher.register(
        getBonusingDispatcher(campaignId, campaignExtRef, userId, userAccountId, gameId));

    Calendar end = Calendar.getInstance();
    end.add(Calendar.DATE, 2);

    {
      AwardBonusRequest rq = new AwardBonusRequest();
      rq.setUserId(userId);
      rq.setBonusId(bonusId);
      rq.setGameIds(Lists.newArrayList(gameId));
      rq.setNumberOfFreeRounds(10);
      rq.setCurrency("USD");
      rq.setCoinValue(new BigDecimal("10"));
      rq.setBetValueLevel(10);
      rq.setLineCount(10);
      rq.setFreeRoundsEndDate(end.getTime());

      Response response =
          given()
              .when()
              .contentType(ContentType.JSON)
              .body(rq)
              .post("/v1/extw/exp/everymatrix/award-bonus")
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));
      BonusResponse bonusRs =
          CommonUtils.jsonRead(BonusResponse.class, response.getBody().asString());
      assertThat(bonusRs, is(IsNull.notNullValue()));
      assertThat(bonusRs.getSuccess(), is(Boolean.TRUE));
      assertThat(bonusRs.getVendorBonusId(), is(campaignId.toString()));
    }

    {
      AwardBonusRequest rq = new AwardBonusRequest();
      rq.setUserId(userId);
      rq.setBonusId(bonusId);
      rq.setGameIds(Lists.newArrayList(gameId));
      rq.setNumberOfFreeRounds(10);
      rq.setCurrency("USD");
      rq.setCoinValue(new BigDecimal("10"));
      rq.setBetValueLevel(10);
      rq.setLineCount(10);
      rq.setFreeRoundsEndDate(end.getTime());

      Response response =
          given()
              .when()
              .contentType(ContentType.JSON)
              .body(rq)
              .post("/v1/extw/exp/everymatrix/award-bonus")
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));
      BonusResponse bonusRs =
          CommonUtils.jsonRead(BonusResponse.class, response.getBody().asString());
      assertThat(bonusRs, is(IsNull.notNullValue()));
      assertThat(bonusRs.getSuccess(), is(Boolean.TRUE));
      assertThat(bonusRs.getVendorBonusId(), is(campaignId.toString()));
    }

    {
      ForfeitBonusRequest rq = new ForfeitBonusRequest();
      rq.setBonusId(bonusId);
      rq.setUserId(userId);

      Response response =
          given()
              .when()
              .contentType(ContentType.JSON)
              .body(rq)
              .post("/v1/extw/exp/everymatrix/forfeit-bonus")
              .andReturn();

      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatusCode(), is(200));
      BonusResponse bonusRs =
          CommonUtils.jsonRead(BonusResponse.class, response.getBody().asString());
      assertThat(bonusRs, is(IsNull.notNullValue()));
      assertThat(bonusRs.getSuccess(), is(Boolean.TRUE));
      assertThat(bonusRs.getVendorBonusId(), is(campaignId.toString()));
    }
  }

  /**
   * @param campaignId
   * @param campaignExtRef
   * @param userId
   * @param userAccountId
   * @param gameId
   * @return
   */
  private Dispatcher getBonusingDispatcher(
      final Long campaignId,
      final String campaignExtRef,
      final String userId,
      final Long userAccountId,
      final String gameId) {
    final Map<Long, CampaignModel> campaigns = new HashMap<>();
    final Map<String, Long> campaignsByExtRef = new HashMap<>();

    return new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        if (request.getPath().equals("/v1/campaign") && request.getMethod().equals("POST")) {
          // create campaign:

          CampaignCreateModel create =
              CommonUtils.jsonRead(CampaignCreateModel.class, request.getBody().readUtf8());
          CampaignModel campaign = new CampaignModel();
          campaign.setId(campaignId);
          campaign.setAccountId(create.getAccountId());
          campaign.setName(create.getName());
          campaign.setExtRef(create.getExtRef());
          campaign.setVendorRef(campaignId.toString());
          campaign.setNumOfGames(create.getNumOfGames());
          campaign.setStatus(CampaignModel.Status.fromValue(create.getStatus().toString()));
          campaign.setType(CampaignModel.Type.fromValue(create.getType().toString()));
          campaign.setGameId(create.getGameId());
          campaign.setBetLevel(create.getBetLevel());
          campaign.setStartTime(create.getStartTime());
          campaign.setEndTime(create.getEndTime());
          campaign.setMetaData(new HashMap<>());

          campaigns.put(campaignId, campaign);
          campaignsByExtRef.put(campaign.getExtRef(), campaignId);

          MockResponse response = new MockResponse();
          response.setHeader("Content-Type", "application/json; charset=utf-8");
          response.setBody(
              CommonUtils.jsonToString(new RestResponseWrapperModel(data.meta(), null, campaign)));
          return response;
        }

        if (request.getPath().startsWith("/v1/campaign/search?ext_ref=")
            && request.getMethod().equals("GET")) {
          // search campaign

          Map<String, String> datas = DashurAuthDispatcher.parseQueryParam(request.getPath());
          if (datas.containsKey("ext_ref") && campaignExtRef.equals(datas.get("ext_ref"))) {
            if (campaignsByExtRef.containsKey(campaignExtRef)) {
              Long campaignId = campaignsByExtRef.get(campaignExtRef);
              CampaignModel campaign = campaigns.get(campaignId);

              MockResponse response = new MockResponse();
              response.setHeader("Content-Type", "application/json; charset=utf-8");
              response.setBody(
                  CommonUtils.jsonToString(
                      new RestResponseWrapperModel(data.meta(), null, campaign)));
              return response;
            } else {
              new MockResponse()
                  .setResponseCode(HttpStatus.SC_NOT_FOUND)
                  .setHeader("Content-Type", "application/json; charset=utf-8")
                  .setBody(
                      CommonUtils.jsonToString(
                          data.error(
                              "HTTP_EXCEPTION",
                              HttpStatus.SC_NOT_FOUND,
                              "SC_NOT_FOUND - not found.")));
            }
          }
        }

        if (request.getPath().startsWith("/v1/account?ext_ref=")
            && request.getMethod().equals("GET")) {
          // find account by ext ref
          Map<String, String> datas = DashurAuthDispatcher.parseQueryParam(request.getPath());
          if (datas.containsKey("ext_ref") && userId.equals(datas.get("ext_ref"))) {
            SimpleAccountModel accountModel = new SimpleAccountModel();
            accountModel.setExtRef(userId);
            accountModel.setMyPath(String.format("1,2,3,%s", userAccountId));
            accountModel.setId(userAccountId);
            accountModel.setName("name-" + userAccountId);
            accountModel.setTest(Boolean.FALSE);

            MockResponse response = new MockResponse();
            response.setHeader("Content-Type", "application/json; charset=utf-8");
            response.setBody(
                CommonUtils.jsonToString(
                    new RestResponseWrapperModel(
                        data.meta(), null, Lists.newArrayList(accountModel))));
            return response;
          }
        }

        if (request.getPath().equals(String.format("/v1/item/%s", gameId))
            && request.getMethod().equals("GET")) {
          SimpleItemModel item = new SimpleItemModel();
          item.setId(Long.parseLong(gameId));
          item.setVendorId(1001L);

          MockResponse response = new MockResponse();
          response.setHeader("Content-Type", "application/json; charset=utf-8");
          response.setBody(
              CommonUtils.jsonToString(new RestResponseWrapperModel(data.meta(), null, item)));
          return response;
        }

        if (request.getPath().equals(String.format("/v1/campaign/%s/member", campaignId))
            && request.getMethod().equals("POST")) {
          CampaignMemberModel member = new CampaignMemberModel();
          member.setExtId(userId);
          member.setId(1004L);
          member.setCampaignId(campaignId);
          member.setAccountId(userAccountId);
          MockResponse response = new MockResponse();
          response.setHeader("Content-Type", "application/json; charset=utf-8");
          response.setBody(
              CommonUtils.jsonToString(
                  new RestResponseWrapperModel(data.meta(), null, Lists.newArrayList(member))));
          return response;
        }

        if (request.getPath().equals(String.format("/v1/campaign/%s/member", campaignId))
            && request.getMethod().equals("PUT")) {
          CampaignMemberModel member = new CampaignMemberModel();
          member.setExtId(userId);
          member.setId(1004L);
          member.setCampaignId(campaignId);
          member.setAccountId(userAccountId);
          MockResponse response = new MockResponse();
          response.setHeader("Content-Type", "application/json; charset=utf-8");
          response.setBody(
              CommonUtils.jsonToString(
                  new RestResponseWrapperModel(data.meta(), null, Lists.newArrayList(member))));
          return response;
        }

        return null;
      }
    };
  }
}
