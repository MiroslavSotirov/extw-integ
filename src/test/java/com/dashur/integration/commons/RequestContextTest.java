package com.dashur.integration.commons;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

public class RequestContextTest {
  @Test
  public void testSetServiceType() {
    RequestContext ctx = new RequestContext();
    MatcherAssert.assertThat(ctx.getRequestStartTime(), is(IsNull.notNullValue()));
    MatcherAssert.assertThat(ctx.getUuid(), is(IsNull.notNullValue()));
    try {
      Thread.sleep(100L);
    } catch (Exception e) {
      // ignore
    }
    Long duration = ctx.getDurationMs();
    MatcherAssert.assertThat(duration, is(IsNull.notNullValue()));
    MatcherAssert.assertThat(ctx.getRequestEndTime(), is(IsNull.notNullValue()));
    MatcherAssert.assertThat(duration >= 100L, is(Boolean.TRUE));
  }
}
