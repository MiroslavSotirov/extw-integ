package com.dashur.integration.commons.testhelpers;

import static org.junit.Assert.assertEquals;

import com.intuit.karate.KarateException;
import com.intuit.karate.Runner;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
public class KarateRunner {

  @ConfigProperty(name = "quarkus.http.test-port")
  private int port;

  public BackendServer backendServer;

  public void runFeature(String... features) throws Exception {
    for (var feature : features) {
      log.info("Starting Feature: {}", feature);
      try {
        Field[] fields = this.getClass().getFields();
        Map<String, Object> vars = new HashMap<>();
        for (var field : fields) {
          if (MockServer.class.isAssignableFrom(field.getType())) {
            vars.put(field.getName(), field.get(this));
          }
        }

        log.info("Using quarkus port: {}", port);
        vars.put("port", port);

        Map<String, Object> result = Runner.runFeature(this.getClass(), feature, vars, true);
      } catch (KarateException ex) {
        log.error("\n" + ex.getMessage());
        assertEquals("Karate Tests Failed - see error message logged above", true, false);
      }
      log.info("Ended Feature: {}", feature);
    }
  }
}
