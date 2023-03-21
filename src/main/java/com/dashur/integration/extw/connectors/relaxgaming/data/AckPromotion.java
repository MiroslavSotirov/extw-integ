package com.dashur.integration.extw.connectors.relaxgaming.data;

import java.util.List;
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
public class AckPromotion {

  @JsonProperty("data")
  private AckPromotionData data;

  @JsonProperty("playerid")
  private Long playerId;

  @JsonProperty("promotionid")
  private Long promotionId;

  @JsonProperty("txid")
  private String txId;  

}
