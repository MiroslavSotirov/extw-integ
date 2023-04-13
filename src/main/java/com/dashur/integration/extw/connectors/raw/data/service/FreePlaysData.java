package com.dashur.integration.extw.connectors.raw.data.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FreePlaysData {

    @JsonProperty("promotionCode")
    private String promotionCode;

    @JsonProperty("promotionTotalBet")
    private long promotionTotalBet;

    @JsonProperty("promotionLines")
    private int promotionLines;

    @JsonProperty("promotionMaxLines")
    private boolean promotionMaxLines;

    @JsonProperty("promotionPlaysDone")
    private int promotionPlaysDone;

    @JsonProperty("promotionPlaysRemaining")
    private int promotionPlaysRemaining;

    @JsonProperty("promotionWonSoFar")
    private long promotionWonSoFar;

    @JsonProperty("promotionExtraInfo")
    private String promotionExtraInfo;

    @JsonProperty("promotionTerms")
    private String promotionTerms;

    @JsonProperty("promotionExpirationDate")
    private LocalDate promotionExpirationDate;
}
