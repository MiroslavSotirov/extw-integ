package com.dashur.integration.commons.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.dashur.integration.commons.auth.Token;
import com.dashur.integration.commons.kvstore.KVStore;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CacheServiceTest {
  @Inject CacheService cacheService;

  @Inject KVStore kvStore;

  @Test
  public void test() {
    assertThat(cacheService.getAccessToken("key-1"), is(IsNull.nullValue()));
    assertThat(cacheService.getRefreshToken("key-1"), is(IsNull.nullValue()));
    assertThat(cacheService.getCurrency(3L), is(IsNull.nullValue()));
    Token token = new Token();
    token.setAccessToken("access-token");
    token.setRefreshToken("refresh-token");
    assertDoesNotThrow(() -> cacheService.putToken("Key-1", token));
    assertDoesNotThrow(() -> cacheService.putCurrency(1L, "SGD"));
    assertDoesNotThrow(() -> cacheService.putCurrency(2L, "CNY"));
    assertThat(cacheService.getCurrency(1L), is("SGD"));
    assertThat(cacheService.getCurrency(2L), is("CNY"));
    assertThat(cacheService.getAccessToken("Key-1"), is(token.getAccessToken()));
    assertThat(cacheService.getRefreshToken("Key-1"), is(token.getRefreshToken()));

    kvStore.putWithTtl("key", 30 * 1000L, "value");
    String value = kvStore.get("key");
    assertThat(value, is("value"));
  }
}
