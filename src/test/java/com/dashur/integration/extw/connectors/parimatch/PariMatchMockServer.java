package com.dashur.integration.extw.connectors.parimatch;

import com.dashur.integration.commons.testhelpers.MockServer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PariMatchMockServer extends MockServer {

  static Map<String, String> services =
      Map.of(
          "com.dashur.integration.extw.connectors.parimatch.PariMatchClientService",
          "/external/parimatch",
          "manual-rest-client.extw.operator.parimatch.co.12345.remote-base-uri",
          "/external/parimatch");

  public PariMatchMockServer() {
    super("pariMatchServer", services);
  }
}
