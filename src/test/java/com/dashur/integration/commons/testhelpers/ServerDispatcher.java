package com.dashur.integration.commons.testhelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.http.HttpStatus;

/** mean to implements simple logic on server end. */
public class ServerDispatcher extends Dispatcher {
  private List<Dispatcher> dispatchers = new ArrayList<>();

  /** clear dispatcher list */
  public void clear() {
    this.dispatchers = new ArrayList<>();
    this.dispatchers.add(
        new AuthDispatcher()); // authenticate dispatcher will always be returned as default.
  }

  /**
   * add in dispatcher list
   *
   * @param dispatcher
   */
  public void register(Dispatcher dispatcher) {
    this.dispatchers.add(dispatcher);
  }

  @Override
  public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
    MockResponse response = null;

    for (Dispatcher dispatcher : dispatchers) {
      response = dispatcher.dispatch(request);
      if (Objects.nonNull(response)) {
        return response;
      }
    }

    // if no dispatcher handling the request, then return empty.
    return new MockResponse().setResponseCode(HttpStatus.SC_NOT_FOUND);
  }

  /** default dispatcher, handling the member login and application login.s */
  public static final class AuthDispatcher extends Dispatcher {
    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
      return null;
    }
  }
}
