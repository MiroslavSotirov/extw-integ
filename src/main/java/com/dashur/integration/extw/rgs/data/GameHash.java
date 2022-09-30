package com.dashur.integration.extw.rgs.data;

import java.util.Map;
import java.util.List;
import java.math.BigDecimal;
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
public class GameHash {

  @JsonProperty("item_id")
  private String itemId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("title")
  private String title;

  @JsonProperty("config")
  private String config;

  @JsonProperty("md5_digest")
  private String md5Digest;

  @JsonProperty("sha1_digest")
  private String sha1Digest;

  @JsonProperty("category")
  private String category;

  @JsonProperty("flags")
  private String flags;

  @JsonProperty("stakes")
  private Map<String, List<BigDecimal>> stakes;

  
}
