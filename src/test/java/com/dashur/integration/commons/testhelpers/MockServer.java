package com.dashur.integration.commons.testhelpers;

import com.dashur.integration.commons.exception.ApplicationException;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.*;

@Slf4j
public abstract class MockServer implements QuarkusTestResourceLifecycleManager {

  protected MockWebServer server;

  protected String injectName = "";

  protected Map<String, String> services;

  protected List<MockResponse> initRes;

  /**
   * @param injectName The name of the variable that should be tried to be injected into the test
   *     class
   * @param services A map of services that will be reconfigured with new port and url. It is
   *     possible to send names like AuthService ->
   *     com.dashur.integration.commons.rest.AuthService/mp-rest/..
   *     com.dashur.integration.commons.rest.AuthService ->
   *     com.dashur.integration.commons.rest.AuthService/mp-rest/.. The key will be used as the
   *     service key and the value will be prepended to the url AuthService : "/v1/auth" ->
   *     com.dashur.integration.commons.rest.AuthService/mp-rest/url=http://localhost:1234/v1/auth
   */
  public MockServer(String injectName, Map<String, String> services) {
    this(injectName, services, null);
  }

  /**
   * @param injectName The name of the variable that should be tried to be injected into the test
   *     class
   * @param services A list of services that will be reconfigured with new port and url. It is
   *     possible to send names like AuthService ->
   *     com.dashur.integration.commons.rest.AuthService/mp-rest/..
   *     com.dashur.integration.commons.rest.AuthService ->
   *     com.dashur.integration.commons.rest.AuthService/mp-rest/..
   * @param initRes Inital responses to setup when starting up, this can be used if the application
   *     does any calls during App Startup
   */
  public MockServer(String injectName, Map<String, String> services, List<MockResponse> initRes) {
    this.injectName = injectName;
    this.services = services;
    this.initRes = initRes;
  }

  @Override
  public Map<String, String> start() {
    try {
      this.server = new MockWebServer();
      this.server.start();
      resetQueue();
      log.info(
          "Started Mock Backend [{}] on port {}", this.getClass().getName(), this.server.getPort());
    } catch (Exception ex) {
      log.error("Error starting MockBackendServer", ex);
      throw new ApplicationException("Unable to start MockBackendServer");
    }

    if (initRes != null) {
      for (var res : initRes) {
        this.server.enqueue(res);
      }
    }

    Map<String, String> configs = new HashMap();

    if (services != null) {
      for (var service : services.entrySet()) {
        // When we check the service name we allow to send in with either
        // short name like AuthService, TransactionService etc or
        // if a user sends the full name including package then we shouldn't add it on
        String serviceName = service.getKey();
        if (!serviceName.contains(".")) {
          serviceName = String.format("com.dashur.integration.commons.rest.%s", serviceName);
        }
        if (serviceName.startsWith("manual-rest-client.")) {
          String urlKey = serviceName.replace("manual-rest-client.", "");
          String urlValue =
              String.format("http://localhost:%d%s", this.server.getPort(), service.getValue());
          configs.put(urlKey, urlValue);
        } else {
          String urlKey = String.format("%s/mp-rest/url", serviceName);
          String urlValue =
              String.format("http://localhost:%d%s", this.server.getPort(), service.getValue());
          String connectTimeout = String.format("%s/mp-rest/connectTimeout", serviceName);
          String readTimeout = String.format("%s/mp-rest/readTimeout", serviceName);
          configs.put(urlKey, urlValue);
          configs.put(connectTimeout, "1000");
          configs.put(readTimeout, "8000");
        }
      }
    }

    return configs;
  }

  @Override
  public void inject(Object testInstance) {
    Class<?> c = testInstance.getClass();
    try {
      Field f = c.getField(this.injectName);
      f.set(testInstance, this);
    } catch (NoSuchFieldException nfe) {
      // Do nothing
      // Quarkus tries to inject even on classes that don't
      // annotate the quarkusextension
    } catch (Exception ex) {
      log.error(String.format("Can't set field [%s] in test class", this.injectName), ex);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void stop() {
    try {
      this.server.close();
    } catch (Exception ex) {
      log.error("Error stopping MockBackendServer", ex);
      throw new ApplicationException("Unable to stop MockBackendServer");
    }
  }

  public int getPort() {
    return this.server.getPort();
  }

  /** reset queue */
  public void resetQueue() {
    QueueDispatcher dispatcher = new QueueDispatcher();
    dispatcher.setFailFast(true);
    this.server.setDispatcher(dispatcher);
  }

  public void resetQueue(Dispatcher dispatcher) {
    this.server.setDispatcher(dispatcher);
  }

  /** @param response */
  public void put(MockResponse response) {
    server.enqueue(response);
  }

  /** @return */
  public RecordedRequest take() {
    try {
      return server.takeRequest();
    } catch (InterruptedException e) {
      log.error("Unable to take request", e);
      throw new ApplicationException("Unable to take request");
    }
  }
}
