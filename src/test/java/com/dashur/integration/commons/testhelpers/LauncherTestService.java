package com.dashur.integration.commons.testhelpers;

import com.dashur.integration.commons.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import lombok.Data;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface LauncherTestService {

  @POST
  @Path("/v1/launcher/item")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  String launchItem(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      final LaunchReq req);

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class LaunchReq {
    @JsonProperty("ext_ref")
    private String mbrExtRef;

    @JsonProperty("item_id")
    private String gameItemId;

    @JsonProperty("app_id")
    private String gameAppId;
  }

  /** test token */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TestToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
  }
}
