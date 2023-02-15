package com.dashur.integration.commons.rest.model;

import java.util.List;

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
public class RelayInitializeModel {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("stakeIncrement")
    private List<Number> stakeIncrement;

    @JsonProperty("minStake")
    private Number minStake;

    @JsonProperty("maxStake")
    private Number maxStake;

    @JsonProperty("autoPlayList")
    private List<Number> autoPlayList;

    @JsonProperty("autoPlayDefault")
    private Number autoPlayDefault;

    @JsonProperty("currencyId")
    private Number currencyId;

    @JsonProperty("currencyPrefix")
    private String currencyPrefix;

    @JsonProperty("currencySuffix")
    private String currencySuffix;

    @JsonProperty("decimalPlaces")
    private String decimalPlaces;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("freeBalance")
    private String freeBalance;

    @JsonProperty("maxWinnings")
    private String maxWinnings;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("sessionExpiry")
    private Number sessionExpiry;

    @JsonProperty("freePlaysData")
    private FreePlaysModel freePlaysData;

    @JsonProperty("userInfo")
    private Object userInfo;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;

}
