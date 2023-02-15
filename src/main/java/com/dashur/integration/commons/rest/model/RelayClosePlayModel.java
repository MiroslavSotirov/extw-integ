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

public class RelayClosePlayModel {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("actionId")
    private Long actionId;

    @JsonProperty("actionResult")
    private String actionResult;

    @JsonProperty("balance")
    private String balance;

    @JsonProperty("freeBalance")
    private String freeBalance;

    @JsonProperty("token")
    private String token;

    @JsonProperty("method")
    private String method;

    @JsonProperty("freePlaysData")
    private FreePlaysModel freePlaysData;

}
