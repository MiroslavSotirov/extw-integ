package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.FreePlaysModel;

import java.util.Optional;

import javax.json.JsonObject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class ClosePlay {

  @GET
  @Path("relayClosePlay")
  @Produces(MediaType.APPLICATION_JSON)
  public Response relayClosePlay(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
      @PathParam("sessionId") Long sessionId,
      @PathParam("rgsPlayId") Long rgsPlayId,
      @PathParam("actionId") Long actionId,
      @QueryParam("async") Boolean async,
      @QueryParam("token") String token,
      @QueryParam("forced") Boolean forced,
      @QueryParam("freePlaysData") Optional<FreePlaysModel> freePlaysData) {
    boolean success = false;

    try {
      success = true;
    } catch (Exception e) {
      success = false;
    }
    if (async) {
      JsonObject response = Json.createObjectBuilder()
          .add("sessionId", sessionId)
          .add("method", "relayClosePlay")
          .add("pending", true)
          .build();

      return Response.ok(response).build();
    } else {

      JsonObject freePlaysJson = Json.createObjectBuilder().build();

      if (freePlaysData.isPresent()) {
        freePlaysJson = Json.createObjectBuilder()
            .add("promotionCode", freePlaysData.get().getPromotionCode())
            .add("promotionTotalBet", freePlaysData.get().getPromotionTotalBet())
            .add("promotionLines", freePlaysData.get().getPromotionLines())
            .add("promotionMaxLines", freePlaysData.get().isPromotionMaxLines())
            .add("promotionPlaysDone", freePlaysData.get().getPromotionPlaysDone())
            .add("promotionPlaysRemaining", freePlaysData.get().getPromotionPlaysRemaining())
            .add("promotionWonSoFar", freePlaysData.get().getPromotionWonSoFar())
            .add("promotionExtraInfo", freePlaysData.get().getPromotionExtraInfo())
            .add("promotionTerms", freePlaysData.get().getPromotionTerms())
            .add("promotionExpirationDate", freePlaysData.get().getPromotionExpirationDate().toString())
            .build();
      }

      JsonObject response = Json.createObjectBuilder().build();
      response = Json.createObjectBuilder()
          .add("actionResult", success ? "ok" : "error")
          .add("freePlaysData", freePlaysJson)
          .add("casinoData", response.getValue("casinoData"))
          .add("method", "relayClosePlay")
          .add("sessionId", sessionId)
          .add("actionId", actionId)
          .add("balance", response.getInt("balance"))
          .add("freebalance", response.getInt("freebalance"))
          .add("token", token)
          .build();

      return Response.ok(response).build();
    }
  }

}