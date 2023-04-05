package com.dashur.integration.extw.connectors.vgs;

import com.dashur.integration.commons.testhelpers.MockServer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VgsMockServer extends MockServer {

  static Map<String, String> services =
      Map.of("com.dashur.integration.extw.connectors.vgs.VgsClientService", "/external/vgs");

  public VgsMockServer() {
    super("vgsServer", services);
  }
}
