package com.dashur.integration.commons.testhelpers.dispatchers;

import com.dashur.integration.commons.utils.CommonUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;

@Slf4j
public class DashurAuthDispatcher extends Dispatcher {
  private static final Object LOCK = new Object();
  private static DashurAuthDispatcher instance = null;

  public static final DashurAuthDispatcher instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new DashurAuthDispatcher();
        }
      }
    }

    return instance;
  }

  private DashurDispatcherData data;

  private DashurAuthDispatcher() {
    data = DashurDispatcherData.instance();
  }

  // TODO: enhance this to handle more cases. now is only for usage on EveryMatrix integration.
  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    if (request.getPath().startsWith("/oauth/token") && "POST".equals(request.getMethod())) {
      log.info(
          "{} - {} - {}", this.getClass().getSimpleName(), request.getMethod(), request.getPath());

      Map<String, String> body = parseFormBody(request.getBody().readUtf8());
      String grantType = body.get("grant_type");
      if ("client_credentials".equals(grantType)) {
        String clientId = body.get("client_id");
        String clientSecret = body.get("client_secret");

        if (data.isValidBasicAppCred(request.getHeader("Authorization"))) {
          if (data.isValidAppCred(clientId, clientSecret)) {
            return new MockResponse()
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(CommonUtils.jsonToString(data.appAuthData(clientId)));
          }
        }

        return new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                CommonUtils.jsonToString(
                    data.error(
                        "HTTP_EXCEPTION",
                        HttpStatus.SC_UNAUTHORIZED,
                        "UNAUTHORIZED - either cred wrong or not found.")));
      }

      if ("password".equals(grantType)) {
        String username = body.get("username");
        String password = body.get("password");

        if (data.isValidBasicPasswordCred(request.getHeader("Authorization"))) {
          if (data.isValidPasswordCred(username, password)) {
            return new MockResponse()
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(CommonUtils.jsonToString(data.passwordAuthData(username)));
          }
        }

        // TODO: handle when we comes to it.
        return new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                CommonUtils.jsonToString(
                    data.error(
                        "HTTP_EXCEPTION",
                        HttpStatus.SC_UNAUTHORIZED,
                        "UNAUTHORIZED - Not implemented. Fix this later.")));
      }

      if ("refresh_token".equals(grantType)) {
        // TODO: handle when we comes to it.
        return new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                CommonUtils.jsonToString(
                    data.error(
                        "HTTP_EXCEPTION",
                        HttpStatus.SC_UNAUTHORIZED,
                        "UNAUTHORIZED - Not implemented. Fix this later.")));
      }

      return new MockResponse()
          .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
          .setHeader("Content-Type", "application/json; charset=utf-8")
          .setBody(
              CommonUtils.jsonToString(
                  data.error(
                      "HTTP_EXCEPTION",
                      HttpStatus.SC_UNAUTHORIZED,
                      "UNAUTHORIZED - Unknown grant types")));
    }

    return null;
  }

  /**
   * un proven parser, kindly don't re-use.
   *
   * @param body
   * @return
   */
  public static final Map<String, String> parseFormBody(String body) {
    Map<String, String> result = new HashMap<>();

    String[] pairs = body.split("&");

    for (String pair : pairs) {
      if (!CommonUtils.isEmptyOrNull(pair) && pair.contains("=")) {
        String[] kv = pair.split("=");
        result.put(kv[0].trim(), kv[1].trim());
      }
    }

    return result;
  }

  public static final Map<String, String> parseQueryParam(String path) {
    Map<String, String> result = new HashMap<>();

    if (!CommonUtils.isEmptyOrNull(path) && path.contains("?")) {
      int idx = path.indexOf("?");
      result = parseFormBody(path.substring(idx + 1));
    }

    return result;
  }
}
