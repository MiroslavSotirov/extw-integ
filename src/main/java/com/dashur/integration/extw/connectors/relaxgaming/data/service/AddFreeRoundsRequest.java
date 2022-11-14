package com.dashur.integration.extw.connectors.relaxgaming.data.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.ZonedDateTime;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AddFreeRoundsRequest extends ServiceRequest {

  @JsonProperty("txid")
  private Long txId;

  @JsonProperty("playerid")
  private Integer playerId;

  @JsonProperty("partnerid")
  private Integer partnerId;

  @JsonProperty("gameref")
  private String gameRef;

  @JsonProperty("amount")
  private Integer amount;     // num free rounds. valid range 1 - 5000

  @JsonProperty("freespinvalue")
  private Long freespinValue; // vale of single round in (default EUR) cents

  // ISO 8601. default 7 days
  @JsonProperty("expires")
  private ZonedDateTime expires;

  // optional

  @JsonProperty("currency")
  private String currency;    // to give bet amount in player's own currency

  @JsonProperty("promocode")
  private String promoCode;   // max 64 characters

  @JsonProperty("playercurrency")
  private String playerCurrency;

}
