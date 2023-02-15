package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RelayOpenPlayModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;

import java.util.Optional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public class RelayOpenPlay {

    @GET
    @Path("relayOpenPlay/{sessionId}/{rgsPlayId}/{actionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponseWrapperModel<RelayOpenPlayModel> relayOpenPlay(
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
            @QueryParam("playingFreePlay") Optional<Boolean> playingFreePlay) {

        int status = -1;
        int delay = 500;

        RestResponseWrapperModel<RelayOpenPlayModel> response = null;

        while (status == 0) {

            status = 1;

            response = relayOpenPlay(auth, tz, currency, txId, lang, tenantId, sessionId, rgsPlayId, actionId,
                    token, playingFreePlay);

            if (response.hasError())
                status = 0;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                status = 0;
            }
        }

        return response;

    }
}
