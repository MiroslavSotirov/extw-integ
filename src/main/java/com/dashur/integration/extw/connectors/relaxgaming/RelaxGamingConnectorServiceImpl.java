package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.BaseException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.PaymentException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.exception.DuplicateException;
import com.dashur.integration.commons.exception.CustomException;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorService;
import com.dashur.integration.extw.connectors.HmacUtil;
import com.dashur.integration.extw.connectors.relaxgaming.data.*;
import com.dashur.integration.extw.data.DasAuthRequest;
import com.dashur.integration.extw.data.DasAuthResponse;
import com.dashur.integration.extw.data.DasBalanceRequest;
import com.dashur.integration.extw.data.DasBalanceResponse;
import com.dashur.integration.extw.data.DasEndRoundRequest;
import com.dashur.integration.extw.data.DasEndRoundResponse;
import com.dashur.integration.extw.data.DasMoney;
import com.dashur.integration.extw.data.DasRequest;
import com.dashur.integration.extw.data.DasResponse;
import com.dashur.integration.extw.data.DasTransactionCategory;
import com.dashur.integration.extw.data.DasTransactionRequest;
import com.dashur.integration.extw.data.DasTransactionResponse;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.commons.rest.CampaignClientService;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.CampaignBetLevelModel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Named("relaxgaming-connector")
@Singleton
@Slf4j
public class RelaxGamingConnectorServiceImpl implements ConnectorService {

  @Inject ExtwIntegConfiguration config;

  @Inject DomainService domainService;

  @Inject CommonService commonService;

  @Inject @RestClient CampaignClientService campaignClientService;

  private RelaxGamingConfiguration relaxConfig;

  private Map<Long, RelaxGamingClientService> clientServices;

  @PostConstruct
  public void init() {
    clientServices = new ConcurrentHashMap<>();
    relaxConfig = config.configuration(Constant.OPERATOR_RELAXGAMING, RelaxGamingConfiguration.class);
  }

  @Override
  public DasAuthResponse auth(Long companyId, DasAuthRequest request) {
    try {
      log.info("RelaxGamingConnectorServiceImpl.auth [{}] [[}]", companyId, request);
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      VerifyTokenRequest operatorReq = (VerifyTokenRequest) Utils.map(request, setting);
      return (DasAuthResponse) Utils.map(request, 
        verifyToken(companyId, operatorReq, 
          clientService.verifyToken(auth, partnerId, operatorReq)));
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasBalanceResponse balance(Long companyId, DasBalanceRequest request) {
    try {
      log.info("RelaxGamingConnectorServiceImpl.balance [{}] [[}]", companyId, request);
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      BalanceRequest operatorReq = (BalanceRequest) Utils.map(request, setting);
      return (DasBalanceResponse) Utils.map(request, balance(clientService.getBalance(auth, partnerId, operatorReq)));
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  @Override
  public DasTransactionResponse transaction(Long companyId, DasTransactionRequest request) {
    try {
      log.info("RelaxGamingConnectorServiceImpl.transaction [{}] [[}]", companyId, request);
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      RelaxGamingClientService clientService = clientService(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();

      if (DasTransactionCategory.WAGER == request.getCategory()) {        
        WithdrawRequest operatorReq = (WithdrawRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = transaction(clientService.withdraw(auth, partnerId, operatorReq));
        if (Objects.isNull(operatorRes.getBalance())) {
          // null balance when wager is 0 (used for promotions) and Dashur expects it
          log.info("Send additional balance request due to missing balance in transaction reply. Wager was {}", 
            operatorReq.getAmount());
          BalanceRequest balReq = new BalanceRequest();
          balReq.setPlayerId(operatorReq.getPlayerId());
          balReq.setGameRef(operatorReq.getGameRef());
          balReq.setCurrency(operatorReq.getCurrency());
          balReq.setSessionId(operatorReq.getSessionId());
          balReq.setTimestamp();
          balReq.setRequestId();
          BalanceResponse balResp = balance(clientService.getBalance(auth, partnerId, balReq));
          operatorRes.setBalance(balResp.getBalance());
        }
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.PAYOUT == request.getCategory()) {
        DepositRequest operatorReq = (DepositRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = transaction(clientService.deposit(auth, partnerId, operatorReq));
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      } else if (DasTransactionCategory.REFUND == request.getCategory()) {
        RollbackRequest operatorReq = (RollbackRequest) Utils.map(request, setting);
        TransactionResponse operatorRes = transaction(clientService.rollback(auth, partnerId, operatorReq));
        return (DasTransactionResponse) Utils.map(request, operatorRes);
      }
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }

    throw new ApplicationException(
        "Unable to find handler for the tx - category => %s", request.getCategory());
  }


  @Override
  public DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request) {    
    try {
      log.info("RelaxGamingConnectorServiceImpl.endRound [{}] [[}]", companyId, request);
      RelaxGamingConfiguration.CompanySetting setting =
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      RelaxGamingClientService clientService = clientService(companyId);
      DepositRequest operatorReq = (DepositRequest) Utils.map(request, setting);
      TransactionResponse operatorRes = transaction(clientService.deposit(auth, partnerId, operatorReq));
      return (DasEndRoundResponse) Utils.map(request, operatorRes);
    } catch (WebApplicationException e) {
      throw Utils.toException(e);
    }
  }

  private <T> T readResponse(javax.ws.rs.core.Response res, Class<T> cls) {
    return res.readEntity(cls);
  }

  private ErrorResponse readErrorResponse(javax.ws.rs.core.Response res) {
    return res.readEntity(ErrorResponse.class);
  }

  public VerifyTokenResponse verifyToken(Long companyId, VerifyTokenRequest req, javax.ws.rs.core.Response res) {
    if (Utils.isSuccess(res.getStatus())) {
      return readResponse(res, VerifyTokenResponse.class);
    }
    throw Utils.toException(readErrorResponse(res));
  }

  public BalanceResponse balance(javax.ws.rs.core.Response res) { // ResponseWrapper<BalanceResponse> res) {
    if (Utils.isSuccess(res.getStatus())) {
      return readResponse(res, BalanceResponse.class);
    }
    throw Utils.toException(readErrorResponse(res));
  }


//  static final 


  public TransactionResponse transaction(javax.ws.rs.core.Response res) { // ResponseWrapper<TransactionResponse> res) {
/*
    // test case for the ip blocked error
    throw new CustomException("IP_BLOCKED", "IP_BLOCKED");
*/
/*
    // test custom operator error
    ErrorResponse errorRes = new ErrorResponse();
    errorRes.setCode("CUSTOM_ERROR");
    errorRes.setMessage("oOooOo Ignore This oooOooo");
    ErrorParameters errorParams = new ErrorParameters();
    errorParams.setCode(559L);
    errorParams.setMessage("CUSTOM_ERROR");
    ErrorDetails errorDetails = new ErrorDetails();
    errorDetails.setMessage("This is a custom error message. It's very nice and could be quite verbose. Let's see\nif it handles linebreaks. No need to take it as far as \ttabs though, that would be silly.");
    errorDetails.setButtonText("A button text that could also be quite long, I guess.");
    errorDetails.setTitle("A title is a title.");
    errorParams.setDetails(errorDetails);
    errorRes.setParameters(errorParams);
    throw Utils.toException(errorRes);
*/
/*
    String[] errors = {
        "BLOCKED_FROM_PRODUCT",
        "IP_BLOCKED",
        "DAILY_TIME_LIMIT",
        "WEEKLY_TIME_LIMIT",
        "MONTHLY_TIME_LIMIT",
        "SPENDING_BUDGET_EXCEEDED",
        "CUSTOM_ERROR",
        "INSUFFICIENT_FUNDS",
        "TRANSACTION_NOT_FOUND",
        "INVALID_TXID"
    }; 
    ErrorResponse errorRes = new ErrorResponse();
    Random rng = new Random(new Date().getTime());
    errorRes.setCode(errors[Math.abs(rng.nextInt()) % errors.length]);
    throw Utils.toException(errorRes);
*/        
    int status = res.getStatus();
    if (Utils.isSuccess(res.getStatus())) {
      return readResponse(res, TransactionResponse.class);
    }
    throw Utils.toException(readErrorResponse(res));
  }

  @Override
  public void validate(Long companyId, String hmacHash, String rawData) {
    if (CommonUtils.isEmptyOrNull(hmacHash)) {
      throw new ApplicationException("hmac-hash is empty");
    }

    if (Objects.isNull(companyId)) {
      throw new ApplicationException("companyId is empty");
    }

    if (CommonUtils.isEmptyOrNull(rawData)) {
      throw new ApplicationException("rawData is empty");
    }

    if (!relaxConfig.getCompanySettings().containsKey(companyId)) {
      throw new ApplicationException("company [%s] config is not exists", companyId);
    }

    String hmacKey = relaxConfig.getCompanySettings().get(companyId).getHmacKey();
    if (!CommonUtils.isEmptyOrNull(hmacKey)) {
      String computedHmacHash = HmacUtil.hash(hmacKey, rawData);
      if (!computedHmacHash.equals(hmacHash)) {
        log.warn(
            "hmac-hash is not same with computed-hmac-hash [{} vs {}]", hmacHash, computedHmacHash);
      }
    } else if (!CommonUtils.isEmptyOrNull(hmacHash)) {
      log.warn(
        "hmac-hash is not validated [{}]", hmacHash);
    }
  }

  @Override
  public void validateIp(Long companyId, String callerIp) {
    if (relaxConfig.isValidateIps()) {
      if (CommonUtils.isEmptyOrNull(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP Validation is enabled, but caller ip is empty [%s]",
            callerIp);
      }

      callerIp = callerIp.trim();

      if (!relaxConfig.getWhitelistIps().contains(callerIp)) {
        throw new ValidationException(
            "Unable to validate caller ip. IP [%s] is not whitelisted", callerIp);
      }
    }
  }

  /** Retrieve clientService based on company id */
  public RelaxGamingClientService clientService(Long companyId) {
    RelaxGamingConfiguration.CompanySetting setting = relaxConfig.getCompanySettings().get(companyId);

    if (clientServices.containsKey(setting.getCompanyId())) {
      return clientServices.get(setting.getCompanyId());
    }

    try {
      String baseUri = setting.getRemoteBaseUri();
      RelaxGamingClientService clientService =
          RestClientBuilder.newBuilder()
              .baseUri(new URI(baseUri))
              .build(RelaxGamingClientService.class);
      clientServices.put(setting.getCompanyId(), clientService);

      return clientServices.get(setting.getCompanyId());
    } catch (URISyntaxException e) {
      log.error("Unable to construct clientService", e);
      throw new ApplicationException("Unable to create client service.");
    }
  }

  /** Utility classes */
  static final class Utils {

    /**
     * Map Dashur request object to Operator request object
     *
     * @param request
     * @param casinoId
     * @return
     */
    static Request map(DasRequest request, RelaxGamingConfiguration.CompanySetting settings) {
      Request output;

      log.debug("map request: {}", CommonUtils.jsonToString(request));

      Map<String, Object> metaData = getMetaData(request);
      String gameRef = metaData.getOrDefault("gameRef", "").toString();
      String clientId = metaData.getOrDefault("clientId", "").toString();
      String channel = metaData.getOrDefault("channel", "").toString();

      if (gameRef.isEmpty()) {
        throw new ValidationException("Unable to resolve gameRef");
      } 
      if (clientId.isEmpty()) {
        // throw new ValidationException("Unable to resolve clientId");
        log.warn("unable to resolve clientId");
      } 

      if (request instanceof DasAuthRequest) {
        String ip = metaData.getOrDefault(
          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS, "").toString();
        if (ip.isEmpty()) {
          throw new ValidationException("Unable to resolve player ip address");
        }
        VerifyTokenRequest operatorReq = new VerifyTokenRequest();
        operatorReq.setChannel(channel);
        operatorReq.setClientId(clientId);
        operatorReq.setToken(request.getToken());
        operatorReq.setGameRef(gameRef);
        operatorReq.setPartnerId(settings.getPartnerId());
        operatorReq.setIp(ip);     // TODO: get this from the request context?
        operatorReq.setTimestamp();
        operatorReq.setRequestId(request.getReqId());
        output = operatorReq;
      } else if (request instanceof DasBalanceRequest) {
        DasBalanceRequest balanceRequest = (DasBalanceRequest) request;
        BalanceRequest operatorReq = new BalanceRequest();
        operatorReq.setPlayerId(Long.parseLong(balanceRequest.getAccountExtRef()));
        operatorReq.setGameRef(gameRef);
        operatorReq.setCurrency(balanceRequest.getCurrency());
        operatorReq.setSessionId(Long.parseLong(balanceRequest.getToken()));
        operatorReq.setTimestamp();
        operatorReq.setRequestId(balanceRequest.getReqId());
        output = operatorReq;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest txRequest = (DasTransactionRequest) request;

        if (DasTransactionCategory.WAGER == txRequest.getCategory()) {
          WithdrawRequest operatorReq = new WithdrawRequest();
          operatorReq.setPlayerId(Long.parseLong(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(Utils.removePrefix(txRequest.getRoundId(), RelaxGamingConfiguration.ROUND_PREFIX));
          operatorReq.setGameRef(gameRef);
          operatorReq.setChannel(channel);
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setClientId(clientId);
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          if (Objects.isNull(txRequest.getCampaignId()) || txRequest.getCampaignId() == 0) {
            operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).longValue());
            operatorReq.setTxType("withdraw");
          } else {
            operatorReq.setAmount(CommonUtils.toCents(txRequest.getFreeAmount()).longValue());
            operatorReq.setTxType("freespinbet");
          }
          log.debug("wager of type {} and amount {}", operatorReq.getTxType(), operatorReq.getAmount());
          operatorReq.setEnded(Boolean.FALSE);
          operatorReq.setTimestamp();
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else if (DasTransactionCategory.PAYOUT == txRequest.getCategory()) {
          DepositRequest operatorReq = new DepositRequest();
          operatorReq.setPlayerId(Long.parseLong(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(Utils.removePrefix(txRequest.getRoundId(), RelaxGamingConfiguration.ROUND_PREFIX));
          operatorReq.setGameRef(gameRef);
          operatorReq.setChannel(channel);
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setClientId(clientId);
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).longValue());
          if (Objects.isNull(txRequest.getCampaignId()) || txRequest.getCampaignId() == 0) {
            operatorReq.setTxType("deposit");
          } else {
            operatorReq.setTxType("freespinpayout"); // or freespinpayoutfinal
            operatorReq.setFreespinsId(txRequest.getCampaignId().toString());
            operatorReq.setPromoCode(Utils.getPromoCode(txRequest.getCampaignExtRef()));
          }
          log.debug("payout of type {} and amount {}", operatorReq.getTxType(), operatorReq.getAmount());
          operatorReq.setEnded(Boolean.FALSE);
          operatorReq.setTimestamp();
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else if (DasTransactionCategory.REFUND == txRequest.getCategory()) {
          RollbackRequest operatorReq = new RollbackRequest();
          operatorReq.setPlayerId(Long.parseLong(txRequest.getAccountExtRef()));
          operatorReq.setRoundId(Utils.removePrefix(txRequest.getRoundId(), RelaxGamingConfiguration.ROUND_PREFIX));
          operatorReq.setGameRef(gameRef);
          operatorReq.setCurrency(txRequest.getCurrency());
          operatorReq.setTxId(String.valueOf(txRequest.getTxId()));
          operatorReq.setOriginalTxId(String.valueOf(txRequest.getRefundTxId()));
          operatorReq.setSessionId(Long.parseLong(txRequest.getToken()));
          operatorReq.setAmount(CommonUtils.toCents(txRequest.getAmount()).longValue());
          // operatorReq.setEnded(Boolean.FALSE); // only with Paddy Power Betfair
          operatorReq.setTimestamp();
          operatorReq.setOriginalTimestamp(txRequest.getTimestamp().getTime());
          operatorReq.setRequestId(txRequest.getReqId());
          output = operatorReq;

        } else {
          throw new ApplicationException("Unknown input, not mapped [%s] - category", request);
        }
      } else if (request instanceof DasEndRoundRequest) {
        DasEndRoundRequest endRequest = (DasEndRoundRequest) request;        
        DepositRequest operatorReq = new DepositRequest();
        operatorReq.setPlayerId(Long.parseLong(endRequest.getAccountExtRef()));
        operatorReq.setRoundId(Utils.removePrefix(endRequest.getRoundId(), RelaxGamingConfiguration.ROUND_PREFIX));
        operatorReq.setGameRef(gameRef);
        operatorReq.setChannel(channel);
        operatorReq.setCurrency(endRequest.getCurrency());
        operatorReq.setClientId(clientId);
        operatorReq.setTxId(String.valueOf(endRequest.getTxId()));
        operatorReq.setSessionId(Long.parseLong(endRequest.getToken()));
        operatorReq.setAmount(0L);
        if (Objects.isNull(endRequest.getCampaignId()) || endRequest.getCampaignId() == 0) {
          operatorReq.setTxType("deposit");
        } else {
          operatorReq.setTxType(endRequest.getSpinsRemain() > 0 ? "freespinpayout" : "freespinpayoutfinal");
          operatorReq.setFreespinsId(endRequest.getCampaignId().toString());
          operatorReq.setPromoCode(Utils.getPromoCode(endRequest.getCampaignExtRef()));
        }
        operatorReq.setEnded(Boolean.TRUE);
        operatorReq.setTimestamp(); // new date().getTime());
        operatorReq.setRequestId(endRequest.getReqId());
        output = operatorReq;        
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", request);
      }

      return output;
    }

    /**
     * Map Operator response object to Dashur response object
     *
     * @param request
     * @param input
     * @return
     */
    static DasResponse map(DasRequest request, Response input) {
      DasResponse output;
      if (request instanceof DasAuthRequest) {
        DasAuthRequest authRequest = (DasAuthRequest) request;
        VerifyTokenResponse operatorRes = (VerifyTokenResponse) input;

        DasAuthResponse response = new DasAuthResponse();
        response.setToken(input.getSessionId().toString());
        response.setAccountExtRef(operatorRes.getPlayerId().toString());
        response.setBalance(
          new DasMoney(
            operatorRes.getCurrency(),
            CommonUtils.fromCents(operatorRes.getBalance().longValue())));
        if (CommonUtils.isEmptyOrNull(operatorRes.getUserName())) {
          response.setUsername("ref-" + operatorRes.getPlayerId()); // operatorRes.getCustomerId());
        } else {
          response.setUsername(operatorRes.getUserName());
        }
        response.setCurrency(operatorRes.getCurrency());
        response.setTimestamp(new Date());
        response.setReqId(authRequest.getReqId());
        log.debug("DasAuthResponse: {}", response);
        output = response;
      } else if (request instanceof DasBalanceRequest) {
        DasBalanceRequest balRequest = (DasBalanceRequest) request;
        BalanceResponse operatorRes = (BalanceResponse) input;

        DasBalanceResponse response = new DasBalanceResponse();
        response.setToken(input.getSessionId().toString());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setTimestamp(new Date());
        response.setReqId(balRequest.getReqId());
        output = response;
      } else if (request instanceof DasTransactionRequest) {
        DasTransactionRequest txRequest = (DasTransactionRequest) request;
        TransactionResponse operatorRes = (TransactionResponse) input;

        DasTransactionResponse response = new DasTransactionResponse();
        response.setToken(input.getSessionId().toString());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setTimestamp(new Date());
        response.setReqId(txRequest.getReqId());
        output = response;
      } else if (request instanceof DasEndRoundRequest) {
        DasEndRoundRequest endRequest = (DasEndRoundRequest) request;
        TransactionResponse operatorRes = (TransactionResponse) input;

        DasEndRoundResponse response = new DasEndRoundResponse();
        response.setToken(input.getSessionId().toString());
        response.setBalance(CommonUtils.fromCents(operatorRes.getBalance().longValue()));
        response.setTimestamp(new Date());
        response.setReqId(endRequest.getReqId());
        output = response;
      } else {
        throw new ApplicationException("Unknown input, not mapped [%s]", input);
      }

      return output;
    }

    /**
     * Map Operator exception object to Dashur exception object
     *
     * @param ex
     * @return
     */
    static BaseException toException(WebApplicationException ex) {
      log.debug("handling WebApplicationException");
      if (Objects.isNull(ex) || Objects.isNull(ex.getResponse())) {
        log.error("error mapping exception. response is null", ex);
        return new ApplicationException("Unhandled exception mapping.");
      }

      if (!ex.getResponse().hasEntity()) {
        log.error(
            "error mapping exception. response doesn't contain body - status: [{}]",
            ex.getResponse().getStatus());
        return new ApplicationException("Unhandled exception mapping.");
      }

      ErrorResponse errorRes = ex.getResponse().readEntity(ErrorResponse.class);
      if (Objects.isNull(errorRes) || Objects.isNull(errorRes.getCode())) {
        log.error(
            "error mapping exception. response body is empty - status: [{}]",
            ex.getResponse().getStatus());
        return new ApplicationException("Unhandled exception mapping.");
      }
      return toException(errorRes);
      
    }

    /**
     * Map Operator exception object to Dashur exception object
     *
     * @param ex
     * @return
     */
    static BaseException toException(ErrorResponse errorRes) {
      log.debug("handling ErrorResponse {}", errorRes.toString());
      switch (errorRes.getCode()) {
      case "INVALID_TOKEN":
      case "SESSION_EXPIRED":
        return new AuthException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      case "BLOCKED_FROM_PRODUCT":
      case "IP_BLOCKED":
      case "DAILY_TIME_LIMIT":
      case "WEEKLY_TIME_LIMIT":
      case "MONTHLY_TIME_LIMIT":
      case "SPENDING_BUDGET_EXCEEDED":
        return new CustomException(errorRes.getCode(), errorRes.getCode());
      case "CUSTOM_ERROR":
        return new CustomException(errorRes.getCode(), CommonUtils.jsonToString(errorRes));
      case "INSUFFICIENT_FUNDS":
        return new PaymentException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      case "TRANSACTION_NOT_FOUND":
      case "INVALID_TXID":
        return new EntityNotExistException(
            "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
      }

      /*
       * All other error codes:
       *  INVALID_PARAMETERS => Something wrong with the request parameters.
       *  TRANSACTION_DECLINED => Operator declined the withdraw transaction. Transaction shall not be rollbacked.
       *  RC_SESSION_EXPIRED => Reality check is due.
       *  RETRY => Platform shall retry the transaction (deposit or rollback).
       *  ROLLBACK => Platform shall rollback the withdraw.
       *  MAINTENANCE => Scheduled maintenance is ongoing.
       *  UNHANDLED => Final fallback error code.
       *  SETUP_TIMEOUT => Requests are piling up.
       *  CHANNEL_MISSING => channel in data object is missing.
       *  FREESPINSID_MISSING => freespinsid in data object is missing.
       *  GAME_SESSION_MISMATCH => Returned in withdraw if sessionid in request was used for another gameref.
       *
       *  CUSTOM_ERROR => Custom error messages can be sent for example for special limits.
       */
      return new ApplicationException(
          "Connector response [%s] - [%s] - events [%s]", 
            errorRes.getCode(), errorRes.getMessage(), errorRes.getEvents());
    }


    /**
     * successful http status code
     */
    static boolean isSuccess(int status) {
      return status >= 200 && status <= 299;
    } 

    /**
     * Map operator http status code to Dashur exception object
     */
    static BaseException toException(int status) {
      if (status < 500) { // 4xx, faulty request, shall not retry or rollback 
        return new ApplicationException(toMessage(status));
      }
      // 5xx, unknown error, retry or rollback
      return new PaymentException(toMessage(status));
    }

    /**
     * status code to message
     */
    static String toMessage(int status) {
      return String.format("ClientService http status code [%d]", status);
    }

    /**
     * Get the opr_meta map from a request
     *
     * @param request
     * @return
     */
    static Map<String,Object> getMetaData(DasRequest request) {
      if (Objects.nonNull(request.getCtx())) {
        return (Map<String,Object>)request.getCtx().getOrDefault(
          com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_OPR_META,
          new HashMap<>());
      }
      return new HashMap<String,Object>();
    }

    /**
     * removePrefix
     *
     * @param prefixedString
     * @param prefix
     * @return prefixedString without the prefx
     */
    static String removePrefix(String prefixedString, String prefix) {
      if (!CommonUtils.isEmptyOrNull(prefixedString)) {
        if (prefixedString.startsWith(prefix)) {
          return prefixedString.substring(prefix.length());
        }
        return prefixedString;
      }
      return null;
    }

    /**
     * getItemId
     * 
     * @param gameRef
     * @return Dashur itemId
     */
    static Long getItemId(String gameRef) {
      String[] parts = gameRef.split("\\.");
      if (parts.length > 0) {
        return Long.parseLong(parts[parts.length-1]);
      }
      throw new ValidationException("gameRef is malformed [%s]", gameRef);
    }

    /**
     * toDate
     * 
     * @param zonedDateTime
     * @return Date
     */
    static Date toDate(ZonedDateTime zonedDateTime) {
      return Date.from(zonedDateTime.toInstant());
    }

    /**
     * getPromoCode
     * 
     * @param campaignExtRef
     * @return promo code
     */
    static String getPromoCode(String campaignExtRef) {
      if (!CommonUtils.isEmptyOrNull(campaignExtRef)) {
        int idx = campaignExtRef.indexOf(":");
        if (idx >= 0) {
          String promoCode = campaignExtRef.substring(idx+1);
          if (promoCode.length() > 0) {
            if (!promoCode.contains(RelaxGamingConfiguration.NOPROMO_PREFIX)){
              return promoCode;
            }
          }
        }
      }
      return null;
    }

  }

}
