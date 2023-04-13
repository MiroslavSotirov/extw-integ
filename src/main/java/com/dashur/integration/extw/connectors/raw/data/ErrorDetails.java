package com.dashur.integration.extw.connectors.raw.data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ErrorDetails {

  @JsonProperty("message")
  private String message;

  @JsonProperty("buttontext")
  private String buttonText;

  @JsonProperty("title")
  private String title;
}
