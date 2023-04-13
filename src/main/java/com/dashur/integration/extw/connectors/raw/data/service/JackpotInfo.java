package com.dashur.integration.extw.connectors.raw.data.service;

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
public class JackpotInfo {

  @JsonProperty("name")
  private String id;

  @JsonProperty("jackpotBet")
  private Long jackpotBet;

  @JsonProperty("currencyId")
  private String currencyId;

  @JsonProperty("seedingContributionAmount")
  private String seedingContributionAmount;

  @JsonProperty("progressiveContributionAmount")
  private String progressiveContributionAmount;

  @JsonProperty("progressiveAccumulated")
  private String progressiveAccumulated;

}