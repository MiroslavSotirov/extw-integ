package com.dashur.integration.extw.connectors.raw.data;

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

public class RelayClosePlayResponse {

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

    // IsPending

    @JsonProperty("pending")
    private Boolean pending;

    //Optional

    @JsonProperty("freePlaysData")
    private FreePlaysData freePlaysData;

}
