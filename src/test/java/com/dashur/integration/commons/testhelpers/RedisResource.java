package com.dashur.integration.commons.testhelpers;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.File;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;

/** Resource to Load Redis during testing */
@Slf4j
public class RedisResource implements QuarkusTestResourceLifecycleManager {
  static final String REDIS_IMAGE = "redis:3.2";
  static final int REDIS_PORT = 6379;
  static final String REDIS_IP = "127.0.0.1";

  GenericContainer redis = null;

  @Override
  public Map<String, String> start() {
    log.info("start redis");
    int port = REDIS_PORT;
    String ip = REDIS_IP;
    try {
      redis = new GenericContainer(REDIS_IMAGE).withExposedPorts(REDIS_PORT);
      redis.start();
      port = redis.getFirstMappedPort();
      ip = redis.getContainerInfo().getNetworkSettings().getIpAddress();
      log.info("redis ip {}, port {}", ip, port);
    } catch (Exception e) {
      log.warn("redis.start() throwing error, please see if this affecting test cases", e);
    }

    if (!isDnd()) {
      return Map.of(
          "commons.cache.port", String.valueOf(port),
          "commons.kv.port", String.valueOf(port));
    } else {
      return Map.of(
          "commons.cache.host",
          ip,
          "commons.cache.port",
          String.valueOf(REDIS_PORT),
          "commons.kv.host",
          ip,
          "commons.kv.port",
          String.valueOf(REDIS_PORT));
    }
  }

  /** @return is run inside a docker. */
  Boolean isDnd() {
    return new File("/.dockerenv").exists();
  }

  @Override
  public void stop() {
    log.info("stop redis");
    redis.stop();
  }
}
