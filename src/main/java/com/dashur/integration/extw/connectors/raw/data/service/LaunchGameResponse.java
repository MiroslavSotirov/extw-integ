package com.dashur.integration.extw.connectors.raw.data.service;

import com.dashur.integration.extw.connectors.raw.data.Response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

// raw

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class LaunchGameResponse extends Response {

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
