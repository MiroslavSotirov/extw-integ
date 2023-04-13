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
public class RelayOpenPlayModel {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("rgsPlayId")
    private Long rgsPlayId;

    @JsonProperty("legaPlayId")
    private Long legaPlayId;

    @JsonProperty("actionId")
    private Long actionId;

    @JsonProperty("actionResult")
    private String actionResult;

    @JsonProperty("balance")
    private Number balance;

    @JsonProperty("freeBalance")
    private String freeBalance;

    @JsonProperty("freePlaysData")
    private FreePlaysData freePlaysData;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;
}
