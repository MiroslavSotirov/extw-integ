package com.dashur.integration.extw;

import com.dashur.integration.commons.testhelpers.dispatchers.DashurDispatcherData;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

@Slf4j
public class DashurLauncherDispatcher extends Dispatcher {
  private static final Object LOCK = new Object();
  private static DashurLauncherDispatcher instance = null;

  public static final DashurLauncherDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new DashurLauncherDispatcher();
        }
      }
    }

    return instance;
  }

  private DashurDispatcherData data;

  private DashurLauncherDispatcher() {
    data = DashurDispatcherData.instance();
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    if (request.getPath().startsWith("/v1/launcher/item") && "POST".equals(request.getMethod())) {
      log.info(
          "{} - {} - {}", this.getClass().getSimpleName(), request.getMethod(), request.getPath());

      String body = request.getBody().readUtf8();
      Map<String, Object> bodyData = CommonUtils.jsonReadMap(body);
      if (1000L == Long.parseLong(bodyData.get("item_id").toString())) {
        Map<String, Object> result = new HashMap<>();
        result.put("meta", data.meta());
        result.put(
            "data",
            "https://stg.maverick-ops.com/a-fairy-tale/index.html?operator=mav&language=en&currency=USD&user=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdCI6ImIyMThiZGNlLTExNWMtNDcwNy1iZTM1LTc2NjA3YzVkZWMyZiIsImN0eCI6MjMsInVzZXJfbmFtZSI6IkhEVGVzdEhPX0xJX0NPMy5NQlItMDAxOjE5MzgzIiwidWlkIjoyNjE4MTc2LCJhaWQiOjI2MDE5NTd9.NGY8M2ISF6R0iD6APpy409JYMn_EWrLsqePOQNm4BD8&wallet=dashur&bank_url=https%3A%2F%2Fbank_url.somewhere.com&lobby_url=https%3A%2F%2Fbank_url.somewhere.com&gameName=a-fairy-tale");

        return new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(CommonUtils.jsonToString(result));
      }

      return new MockResponse()
          .setHeader("Content-Type", "application/json; charset=utf-8")
          .setBody(
              CommonUtils.jsonToString(
                  data.error(
                      "ENTITY_NOT_FOUND",
                      -9,
                      String.format("Not Found [%s]", bodyData.get("item_id")))));
    } else if (request.getPath().startsWith("/v1/launcher/tx")
        && "GET".equals(request.getMethod())) {
      log.info(
          "{} - {} - {}", this.getClass().getSimpleName(), request.getMethod(), request.getPath());

      String body = request.getBody().readUtf8();
      Map<String, Object> bodyData = CommonUtils.jsonReadMap(body);
      if (request.getPath().equals("/v1/launcher/tx/123456789?lang=en")) {
        Map<String, Object> result = new HashMap<>();
        result.put("meta", data.meta());
        result.put(
            "data",
            "https://stg.maverick-ops.com/a-fairy-tale/index.html?operator=mav&language=en&currency=USD&user=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdCI6ImIyMThiZGNlLTExNWMtNDcwNy1iZTM1LTc2NjA3YzVkZWMyZiIsImN0eCI6MjMsInVzZXJfbmFtZSI6IkhEVGVzdEhPX0xJX0NPMy5NQlItMDAxOjE5MzgzIiwidWlkIjoyNjE4MTc2LCJhaWQiOjI2MDE5NTd9.NGY8M2ISF6R0iD6APpy409JYMn_EWrLsqePOQNm4BD8&wallet=dashur&bank_url=https%3A%2F%2Fbank_url.somewhere.com&lobby_url=https%3A%2F%2Fbank_url.somewhere.com&gameName=a-fairy-tale");

        return new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(CommonUtils.jsonToString(result));
      }

      return new MockResponse()
          .setHeader("Content-Type", "application/json; charset=utf-8")
          .setBody(
              CommonUtils.jsonToString(
                  data.error(
                      "ENTITY_NOT_FOUND",
                      -9,
                      String.format("Not Found [%s]", bodyData.get("item_id")))));
    }

    return null;
  }
}
