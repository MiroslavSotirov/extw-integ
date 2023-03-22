package com.dashur.integration.extw.connectors.everymatrix;

import com.dashur.integration.commons.testhelpers.MockServer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EveryMatrixMockServer extends MockServer {

  static Map<String, String> services =
      Map.of(
          "com.dashur.integration.extw.connectors.everymatrix.EveryMatrixClientService",
          "/external/everymatrix");

  public EveryMatrixMockServer() {
    super("everyMatrixServer", services);
  }
}
