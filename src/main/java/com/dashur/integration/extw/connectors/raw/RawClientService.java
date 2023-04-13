package com.dashur.integration.extw.connectors.raw;

import com.dashur.integration.extw.connectors.raw.data.service.ClosePlayRequest;
import com.dashur.integration.extw.connectors.raw.data.service.InitializeRequest;
import com.dashur.integration.extw.connectors.raw.data.service.LaunchGameRequest;
import com.dashur.integration.extw.connectors.raw.data.service.RelayOpenPlayRequest;
import com.dashur.integration.extw.connectors.raw.data.service.StakeRequest;
import com.dashur.integration.extw.connectors.raw.data.service.VoidStakeRequest;
import com.dashur.integration.extw.connectors.raw.data.service.WinRequest;
import com.dashur.integration.extw.connectors.raw.data.BalanceRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface RawClientService {

        static final String AUTHORIZATION = "Authorization";
        static final String SESSIONID = "sessionId";

        @POST
        @Path("/{sessionId}/relayLaunchGame")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // ResponseWrapper<TransactionResponse>
        javax.ws.rs.core.Response launchGame(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final LaunchGameRequest request);

        @POST
        @Path("/{sessionId}/relayInitialize")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // ResponseWrapper<TransactionResponse>
        javax.ws.rs.core.Response initialize(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final InitializeRequest request);

        @POST
        @Path("/{sessionId}/relayOpenPlay")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // ResponseWrapper<TransactionResponse>
        javax.ws.rs.core.Response openPlay(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final RelayOpenPlayRequest request);

        @POST
        @Path("/{sessionId}/relayStake")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // ResponseWrapper<TransactionResponse>
        javax.ws.rs.core.Response stake(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final StakeRequest request);

        @POST
        @Path("/{sessionId}/relayGetbalance")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // ResponseWrapper<BalanceResponse>
        javax.ws.rs.core.Response getBalance(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final BalanceRequest request);

        @POST
        @Path("/{sessionId}/relayClosePlay")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // AckPromotionAddResponse
        javax.ws.rs.core.Response closePlay(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final ClosePlayRequest request);

        @POST
        @Path("/{sessionId}/relayWin")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        // TransactionResponse
        javax.ws.rs.core.Response win(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final WinRequest request);

        @POST
        @Path("/{sessionId}/realyVoidStake")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        javax.ws.rs.core.Response voidStake(
                        @HeaderParam(AUTHORIZATION) String auth, @PathParam(SESSIONID) Integer sessionId,
                        final VoidStakeRequest request);
}
