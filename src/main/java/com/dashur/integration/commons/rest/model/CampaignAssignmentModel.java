package com.dashur.integration.commons.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

@Data
@Builder(builderMethodName = "newCampaignAssignmentModelBuilder")
@Wither
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CampaignAssignmentModel {
  /** The account id of the member */
  @JsonProperty("account_id")
  private Long accountId;

  /** The account ext ref of the member */
  @JsonProperty("account_ext_ref")
  private String accountExtRef;

  /** The id of the campaign */
  @JsonProperty("campaign_id")
  private Long campaignId;

  /** The id of game which the player has been awarded */
  @JsonProperty("game_id")
  private Long gameId;

}
