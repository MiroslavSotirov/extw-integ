package com.dashur.integration.extw.connectors.raw.data;

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
public class JackpotContribution {

  @JsonProperty("id")
  private String id;

  @JsonProperty("amount")
  private Long amount;

  @JsonProperty("currencyId")
  private String currencyId;

  @JsonProperty("seedingContributionAmount")
  private String seedingContributionAmount;

  @JsonProperty("progressiveContributionAmount")
  private String progressiveContributionAmount;

  @JsonProperty("progressiveAccumulated")
  private String progressiveAccumulated;

}