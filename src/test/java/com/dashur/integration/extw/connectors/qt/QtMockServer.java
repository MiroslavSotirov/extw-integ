package com.dashur.integration.extw.connectors.qt;

import com.dashur.integration.commons.testhelpers.MockServer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QtMockServer extends MockServer {

  static Map<String, String> services =
      Map.of(
          "com.dashur.integration.extw.connectors.qt.QtClientService", "/external/qt",
          "manual-rest-client.extw.operator.qt.co.12345.remote-base-uri", "/external/qt");

  public QtMockServer() {
    super("qtServer", services);
  }
}
