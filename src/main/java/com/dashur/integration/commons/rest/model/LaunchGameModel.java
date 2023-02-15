package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchGameModel {
    @JsonProperty("gameId")
    private Long gameId;

    @JsonProperty("operatorId")
    private String operatorId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("currencyId")
    private String currencyId;

    @JsonProperty("gameMode")
    private String gameMode;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("isInterrupted")
    private Boolean isInterrupted;

    @JsonProperty("currencySuffix")
    private String currencySuffix;

    @JsonProperty("decimalPlaces")
    private Number decimalPlaces;

    @JsonProperty("currencyPrefix")
    private String currencyPrefix;

    @JsonProperty("method")
    private String method;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("responseHistory")
    private List<String> responseHistory;
}
