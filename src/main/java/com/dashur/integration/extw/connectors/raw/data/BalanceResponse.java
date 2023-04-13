package com.dashur.integration.extw.connectors.raw.data;

import com.dashur.integration.extw.connectors.raw.data.service.FreePlaysData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class BalanceResponse extends Response {

  @JsonProperty("balance")
  private Long balance; // In cents

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("freeBalance")
  private FreePlaysData freeBalance;

  @JsonProperty("method")
  private String method;

  @JsonProperty("token")
  private String token;

}
