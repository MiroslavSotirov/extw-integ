package com.dashur.integration.extw.connectors.raw.data;

import com.dashur.integration.extw.connectors.raw.data.service.FreePlaysData;
import com.dashur.integration.extw.data.DasResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class DasBalanceResponse extends DasResponse {
  @JsonProperty("sessionId")
  private Long sessionId;

  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("freeBalance")
  private BigDecimal freeBalance;

  @JsonProperty("freePlaysData")
  private FreePlaysData freePlaysData;

  @JsonProperty("method")
  private FreePlaysData method;
}
