package com.dashur.integration.extw.connectors.relaxgaming.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor()
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWrapper<T> {

  @JsonUnwrapped
  private javax.ws.rs.core.Response response;

  public T getResponse(Class<T> cls) {
    return response.readEntity(cls);
  }

  public ErrorResponse getErrorResponse() {
    return response.readEntity(ErrorResponse.class);
  }

  public int getStatus() {
    return response.getStatus();
  }

}
