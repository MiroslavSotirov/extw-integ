package com.dashur.integration.commons.rest;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RelayGetBalanceModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface RelayGetBalance {

    @GET
    @Path("/relayGetBalance")
    @Produces(MediaType.APPLICATION_JSON)
    RestResponseWrapperModel<RelayGetBalanceModel> relayGetBalance(
        @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
        @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
        @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
        @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
        @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
        @PathParam("sessionId") Long sessionId,
        @QueryParam("token") String token);
}
