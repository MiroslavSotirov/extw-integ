package com.dashur.integration.extw.connectors.raw.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class BalanceRequest extends Request {

  @JsonProperty("sessionId")
  private Long sessionId;

  @JsonProperty("token")
  private String token;
}
