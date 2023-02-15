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

public class RelayWinModel {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("actionId")
    private Long actionId;

    @JsonProperty("totalPaid")
    private String totalPaid;

    @JsonProperty("actionResult")
    private String actionResult;

    @JsonProperty("balance")
    private Number balance;

    @JsonProperty("freeBalance")
    private String freeBalance;

    @JsonProperty("freePlaysData")
    private FreePlaysModel freePlaysData;

    @JsonProperty("casinoData")
    private Object casinoData;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;

}
