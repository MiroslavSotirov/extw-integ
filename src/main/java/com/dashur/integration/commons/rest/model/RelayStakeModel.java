package com.dashur.integration.commons.rest.model;

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

public class RelayStakeModel {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("actionId")
    private Long actionId;

    @JsonProperty("actionResult")
    private String actionResult;

    @JsonProperty("balance")
    private Number balance;

    @JsonProperty("freeBalance")
    private String freeBalance;

    @JsonProperty("freePlaysData")
    private FreePlaysModel freePlaysData;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;

}
