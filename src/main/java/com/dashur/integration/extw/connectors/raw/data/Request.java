package com.dashur.integration.extw.connectors.raw.data;

import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Request {

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("requestid")
  private String requestId;

  public void setTimestamp() {
    timestamp = new Date().getTime();
  }

  public void setRequestId() {
    requestId = UUID.randomUUID().toString();
  }
}
