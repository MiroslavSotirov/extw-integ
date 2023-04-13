package com.dashur.integration.extw.connectors.raw.data;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.extw.connectors.raw.data.service.FreePlaysData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import javax.json.JsonObject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RelayWin {

  @GET
  @Path("relayWin/{sessionId}/{rgsPlayId}/{actionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response relayWin(
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
      @QueryParam("freePlaysData") Optional<String> freePlaysData) {
    int status = -1;
    int delay = 500;

    while (status == 0) {

      status = 1;

      Response response = relayWin(auth, tz, currency, txId, lang, tenantId, sessionId, rgsPlayId, actionId, async,
          token, forced, freePlaysData);

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {

        if (async) {
          JsonObject jsonResponse = Json.createObjectBuilder()
              .add("sessionId", sessionId)
              .add("method", "relayClosePlay")
              .add("pending", true)
              .build();

          return Response.ok(jsonResponse).build();
        } else {

          JsonObject freePlaysJson = Json.createObjectBuilder().build();

          if (freePlaysData.isPresent()) {
            ObjectMapper objectMapper = new ObjectMapper();
            FreePlaysData _freePlaysData = null;
            try {
              _freePlaysData = objectMapper.readValue(freePlaysData.get(), FreePlaysData.class);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

            if (_freePlaysData != null) {
              freePlaysJson = Json.createObjectBuilder()
                  .add("promotionCode", _freePlaysData.getPromotionCode())
                  .add("promotionTotalBet", _freePlaysData.getPromotionTotalBet())
                  .add("promotionLines", _freePlaysData.getPromotionLines())
                  .add("promotionMaxLines", _freePlaysData.isPromotionMaxLines())
                  .add("promotionPlaysDone", _freePlaysData.getPromotionPlaysDone())
                  .add("promotionPlaysRemaining", _freePlaysData.getPromotionPlaysRemaining())
                  .add("promotionWonSoFar", _freePlaysData.getPromotionWonSoFar())
                  .add("promotionExtraInfo", _freePlaysData.getPromotionExtraInfo())
                  .add("promotionTerms", _freePlaysData.getPromotionTerms())
                  .add("promotionExpirationDate", _freePlaysData.getPromotionExpirationDate().toString())
                  .build();
            }
          }

          JsonObject jsonResponse = Json.createObjectBuilder().build();
          jsonResponse = Json.createObjectBuilder()
              .add("actionResult", "ok")
              .add("freePlaysData", freePlaysJson)
              .add("method", "relayClosePlay")
              .add("sessionId", sessionId)
              .add("actionId", actionId)
              .add("balance", jsonResponse.getInt("balance"))
              .add("freebalance", jsonResponse.getInt("freebalance"))
              .add("token", token)
              .build();

          return Response.ok(jsonResponse).build();
        }
      } else
        status = 0;

      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
        status = 0;
      }
    }

    return Response.serverError().build();

  }

}