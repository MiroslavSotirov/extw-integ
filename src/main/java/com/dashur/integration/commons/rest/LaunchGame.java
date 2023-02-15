package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.LaunchGameModel;
import com.dashur.integration.commons.rest.model.RelayStakeModel;
import com.dashur.integration.commons.rest.model.RelayVoidStakeModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/launch/")
public interface LaunchGame {

        @GET
        @Path("relayLaunchGame/{sessionId}")
        @Produces(MediaType.APPLICATION_JSON)
        RestResponseWrapperModel<LaunchGameModel> relayLaunchGame(
                        @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
                        @PathParam("sessionId") Long sessionId,
                        @QueryParam("token") String token);

        @GET
        @Path("relayStake/{sessionId}/{rgsPlayId}/{actionId}")
        @Produces(MediaType.APPLICATION_JSON)
        RestResponseWrapperModel<RelayStakeModel> relayStake(
                        @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
                        @PathParam("sessionId") Long sessionId,
                        @PathParam("rgsPlayId") Long rgsPlayId,
                        @PathParam("actionId") Long actionId,
                        @QueryParam("token") String token,
                        @QueryParam("type") String type,
                        @QueryParam("amount") String amount);

        @GET
        @Path("relayVoidStake/{sessionId}/{rgsPlayId}/{voidedActionId}")
        @Produces(MediaType.APPLICATION_JSON)
        RestResponseWrapperModel<RelayVoidStakeModel> relayVoidStake(
                        @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
                        @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
                        @PathParam("sessionId") Long sessionId,
                        @PathParam("rgsPlayId") Long rgsPlayId,
                        @PathParam("voidedActionId") Long voidedActionId,
                        @QueryParam("token") String token);

}