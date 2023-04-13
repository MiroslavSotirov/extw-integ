package com.dashur.integration.extw.connectors.raw.data;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.extw.connectors.raw.data.service.ClosePlayRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface ClosePlayService {

  @GET
  @Path("relayClosePlay")
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponseWrapperModel<RelayClosePlayResponse> closePlay(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      final ClosePlayRequest request);

}