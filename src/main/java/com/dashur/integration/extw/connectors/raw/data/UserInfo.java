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

public class UserInfo {
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("country")
    private String country;

    @JsonProperty("birthdate")
    private String birthdate;

    @JsonProperty("language")
    private String language;

    @JsonProperty("channel")
    private String channel;
}
