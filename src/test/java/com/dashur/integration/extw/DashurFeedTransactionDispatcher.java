package com.dashur.integration.extw;

import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurAuthDispatcher;
import com.dashur.integration.commons.testhelpers.dispatchers.DashurDispatcherData;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

@Slf4j
public class DashurFeedTransactionDispatcher extends Dispatcher {
  private static final Object LOCK = new Object();
  private static DashurFeedTransactionDispatcher instance = null;

  public static final DashurFeedTransactionDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new DashurFeedTransactionDispatcher();
        }
      }
    }

    return instance;
  }

  private DashurDispatcherData data;

  private DashurFeedTransactionDispatcher() {
    data = DashurDispatcherData.instance();
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    if (request.getPath().startsWith("/v1/feed/transactionround")
        && "GET".equals(request.getMethod())) {
      log.info(
          "{} - {} - {}", this.getClass().getSimpleName(), request.getMethod(), request.getPath());

      Map<String, String> params = DashurAuthDispatcher.parseQueryParam(request.getPath());

      if (params.containsKey("external_ref")) {
        if ("123456789".equals(params.get("external_ref"))) {
          TransactionRoundModel model = new TransactionRoundModel();
          model.setId(123456789L);

          List<TransactionRoundModel> results = new ArrayList<>();
          results.add(model);

          Map<String, Object> result = new HashMap<>();
          result.put("meta", data.meta());
          result.put("data", results);

          return new MockResponse()
              .setHeader("Content-Type", "application/json; charset=utf-8")
              .setBody(CommonUtils.jsonToString(result));
        }
      }

      return new MockResponse()
          .setHeader("Content-Type", "application/json; charset=utf-8")
          .setBody(
              CommonUtils.jsonToString(
                  data.error(
                      "ENTITY_NOT_FOUND",
                      -9,
                      String.format("Not Found [%s]", params.get("external_ref")))));
    }

    return null;
  }
}
