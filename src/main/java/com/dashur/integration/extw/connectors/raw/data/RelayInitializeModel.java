package com.dashur.integration.extw.connectors.raw.data;

import java.util.List;

import com.dashur.integration.extw.connectors.raw.data.service.FreePlaysData;
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
    private List<Long> stakeIncrement;

    @JsonProperty("minStake")
    private Number minStake;

    @JsonProperty("maxStake")
    private Number maxStake;

    @JsonProperty("autoPlayList")
    private List<Long> autoPlayList;

    @JsonProperty("autoPlayDefault")
    private Long autoPlayDefault;

    @JsonProperty("currencyId")
    private Long currencyId;

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
    private FreePlaysData freePlaysData;

    @JsonProperty("userInfo")
    private UserInfo userInfo;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;

}
