package com.dashur.integration.commons.testhelpers.dispatchers;

import com.dashur.integration.commons.utils.CommonUtils;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

@Slf4j
public class DashurAppIdDispatcher extends Dispatcher {
  private static final Object LOCK = new Object();
  private static DashurAppIdDispatcher instance = null;

  public static final DashurAppIdDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new DashurAppIdDispatcher();
        }
      }
    }

    return instance;
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    if (request.getPath().startsWith("/app-id.json") && "GET".equals(request.getMethod())) {
      log.info(
          "{} - {} - {}", this.getClass().getSimpleName(), request.getMethod(), request.getPath());
      Map<String, Object> result = new HashMap<>();
      List<Map<String, Object>> trustedFacets = new ArrayList<>();
      Map<String, Object> trustedFacet = new HashMap<>();
      Map<String, Object> version = new HashMap<>();
      {
        version.put("major", 1);
        version.put("minor", 0);
      }
      trustedFacet.put("version", version);
      List<String> ids = new ArrayList<>();
      ids.add("https://ui.t1.dashur.io");
      ids.add("https://ui.t2.dashur.io");
      ids.add("https://ui.t3.dashur.io");
      ids.add("https://api.t1.dashur.io");
      ids.add("https://api.t2.dashur.io");
      ids.add("https://api.t3.dashur.io");
      trustedFacet.put("ids", ids);
      trustedFacets.add(trustedFacet);
      result.put("trustedFacets", trustedFacets);

      return new MockResponse()
          .setHeader("Content-Type", "application/json; charset=utf-8")
          .setBody(CommonUtils.jsonToString(result));
    }

    return null;
  }

  /**
   * un proven parser, kindly don't re-use.
   *
   * @param body
   * @return
   */
  Map<String, String> parse(String body) {
    Map<String, String> result = new HashMap<>();

    String[] pairs = body.split("&");

    for (String pair : pairs) {
      String[] kv = pair.split("=");
      result.put(kv[0].trim(), kv[1].trim());
    }

    return result;
  }
}
