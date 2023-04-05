package com.dashur.integration.commons.testhelpers;

import com.dashur.integration.commons.Constant;
import com.dashur.integration.commons.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.Data;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient
@RegisterClientHeaders
public interface AuthClientTestService {

  @POST
  @Path("/oauth/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  Token loginPassword(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @FormParam(Constant.REST_AUTH_PARAM_GRANT_TYPE) String grantType,
      @FormParam(Constant.REST_AUTH_PARAM_USERNAME) String username,
      @FormParam(Constant.REST_AUTH_PARAM_PASSWORD) String password);

  @POST
  @Path("/oauth/token")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  Token loginClientCredentials(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      @FormParam(Constant.REST_AUTH_PARAM_GRANT_TYPE) String grantType,
      @FormParam(Constant.REST_AUTH_PARAM_CLIENT_ID) String clientId,
      @FormParam(Constant.REST_AUTH_PARAM_CLIENT_SECRET) String clientSecret);

  @POST
  @Path("/v1/token/login_as_member")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  MemberToken loginAsMember(
      @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
      @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
      @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
      final LoginMemberModel model);

  /** test token */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  class Token {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
  }

  /** test token */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  class MemberToken {
    @JsonProperty("data")
    private Token tokenData;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Token {
      @JsonProperty("token")
      private String accessToken;

      @JsonProperty("refresh_token")
      private String refreshToken;
    }
  }

  /** test token */
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  class LoginMemberModel {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("token")
    private String token;

    @JsonProperty("short_token")
    private Boolean shortToken;

    @JsonProperty("scopes")
    private List<String> scopes;

    @JsonProperty("linking_app_id")
    private Long linkingAppId;

    @JsonProperty("validity_seconds")
    private Integer validitySeconds;
  }

  /** utils */
  class Utils {
    /**
     * @param clientId
     * @param credentials
     * @return
     */
    public static String auth(String clientId, String credentials) {
      return CommonUtils.authorizationBasic(clientId, credentials);
    }

    /**
     * @param accessToken
     * @return
     */
    public static String bearer(String accessToken) {
      return CommonUtils.authorizationBearer(accessToken);
    }
  }
}
