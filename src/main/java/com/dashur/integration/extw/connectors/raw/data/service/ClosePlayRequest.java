package com.dashur.integration.extw.connectors.raw.data.service;

import com.dashur.integration.extw.connectors.raw.data.Request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ClosePlayRequest extends ServiceRequest {

    @JsonProperty("sessionId")
    private Long sessionId;

    @JsonProperty("rgsPlayId")
    private Long rgsPlayId;

    @JsonProperty("actionId")
    private Long actionId;

    @JsonProperty("async")
    private Boolean async;

    @JsonProperty("token")
    private String token;

    @JsonProperty("freePlaysData")
    private FreePlaysData freePlaysData;

    @JsonProperty("forced")
    private Boolean forced;

}
