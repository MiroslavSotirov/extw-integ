package com.dashur.integration.extw.rgs.lega;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dashur.integration.commons.Constant;

public class ErrorService {

    private static final Map<String, String> errorCodes = new HashMap<String, String>() {
        {
            put("101", "Unable to lock session. The session is not accessible at the time.");
            put("102", "Invalid license.");
            put("103", "Invalid game.");
            put("104", "Invalid casino operator.");
            put("105", "Invalid game mode.");
            put("106", "Invalid user identifier.");
            put("107", "Fatal Error.");
            put("108", "Initial balance required.");
            put("110", "Expired License.");
            put("111", "Invalid currency.");
            put("112", "Service under maintenance.");
            put("113", "Play not ready to settle.");
            put("114", "The session has expired.");
            put("116", "The session is finalized.");
            put("509", "Insufficient funds.");
            put("530", "Service Temporarily Unavailable.");
            put("531", "Responsible gaming: Session limit reached.");
            put("532", "Responsible gaming: Loss limit reached.");
            put("533", "Responsible gaming: Blocked temporarily.");
            put("536", "Maximum win reached.");
        }
    };

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response error(
            @HeaderParam(Constant.REST_HEADER_AUTHORIZATION) String auth,
            @HeaderParam(Constant.REST_HEADER_X_DAS_TZ) String tz,
            @HeaderParam(Constant.REST_HEADER_X_DAS_CURRENCY) String currency,
            @HeaderParam(Constant.REST_HEADER_X_DAS_TX_ID) String txId,
            @HeaderParam(Constant.REST_HEADER_X_DAS_TX_LANG) String lang,
            @HeaderParam(Constant.REST_HEADER_X_DAS_TENANT_ID) Long tenantId,
            @QueryParam("code") String code) {

        String description = errorCodes.get(code);

        JsonObject response = Json.createObjectBuilder().build();
        response = Json.createObjectBuilder()
                .add("status", "ERROR")
                .add("errorCode", response.getJsonString("errorCode"))
                .add("code", code)
                .add("description", description)
                .add("traceback", response.getJsonString("traceback"))
                .add("extraInfo", response.getJsonArray("extraInfo"))
                .build();

        return Response.ok(response).build();
    }

}
