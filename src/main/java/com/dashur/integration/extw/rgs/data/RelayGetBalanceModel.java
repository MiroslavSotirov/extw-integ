package com.dashur.integration.extw.rgs.data;

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

public class RelayGetBalanceModel {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("balance")
    private Number balance;

    @JsonProperty("freeBalance")
    private Number freeBalance;

    @JsonProperty("freePlaysData")
    private FreePlaysModel freePlaysData;

    @JsonProperty("method")
    private String method;

    @JsonProperty("token")
    private String token;

}
