package com.dashur.integration.commons;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/** HealthController-Test. */
@Slf4j
@QuarkusTest
public class HealthControllerTest {
  @Test
  public void testHealth() {
    given().when().get("/health").then().statusCode(200);
    String resp = given().when().get("/health").body().asString();
    assertThat(resp.startsWith("OK - ["), is(Boolean.TRUE));
  }
}
