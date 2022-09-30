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
public class AckPromotionAddRequest {


  @JsonProperty("promotions")
  List<AckPromotionAddRequest.Promotion> promotions;

  public class Promotion {

    @JsonProperty("data")
    private AckPromotionAddRequest.Promotion.Data data;

    @JsonProperty("playerid")
    private Integer playerId;

    @JsonProperty("promotionid")
    private Long promotionId;

    @JsonProperty("txid")
    private String txId;  

    public class Data {

      @JsonProperty("channel")
      private String channel;  

      @JsonProperty("freespinsid")
      private String freespinsId;

    }

  }

}
