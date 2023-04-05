package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.testhelpers.MockServer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RelaxGamingMockServer extends MockServer {

  static Map<String, String> services =
      Map.of(
          "com.dashur.integration.extw.connectors.relaxgaming.RelaxGamingClientService",
          "/external/relaxgaming",
          "manual-rest-client.extw.operator.relaxgaming.co.12345.remote-base-uri",
          "/external/relaxgaming");

  public RelaxGamingMockServer() {
    super("relaxGamingServer", services);
  }
}
