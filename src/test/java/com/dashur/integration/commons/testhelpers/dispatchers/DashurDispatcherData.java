package com.dashur.integration.commons.testhelpers.dispatchers;

import com.dashur.integration.commons.utils.CommonUtils;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DashurDispatcherData {
  private static final Object LOCK = new Object();
  private static DashurDispatcherData instance = null;

  public static final DashurDispatcherData instance() {
    if (Objects.isNull(instance)) {
      synchronized (LOCK) {
        if (Objects.isNull(instance)) {
          instance = new DashurDispatcherData();
        }
      }
    }

    return instance;
  }

  private Map<String, String> APP_CRED_AUTH;
  private Map<String, Map<String, Object>> APP_CRED_DATA;
  private Map<String, String> PASSWORD_CRED_AUTH;
  private Map<String, Map<String, Object>> PASSWORD_CRED_DATA;

  private DashurDispatcherData() {
    init();
    reset();
  }

  /** initialise datas */
  private void init() {
    APP_CRED_AUTH = new HashMap<>();
    APP_CRED_DATA = new HashMap<>();
    PASSWORD_CRED_AUTH = new HashMap<>();
    PASSWORD_CRED_DATA = new HashMap<>();
  }

  /** reset datas */
  public void reset() {
    initAppCredAuthData();
    initPasswordCredAuthData();
  }

  /** initialize app-credential datas. */
  private void initAppCredAuthData() {
    {
      APP_CRED_AUTH.put("gnrc.api.t1", "b7FwChAzEXumMxTPC8QH2Zci");
      APP_CRED_AUTH.put("gnrc.api.t2", "b7FwChAzEXumMxTPC8QH2Zci");
      APP_CRED_AUTH.put("gnrc.api.t3", "b7FwChAzEXumMxTPC8QH2Zci");

      {
        Map<String, Object> result = new HashMap<>();
        result.put(
            "access_token",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdCI6MSwic2NvcGUiOlsiY2FtcGFpZ246dyIsInVzZXI6dyIsIndhbGxldDp3Iiwid2FsbGV0OnIiLCJhdWRpdDpyIiwiY2FtcGFpZ246ciIsInRva2VuOnciLCJyZXBvcnQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInVzZXI6ciIsInR4OnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJjb21wbGlhbmNlOnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImFjY291bnQ6ciIsImFwcGxpY2F0aW9uOnIiXSwicGlkIjo5ODk5LCJleHAiOjE1ODcwOTYwNTgsImFpZCI6MSwiYW4iOiJTWVNURU0iLCJ0aWQiOjEsImp0aSI6IjA2NzZhZmI3LTkyYzEtNDkzYi1hYzE3LTYyYzZkODY3YzUyZiIsImNsaWVudF9pZCI6ImducmMuYXBpLnQxIiwiYXAiOiIxIn0.ppSeEhz9Obdn_GV7WZZUjO6NUcgJXBPnONp28z1Q5S4");
        result.put("token_type", "bearer");
        result.put("expires_in", 3599);
        result.put(
            "scope",
            "campaign:w user:w wallet:w wallet:r audit:r campaign:r token:w report:r launcher_item:r user:r tx:r accapp:r category:r compliance:r account:w item:r tx:w exchange_rates:r account:r application:r");
        result.put("jti", UUID.randomUUID().toString());
        APP_CRED_DATA.put("gnrc.api.t1", result);
      }

      {
        Map<String, Object> result = new HashMap<>();
        result.put(
            "access_token",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdCI6MSwic2NvcGUiOlsiY2FtcGFpZ246dyIsInVzZXI6dyIsIndhbGxldDp3Iiwid2FsbGV0OnIiLCJhdWRpdDpyIiwiY2FtcGFpZ246ciIsInRva2VuOnciLCJyZXBvcnQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInVzZXI6ciIsInR4OnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJjb21wbGlhbmNlOnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImFjY291bnQ6ciIsImFwcGxpY2F0aW9uOnIiXSwicGlkIjo5OTAwLCJleHAiOjE1ODcwOTYyNzcsImFpZCI6MSwiYW4iOiJTWVNURU0iLCJ0aWQiOjIsImp0aSI6ImNiMGYxZDZjLTBlZjUtNGFjYS05YjQ3LTExYjUzYTM1YmU4OCIsImNsaWVudF9pZCI6ImducmMuYXBpLnQyIiwiYXAiOiIxIn0.HdHg0cIxjTSsiTbMgSiP-gIWtzuJ0lQm_tV5YU2jH5I");
        result.put("token_type", "bearer");
        result.put("expires_in", 3599);
        result.put(
            "scope",
            "campaign:w user:w wallet:w wallet:r audit:r campaign:r token:w report:r launcher_item:r user:r tx:r accapp:r category:r compliance:r account:w item:r tx:w exchange_rates:r account:r application:r");
        result.put("jti", UUID.randomUUID().toString());
        APP_CRED_DATA.put("gnrc.api.t2", result);
      }

      {
        Map<String, Object> result = new HashMap<>();
        result.put(
            "access_token",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdCI6MSwic2NvcGUiOlsiY2FtcGFpZ246dyIsInVzZXI6dyIsIndhbGxldDp3Iiwid2FsbGV0OnIiLCJhdWRpdDpyIiwiY2FtcGFpZ246ciIsInRva2VuOnciLCJyZXBvcnQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInVzZXI6ciIsInR4OnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJjb21wbGlhbmNlOnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImFjY291bnQ6ciIsImFwcGxpY2F0aW9uOnIiXSwicGlkIjo5OTAxLCJleHAiOjE1ODcwOTYzNjksImFpZCI6MSwiYW4iOiJTWVNURU0iLCJ0aWQiOjMsImp0aSI6ImY2OThkN2JkLTU5NjAtNGQ1OS1hZGY3LTE2YzMxZGQ4YzU1NyIsImNsaWVudF9pZCI6ImducmMuYXBpLnQzIiwiYXAiOiIxIn0.AfzOgX3BxOGyAa-Eh6bgE2dFdMX5TbCggFmfptkJyxo");
        result.put("token_type", "bearer");
        result.put("expires_in", 3599);
        result.put(
            "scope",
            "campaign:w user:w wallet:w wallet:r audit:r campaign:r token:w report:r launcher_item:r user:r tx:r accapp:r category:r compliance:r account:w item:r tx:w exchange_rates:r account:r application:r");
        result.put("jti", UUID.randomUUID().toString());
        APP_CRED_DATA.put("gnrc.api.t3", result);
      }
    }
  }

  private void initPasswordCredAuthData() {
    PASSWORD_CRED_AUTH.put("gnrc.api.t1", "b7FwChAzEXumMxTPC8QH2Zci");

    {
      Map<String, Object> result = new HashMap<>();
      result.put(
          "access_token",
          "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdCI6MSwic2NvcGUiOlsiY2FtcGFpZ246dyIsInVzZXI6dyIsIndhbGxldDp3Iiwid2FsbGV0OnIiLCJhdWRpdDpyIiwiY2FtcGFpZ246ciIsInRva2VuOnciLCJyZXBvcnQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInVzZXI6ciIsInR4OnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJjb21wbGlhbmNlOnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImFjY291bnQ6ciIsImFwcGxpY2F0aW9uOnIiXSwicGlkIjo5ODk5LCJleHAiOjE1ODcwOTYwNTgsImFpZCI6MSwiYW4iOiJTWVNURU0iLCJ0aWQiOjEsImp0aSI6IjA2NzZhZmI3LTkyYzEtNDkzYi1hYzE3LTYyYzZkODY3YzUyZiIsImNsaWVudF9pZCI6ImducmMuYXBpLnQxIiwiYXAiOiIxIn0.ppSeEhz9Obdn_GV7WZZUjO6NUcgJXBPnONp28z1Q5S4");
      result.put("token_type", "bearer");
      result.put("expires_in", 3599);
      result.put(
          "scope",
          "campaign:w user:w wallet:w wallet:r audit:r campaign:r token:w report:r launcher_item:r user:r tx:r accapp:r category:r compliance:r account:w item:r tx:w exchange_rates:r account:r application:r");
      result.put("jti", UUID.randomUUID().toString());
      PASSWORD_CRED_DATA.put("gnrc.api.t1", result);
    }
  }

  /**
   * check if basic app is valid.
   *
   * @param authorizationHeader
   * @return
   */
  public Boolean isValidBasicAppCred(String authorizationHeader) {
    if (!CommonUtils.isEmptyOrNull(authorizationHeader)) {
      if (authorizationHeader.startsWith("Basic ")) {
        try {
          String base64 = authorizationHeader.replace("Basic ", "");
          String raw = new String(Base64.getDecoder().decode(base64));
          String[] kv = raw.split(":");
          return isValidAppCred(kv[0], kv[1]);
        } catch (Exception e) {
        }
      }
    }
    return Boolean.FALSE;
  }

  /**
   * @param clientId
   * @param clientCredential
   * @return
   */
  public Boolean isValidAppCred(String clientId, String clientCredential) {
    if (APP_CRED_AUTH.containsKey(clientId)) {
      if (clientCredential.equals(APP_CRED_AUTH.get(clientId))) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  /**
   * check if basic app is valid.
   *
   * @param authorizationHeader
   * @return
   */
  public Boolean isValidBasicPasswordCred(String authorizationHeader) {
    if (!CommonUtils.isEmptyOrNull(authorizationHeader)) {
      if (authorizationHeader.startsWith("Basic ")) {
        try {
          String base64 = authorizationHeader.replace("Basic ", "");
          String raw = new String(Base64.getDecoder().decode(base64));
          String[] kv = raw.split(":");
          return isValidPasswordCred(kv[0], kv[1]);
        } catch (Exception e) {
        }
      }
    }
    return Boolean.FALSE;
  }

  /**
   * @param clientId
   * @param clientCredential
   * @return
   */
  public Boolean isValidPasswordCred(String clientId, String clientCredential) {
    if (PASSWORD_CRED_AUTH.containsKey(clientId)) {
      if (clientCredential.equals(PASSWORD_CRED_AUTH.get(clientId))) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }

  /**
   * @param appId
   * @return
   */
  public Map<String, Object> appAuthData(String appId) {
    if (!CommonUtils.isEmptyOrNull(appId) && APP_CRED_DATA.containsKey(appId)) {
      return APP_CRED_DATA.get(appId);
    }
    return null;
  }

  /**
   * @param username
   * @return
   */
  public Map<String, Object> passwordAuthData(String username) {
    if (!CommonUtils.isEmptyOrNull(username) && PASSWORD_CRED_DATA.containsKey(username)) {
      return PASSWORD_CRED_DATA.get(username);
    }
    return null;
  }

  /**
   * @param type
   * @param code
   * @param message
   * @return
   */
  public Map<String, Object> error(String type, Integer code, String message) {
    Map<String, Object> result = new HashMap<>();
    result.put("meta", meta());
    Map<String, Object> error = new HashMap<>();
    result.put("error", error);

    error.put("type", type);
    error.put("code", code);
    error.put("message", message);

    return result;
  }

  /** @return */
  public Map<String, Object> meta() {
    Map<String, Object> meta = new HashMap<>();
    meta.put("currency", "USD");
    meta.put("time_zone", "UTC");
    meta.put("transaction_id", "DEFAULT-TX-ID");
    meta.put("processing_time", new Long("475"));
    return meta;
  }
}
