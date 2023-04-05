package com.dashur.integration.commons.cache;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.exception.BaseException;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
public class CacheProviderTest {
  @Inject CacheProvider cacheProvider;

  @Test
  public void testRefresh() {
    assertDoesNotThrow(() -> cacheProvider.refresh());
  }

  @Test
  public void testInit() {
    // void initCache(String cacheName, Class kType, Class vType, Integer size, Integer ttl);
    assertDoesNotThrow(
        () -> cacheProvider.initCache("test-cache-2", String.class, String.class, 10, 10));
    assertThrows(
        BaseException.class,
        () -> cacheProvider.initCache("test-cache-2", String.class, String.class, 10, 10));
  }

  @Test
  public void testOperations() {
    assertThrows(BaseException.class, () -> cacheProvider.put("test-cache-3", "abc", "def"));

    assertDoesNotThrow(
        () -> cacheProvider.initCache("test-cache-3", String.class, String.class, 10, 10));

    assertDoesNotThrow(() -> cacheProvider.put("test-cache-3", "key-1", "value-1"));

    assertThat(cacheProvider.get("test-cache-3", String.class, "key-1"), is("value-1"));

    assertThrows(BaseException.class, () -> cacheProvider.put("test-cache-3", "key-1", 10L));

    assertThrows(BaseException.class, () -> cacheProvider.put("test-cache-3", 20L, 10L));

    assertThrows(BaseException.class, () -> cacheProvider.put("test-cache-3", 20L, null));

    assertThrows(BaseException.class, () -> cacheProvider.put("test-cache-3", "key-2", null));

    assertDoesNotThrow(() -> cacheProvider.remove("test-cache-3", "key-1"));

    assertDoesNotThrow(() -> cacheProvider.remove("test-cache-3", "key-1"));

    assertDoesNotThrow(() -> cacheProvider.removeAll("test-cache-3"));

    assertThrows(BaseException.class, () -> cacheProvider.removeAll("test-cache-4"));

    assertDoesNotThrow(() -> cacheProvider.remove("test-cache-3", "key-4"));
  }
}
