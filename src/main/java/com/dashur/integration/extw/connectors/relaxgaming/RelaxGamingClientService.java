package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.extw.connectors.relaxgaming.data.AddFreeSpinsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.DepositRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.PingResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.RollbackRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WithdrawRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.ResponseWrapper;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotionAddRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotionAddResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface RelaxGamingClientService {

  static final String AUTHORIZATION = "Authorization";
  static final String PARTNERID = "partnerid";

  @POST
  @Path("/{partnerid}/verifytoken")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  // ResponseWrapper<VerifyTokenResponse> 
  javax.ws.rs.core.Response verifyToken(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final VerifyTokenRequest request);

  @POST
  @Path("/{partnerid}/withdraw")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  // ResponseWrapper<TransactionResponse> 
  javax.ws.rs.core.Response withdraw(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final WithdrawRequest request);

  @POST
  @Path("/{partnerid}/deposit")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  //ResponseWrapper<TransactionResponse> 
  javax.ws.rs.core.Response deposit(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final DepositRequest request);

  @POST
  @Path("/{partnerid}/rollback")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  // ResponseWrapper<TransactionResponse> 
  javax.ws.rs.core.Response rollback(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final RollbackRequest request);

  @POST
  @Path("/{partnerid}/getbalance")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  // ResponseWrapper<BalanceResponse> 
  javax.ws.rs.core.Response getBalance(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final BalanceRequest request);

  @POST
  @Path("/{partnerid}/ackpromotionadd")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  //  AckPromotionAddResponse 
  javax.ws.rs.core.Response ackPromotionAdd(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final AckPromotionAddRequest request);

  @POST
  @Path("/{partnerid}/addfreespins")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  // TransactionResponse 
  javax.ws.rs.core.Response addFreespins(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId, 
    final AddFreeSpinsRequest request);

  @POST
  @Path("/{partnerid}/ping")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  PingResponse ping(
    @HeaderParam(AUTHORIZATION) String auth, @PathParam(PARTNERID) Integer partnerId);

}
