package com.dashur.integration.extw.connectors.relaxgaming;

import com.dashur.integration.commons.RequestContext;
import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.ValidationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.exception.*;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.commons.rest.CampaignClientService;
import com.dashur.integration.commons.rest.LauncherClientService;
import com.dashur.integration.commons.rest.model.TransactionRoundModel;
import com.dashur.integration.commons.rest.model.TransactionFeedModel;
import com.dashur.integration.commons.rest.model.CampaignCreateModel;
import com.dashur.integration.commons.rest.model.CampaignUpdateModel;
import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.rest.model.SimpleAccountModel;
import com.dashur.integration.commons.rest.model.RestResponseWrapperModel;
import com.dashur.integration.commons.rest.model.CampaignBetLevelModel;
import com.dashur.integration.commons.rest.model.SimplifyLauncherItemModel;
import com.dashur.integration.commons.rest.model.CampaignAssignmentModel;
import com.dashur.integration.commons.domain.DomainService;
import com.dashur.integration.commons.domain.CommonService;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
/*
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GameInfo;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.Credentials;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.ServiceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetGamesResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetReplayRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetReplayResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetStateRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetStateResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.FreeRound;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.FreeRoundsInfo;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.FeatureTriggerInfo;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.AddFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.AddFreeRoundsResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.GetFreeRoundsResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.CancelFreeRoundsRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.service.CancelFreeRoundsResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotionAddRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotionAddResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotionData;
import com.dashur.integration.extw.connectors.relaxgaming.data.AckPromotion;
*/
import com.dashur.integration.extw.connectors.relaxgaming.data.service.*;
import com.dashur.integration.extw.connectors.relaxgaming.data.*;
import com.dashur.integration.extw.rgs.RgsService;
import com.dashur.integration.extw.rgs.RgsServiceProvider;
import com.dashur.integration.extw.rgs.data.GameHash;
import com.dashur.integration.extw.rgs.data.PlaycheckExtRequest;
import com.dashur.integration.extw.rgs.data.PlaycheckExtResponse;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;
import java.util.UUID;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpRequest;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@Path("/v1/extw/exp/relaxgaming")
public class RelaxGamingController {

  @Inject ExtwIntegConfiguration config;

  @Inject ConnectorServiceLocator connectorLocator;

  @Inject Service service;

  @Inject DomainService domainService;

  @Inject CommonService commonService;

  @Inject RgsService rgsService;

  @Context HttpRequest request;

  @Inject @RestClient CampaignClientService campaignClientService;

  @Inject @RestClient LauncherClientService launcherClientService;


  private RelaxGamingConfiguration relaxConfig;

  @PostConstruct
  public void init() {
    relaxConfig = config.configuration(RelaxGamingConfiguration.OPERATOR_CODE, RelaxGamingConfiguration.class);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  /**
   * RelaxGaming launch game url
   *
   * @param gameid
   * @param ticket
   * @param jurisdiction
   * @param lang
   * @param channel
   * @param partnerid
   * @param moneymode
   * @param currency
   * @param clientid
   * @param homeurl
   * @param hidehome
   * @param fullscreen
   * @param plurl
   * @param rg_account_uri
   * @param accountlabel
   * @param rcinterval
   * @param rcelapsed
   * @param rchistoryurl
   * @param rcenable
   * @param rciframeurl 
   * @param sessiontimer
   * @param sessionresult
   * @param sessiontimelimit
   * @param sessionlapsed
   * @param sessionrcinterval
   * @param sessionwagered
   * @param sessionwon
   * @param sessionlostlimit
   * @param sessiontimewarninglimit
   * @param sessionlosswarninglimit
   * @param sessionshowsummary
   * @param sessiontimer
   * @param sessionresult
   * @return
   */
  @GET
  @Path("/launch")
  public Response getLauncher(
      @QueryParam("gameid") String gameId,
      @QueryParam("ticket") String token,
      @QueryParam("lang") String language,
      @QueryParam("channel") String channel,
      @QueryParam("partnerid") String partnerId,
      @QueryParam("moneymode") String mode,
      @QueryParam("currency") String demoCurrency,
      @QueryParam("clientid") String clientId,
      @QueryParam("homeurl") @DefaultValue("") String lobbyUrl,
      @QueryParam("rcenable") @DefaultValue("") String rcEnable,
      @QueryParam("rciframeurl") @DefaultValue("") String rciFrameUrl,
      @QueryParam("rcinterval") @DefaultValue("") String rcInterval,
      @QueryParam("rcelapsed") @DefaultValue("") String rcElapsed,
      @QueryParam("rchistoryurl") @DefaultValue("") String rcHistoryUrl
      ) {
    try {
      String callerIp = CommonUtils.resolveIpAddress(this.request);

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/launch - [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}]",
            gameId,
            token,
            language,
            channel,
            partnerId,
            mode,
            demoCurrency,
            clientId,
            lobbyUrl,
            callerIp,
            rcEnable,
            rciFrameUrl);
      }

      if (CommonUtils.isEmptyOrNull(clientId)) {
        clientId = "mobile_app";
        log.info("setting client if to {}", clientId);
      }

      return getLauncherInternal(
        gameId, 
        token, 
        language, 
        channel, 
        partnerId, 
        mode, 
        demoCurrency,
        clientId, 
        lobbyUrl,
        callerIp,
        rcEnable,
        rciFrameUrl,
        rcInterval,
        rcElapsed,
        rcHistoryUrl);
    } catch (Exception e) {
      log.error("Unable to launch game [{}] - [{}]", gameId, partnerId, e);
      return Response.serverError()
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.launch.error.title", getLocale(language)),
                  CommonUtils.getI18nMessages("msg.launch.error.description", getLocale(language))))
          .build();
    }
  }

  @POST
  @Path("/games/getgames")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getGames(
    @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final ServiceRequest request) {
    if (!authenticate(auth, request.getCredentials().getPartnerId())) {
      return Response.status(401).build();
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "/v1/extw/exp/relaxgaming/games/getgames - [{}] [{}]",
          request.getCredentials(),
          request.getJurisdiction());
    }

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, false);

    List<GameHash> rgsResp = getRgs().gameHashes("EUR");
    List<GameInfo> games = new ArrayList<GameInfo>();
    GetGamesResponse resp = new GetGamesResponse();
    for ( GameHash hash : rgsResp) {
      if (!hash.getItemId().isEmpty()) {
        GameInfo game = new GameInfo();
        game.setGameRef(getGameRef(hash.getItemId()));
        game.setName(hash.getTitle());
        game.setStudio(relaxConfig.getRgsProvider());
        List<Long> legalBetSizes = new ArrayList<Long>();
        for (BigDecimal stake : hash.getStakes().get("EUR")){
          legalBetSizes.add(CommonUtils.toCents(stake));
        }
        game.setLegalBetSizes(legalBetSizes);
        if (!CommonUtils.isEmptyOrNull(hash.getFlags()) && hash.getFlags().contains("campaign")) {
          FreeRoundsInfo freerounds = new FreeRoundsInfo();
          if (!CommonUtils.isEmptyOrNull(setting.getChannel())) {
            freerounds.setChannels(new ArrayList<String>());
            String[] channels = setting.getChannel().split(",");
            for (String ch : channels) {
              freerounds.getChannels().add(ch);
            }
          }
          freerounds.setTypes(new ArrayList<String>());
          freerounds.getTypes().add("regular");
          FeatureTriggerInfo featureTriggers = new FeatureTriggerInfo();
          featureTriggers.setChannels(new ArrayList<String>());
          freerounds.setFeatureTriggers(featureTriggers);
          log.debug("available free rounds: {}", freerounds);
          game.setFreespins(freerounds);
        }
        games.add(game);
      }
    }
    resp.setGames(games);

    return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
  }

  @POST
  @Path("/round/getstate")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getState(
    @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final GetStateRequest request) {
    if (!authenticate(auth, request.getCredentials().getPartnerId())) {
      return Response.status(401).build();
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "/v1/extw/exp/relaxgaming/round/getstate - [{}] [{}] [{}]",
          request.getCredentials(),
          request.getRoundId(),
          request.getJurisdiction());
    }

    GetStateResponse resp = new GetStateResponse();

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

    RequestContext ctx = RequestContext.instance();
    ctx = ctx.withAccessToken(
          commonService.companyAppAccessToken(
              ctx, 
              setting.getLauncherAppClientId(), 
              setting.getLauncherAppClientCredential(), 
              setting.getLauncherAppApiId(), 
              setting.getLauncherAppApiCredential()));

    TransactionRoundModel round = domainService.findTransactionRoundByRoundExtRef(ctx, 
      getPrefixedRoundId(request.getRoundId()));


    if (!round.getMetaData().containsKey("item_id")) {
      return Response.serverError().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    }
    resp.setClosedTime(round.getCloseTime().toString());
    resp.setGameRef(getGameRef(round.getMetaData().get("item_id").toString()));
    resp.setTotalWinAmount(round.getSumOfPayout().longValue());
    return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
  }


  @POST
  @Path("/playcheck")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getPlaycheck(
    @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/playcheck - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);
      
      String url =
          service.playcheckUrl(
              RequestContext.instance(),
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential(),
              getPrefixedRoundId(request.getRoundId()));

      GetReplayResponse resp = new GetReplayResponse();
      resp.setReplayUrl(url);
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to get playcheck [{}] - [{}]", 
        request.getCredentials().getPartnerId(), request.getRoundId(), e);
        return Response.status(500).build();
    }
  }


  @POST
  @Path("/replay/get")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getReplay(
    @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/replay/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      RequestContext ctx = RequestContext.instance();
      ctx = ctx.withAccessToken(
            commonService.companyAppAccessToken(
                ctx, 
                setting.getLauncherAppClientId(), 
                setting.getLauncherAppClientCredential(), 
                setting.getLauncherAppApiId(), 
                setting.getLauncherAppApiCredential()));

      TransactionRoundModel round = domainService.findTransactionRoundByRoundExtRef(ctx, 
        getPrefixedRoundId(request.getRoundId()));
      TransactionFeedModel feed = domainService.findTransactionFeedById(ctx, round.getId());
      
      PlaycheckExtRequest playcheckReq = new PlaycheckExtRequest();
      List<TransactionFeedModel> feeds = new ArrayList<TransactionFeedModel>();
      feeds.add(feed);
      playcheckReq.setFeeds(feeds);

      PlaycheckExtResponse playcheckResp = getRgs().playcheckExt(playcheckReq);

      GetReplayResponse resp = new GetReplayResponse();
      resp.setReplayUrl(playcheckResp.getUrl());
      resp.setRoundStart(toZonedDateTime(round.getStartTime()));
      resp.setRoundEnd(toZonedDateTime(round.getCloseTime()));
      resp.setBetAmount(round.getSumOfWager().longValue());
      resp.setWinAmount(round.getSumOfPayout().longValue());
      resp.setCurrency(round.getCurrencyUnit());
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to get replay [{}] - [{}]", 
        request.getCredentials().getPartnerId(), getPrefixedRoundId(request.getRoundId()), e);
        return Response.status(500).build();
    }
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/add")
  public Response addFreespins(
      @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final AddFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    Long itemId = 0L;
    Long vendorId = 0L;
    String promoCode = null;
    String partnerId = null;
    String currency = null;
    String campaignExtRef = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/add - [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getGameRef(),
            request.getPlayerId());
      }

      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);

      currency = request.getCurrency();
      if (Strings.isNullOrEmpty(currency)) {
        currency = RelaxGamingConfiguration.DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", currency);
      }

      itemId = RelaxGamingConnectorServiceImpl.Utils.getItemId(request.getGameRef());
        // relax-api:promoCode
      campaignExtRef = String.format("%sapi:", RelaxGamingConfiguration.CAMPAIGN_PREFIX);
      if (!CommonUtils.isEmptyOrNull(request.getPromoCode())) {
        campaignExtRef += request.getPromoCode();
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/add - [{}] [{}] [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId(),
            currency,
            itemId,
            campaignExtRef);
      }

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));

/*
      CampaignModel campaign = null;

      RestResponseWrapperModel<List<String>> currencyResp = campaignClientService.currency(
        CommonUtils.authorizationBearer(ctx.getAccessToken()),
        ctx.getTimezone(),
        ctx.getCurrency(),
        ctx.getUuid().toString(),
        ctx.getLanguage(),
        itemId);
      if (!currencyResp.getData().contains(currency)) {
        throw new ValidationException("game %s does not accept %s as campaign currency", itemId, currency);
      }

      RestResponseWrapperModel<CampaignBetLevelModel> betlevelResp = campaignClientService.betLevel(
        CommonUtils.authorizationBearer(ctx.getAccessToken()),
        ctx.getTimezone(),
        ctx.getCurrency(),
        ctx.getUuid().toString(),
        ctx.getLanguage(),
        itemId,
        currency);

      int level = 1;
      for (BigDecimal amount : betlevelResp.getData().getLevels()) {
        if (amount.multiply(BigDecimal.valueOf(100L)).longValue() == request.getFreespinValue()) {
          break;
        }
        level++;
      }
      if (level >= betlevelResp.getData().getLevels().size()) {
        throw new ValidationException("bet amount of %f is not a valid level: %s", 
          (double)request.getFreespinValue()/100.0, 
          betlevelResp.getData().getLevels().toString());
      }

      Calendar now = Calendar.getInstance();
      now.add(Calendar.MINUTE, 1);

      CampaignCreateModel create = new CampaignCreateModel();
      create.setEndTime(RelaxGamingConnectorServiceImpl.Utils.toDate(request.getExpires()));
      create.setGameId(itemId);
      create.setName(campaignExtRef);
      create.setNumOfGames(request.getAmount());
      create.setExtRef(campaignExtRef);
      create.setAccountId(setting.getCompanyId());
      create.setStatus(CampaignCreateModel.Status.ACTIVE);
      create.setType(CampaignCreateModel.Type.FREE_GAMES);
      create.setBetLevel(level);
      create.setCurrency(currency);
      create.setStartTime(now.getTime());

      campaign = domainService.createCampaign(ctx, create);
*/
      CampaignModel campaign = createCampaign(ctx,
        campaignExtRef,
        currency,
        itemId,
        request.getFreespinValue(),
        RelaxGamingConnectorServiceImpl.Utils.toDate(request.getExpires()),
        request.getAmount(),
        setting.getCompanyId());

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException("Campaign not exist, despite created. Please check.");
      }

      /*
      if (Objects.isNull(campaign.getVendorRef())) {
        log.info("Campaign setup is not ready. Please try again later");
        AddFreeRoundsResponse resp = new AddFreeRoundsResponse();
        resp.setFreespinsId(campaign.getId().toString());
        return Response.serverError()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }
      */

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%d] does not exists", request.getPlayerId());
      }

      /*
      domainService.addCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));
      */
      CampaignAssignmentModel assignMember = new CampaignAssignmentModel();
      assignMember.setAccountExtRef(request.getPlayerId().toString());
      assignMember.setCampaignId(campaign.getId());
      assignMember.setGameId(itemId);
      domainService.assignCampaignMember(
        ctx, assignMember);

      AddFreeRoundsResponse resp = new AddFreeRoundsResponse();
      resp.setTxId(request.getTxId());
      resp.setFreespinsId(campaign.getId().toString());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to addFreespins [{}] - [{}] - [{}]", RelaxGamingConfiguration.OPERATOR_CODE, partnerId, e);
      return Response.serverError()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/get")
  public Response getFreespins(
      @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final GetFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    String campaignExtRef = null;
    String partnerId = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId());
      }
      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));

      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%d] does not exists", request.getPlayerId());
      }

      List<CampaignModel> campaigns = null;
      try {
        campaigns = domainService.availableCampaigns(ctx, memberAccount.getId(), true);
      } catch (EntityNotExistException e) {
        // don't do anything.
      }

      // avoid re-fetching bet levels in case of multiple campaigns
      Map<Long,CampaignBetLevelModel> betLevelMap = Maps.newHashMap();

      GetFreeRoundsResponse resp = new GetFreeRoundsResponse();
      if (Objects.nonNull(campaigns)) {
        List<FreeRound> freeRounds = new ArrayList<FreeRound>();
        for (CampaignModel m : campaigns) {

          Integer remaining = m.getNumOfGames();
          if (m.getMetaData().containsKey("spins_count")) {
            Map<String, Object> spinsCount = (Map<String,Object>)m.getMetaData().get("spins_count");
            if (spinsCount.containsKey("remaining")) {
              remaining = (Integer)spinsCount.get("remaining");
              log.debug("meta data remaining spins {}", remaining);
            }
          }

          if (m.getStatus() == CampaignModel.Status.ACTIVE && remaining > 0) {

            if (m.getName().startsWith(RelaxGamingConfiguration.CAMPAIGN_PREFIX)) {

              if (!betLevelMap.containsKey(m.getGameId())) {
                RestResponseWrapperModel<CampaignBetLevelModel> betLevelResp = campaignClientService.betLevel(
                  CommonUtils.authorizationBearer(ctx.getAccessToken()),
                  ctx.getTimezone(),
                  ctx.getCurrency(),
                  ctx.getUuid().toString(),
                  ctx.getLanguage(),
                  m.getGameId(),
                  m.getCurrency());
                betLevelMap.put(m.getGameId(), betLevelResp.getData());
              }

              FreeRound r = new FreeRound();
              int level = 1;
              for (BigDecimal amount : betLevelMap.get(m.getGameId()).getLevels()) {
                if (level == m.getBetLevel()) {
                  r.setFreespinValue(amount.multiply(BigDecimal.valueOf(100L)).longValue());
                  break;
                }
                level++;
              }

              r.setExpires(toZonedDateTime(m.getEndTime()));
              r.setGameRef(getGameRef(m.getGameId().toString()));
              r.setAmount(remaining);
              r.setFreespinsId(m.getId().toString());
              r.setPromoCode(RelaxGamingConnectorServiceImpl.Utils.getPromoCode(m.getExtRef()));
              r.setCreateTime(toZonedDateTime(m.getCreated()));
              r.setCurrency(m.getCurrency());
              freeRounds.add(r);
            }
          }
        }
        resp.setFreespins(freeRounds);
      }
      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();

    } catch (Exception e) {
      log.error("Unable to getFreespins [{}] - [{}] - [{}]", RelaxGamingConfiguration.OPERATOR_CODE, partnerId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/freespins/cancel")
  public Response cancelFreespins(
      @HeaderParam(RelaxGamingConfiguration.AUTHORIZATION) String auth, final CancelFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    String partnerId = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/relaxgaming/freespins/cancel - [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getFreespinsId(),
            request.getPlayerId());
      }      
      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RelaxGamingConfiguration.CompanySetting setting =
          getCompanySettings(partnerId, true);

      ctx =
          ctx.withAccessToken(
              commonService.companyAppAccessToken(
                  ctx,
                  setting.getLauncherAppClientId(),
                  setting.getLauncherAppClientCredential(),
                  setting.getLauncherAppApiId(),
                  setting.getLauncherAppApiCredential()));
      CampaignModel campaign = null;
      try {
        RestResponseWrapperModel<CampaignModel> result = campaignClientService.get(
          CommonUtils.authorizationBearer(ctx.getAccessToken()),
          ctx.getTimezone(),
          ctx.getCurrency(),
          ctx.getUuid().toString(),
          ctx.getLanguage(),
          request.getFreespinsId());
        campaign = result.getData();

      } catch (EntityNotExistException e) {
        // don't do anything.
      }

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException(
            "Campaign not exist, cannot cancel freespins. Please check.");
      }
/*
      if (Objects.isNull(campaign.getVendorRef())) {
        CancelFreeRoundsResponse resp = new CancelFreeRoundsResponse();
        resp.setFreespinsId(campaign.getId().toString());
        return Response.serverError()
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity(resp)
            .build();
      }
*/      
      SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, request.getPlayerId().toString());
      if (Objects.isNull(memberAccount)) {
        throw new EntityNotExistException("User with ext-ref [%s] does not exists", request.getPlayerId());
      }

      domainService.delCampaignMembers(
          ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

      CampaignUpdateModel update = new CampaignUpdateModel();
      update.setName(campaign.getName());
      update.setNumOfGames(campaign.getNumOfGames());
      update.setStatus(CampaignUpdateModel.Status.CLOSED);
      update.setType(CampaignUpdateModel.Type.FREE_GAMES);
      update.setGameId(campaign.getGameId());
      update.setBetLevel(campaign.getBetLevel());
      update.setStartTime(campaign.getStartTime());
      update.setEndTime(campaign.getEndTime());
      update.setMetaData(campaign.getMetaData());
      update.setVersion(campaign.getVersion());

      domainService.updateCampaign(ctx, campaign.getId(), update);


      CancelFreeRoundsResponse resp = new CancelFreeRoundsResponse();
      resp.setFreespinsId(request.getFreespinsId());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to cancelFreespins [{}] - [{}] - [{}]", RelaxGamingConfiguration.OPERATOR_CODE, partnerId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }




  /**
   * Internal method for launching game
   *
   * @param gameId
   * @param token
   * @param language
   * @param channel
   * @param partnerId
   * @param mode
   * @param demoCurrency
   * @param clientId
   * @param lobbyUrl
   * @param callerIp
   * @param rcEnable
   * @param rciFrameUrl
   * @return
   */
  private Response getLauncherInternal(
      String gameId,
      String token,
      String language,
      String channel,
      String partnerId,
      String mode,
      String demoCurrency,
      String clientId,
      String lobbyUrl,
      String callerIp,
      String rcEnable,
      String rciFrameUrl,
      String rcInterval,
      String rcElapsed,
      String rcHistoryUrl
      ) {

    RelaxGamingConfiguration.CompanySetting setting = getCompanySettings(partnerId, false);

    if (!CommonUtils.isEmptyOrNull(setting.getChannel())) {
      String[] channels = setting.getChannel().split(",");
      boolean found = false;
      for (String ch : channels) {
        if (ch.equals(channel)) {
          found = true;
          break;
        }
      }
      if (!found) {
        throw new ValidationException("channel [%s] is not one of the configured channels [%s]", channel, setting.getChannel());
      /*
        log.warn("channel {} is not equal to configured channel {} for partnerId {}", 
          channel, setting.getChannel(), partnerId);
      */
      }
    }

    Boolean isDemo = mode.equals("fun");

    RequestContext ctx = RequestContext.instance()
                                       .withLanguage(language);
    if (isDemo) {
      if (Strings.isNullOrEmpty(demoCurrency)) {
        demoCurrency = RelaxGamingConfiguration.DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", demoCurrency);
      }
      log.info("launching in demo mode with currency {}", demoCurrency);
      ctx = ctx.withCurrency(demoCurrency);
    }
    ctx.getMetaData().put("clientId", clientId);
    ctx.getMetaData().put("gameRef", getGameRef(gameId));
    ctx.getMetaData().put("channel", channel);
    log.debug("launcher request context: {}", ctx.getMetaData());

    ctx =
        ctx.withAccessToken(
            commonService.companyAppAccessToken(
                ctx, 
                setting.getLauncherAppClientId(), 
                setting.getLauncherAppClientCredential(), 
                setting.getLauncherAppApiId(), 
                setting.getLauncherAppApiCredential()));


    List<Long> campaignIds = null;
    if (!isDemo) {
      try {
        RelaxGamingConnectorServiceImpl connector = (RelaxGamingConnectorServiceImpl)connectorLocator
            .getConnector(RelaxGamingConfiguration.OPERATOR_CODE);

        RelaxGamingClientService clientService = getClientService(setting.getCompanyId());

        if (Objects.isNull(clientService)) {
          log.info("could not aquire a connector service for calling verifytoken");
        } else {
          VerifyTokenRequest operatorReq = new VerifyTokenRequest();
          operatorReq.setChannel(channel);
          operatorReq.setClientId(clientId);
          operatorReq.setToken(token);
          operatorReq.setGameRef(gameId);
          operatorReq.setPartnerId(setting.getPartnerId());
          operatorReq.setIp(callerIp);
          String auth = setting.getOperatorCredential();
          javax.ws.rs.core.Response res = clientService.verifyToken(auth, setting.getPartnerId(), operatorReq);
          if (!RelaxGamingConnectorServiceImpl.Utils.isSuccess(res.getStatus())) {
            log.error("verifytoken request error. Skip checking promotions and launch anyway.");
          } else {
            VerifyTokenResponse operatorRes = res.readEntity(VerifyTokenResponse.class);
            if (Objects.isNull(operatorRes)) {
              log.error("Could not read VerifyTokenResponse. Skip checking promotions and launch anyway.");
            } else {
              // ubo promotions test code
              /*
              List<Promotion> promotions = new ArrayList<Promotion>();
              Promotion p = new Promotion();
              p.setPromotionType("freerounds");
              p.setPromotionId(10000L + (Math.abs(new Random(new Date().getTime()).nextLong()) % 10000L));
              p.setTxId(UUID.randomUUID().toString());
              p.setPlayerId(vtres.getPlayerId());
              p.setPartnerId(vtres.getPartnerId());
              p.setGameRef(req.getGameRef());
              p.setAmount(10);
              p.setFreespinValue(100L);
              p.setExpires(ZonedDateTime.now().plus(1, ChronoUnit.DAYS));
              p.setPromoCode("ubopromo-" + UUID.randomUUID().toString());
              promotions.add(p);
              log.info("adding test promotion {}", p);
              operatorRes.setPromotions(promotions);
              */              
              campaignIds = ackPromotions(setting.getCompanyId(), operatorReq, operatorRes);
            }
          }
        }
      } catch(Exception e) {
        log.error("Ignoring exception while processing promotions", e);
      }
    }

    String url;
    RestResponseWrapperModel<String> result;
    try {
      SimplifyLauncherItemModel rq = new SimplifyLauncherItemModel();
      rq.setDemo(isDemo);
      if(!isDemo) rq.setToken(token);
      rq.setAppId(setting.getLauncherItemApplicationId());
      rq.setItemId(Long.parseLong(gameId));
      rq.setCampaigns(campaignIds);
      rq.setExternal(Boolean.TRUE);

      Map<String, Object> confParams = Maps.newHashMap();

      if (!CommonUtils.isEmptyOrNull(lobbyUrl)) {
        confParams.put("lobby_url", lobbyUrl);
      }

      if (!CommonUtils.isEmptyOrNull(demoCurrency)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "currency", demoCurrency);
      }

      if (!CommonUtils.isEmptyOrNull(rcEnable)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "rcenable", rcEnable);
      }

      if (!CommonUtils.isEmptyOrNull(rciFrameUrl)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "rciframeurl", rciFrameUrl);
      }

      if (!CommonUtils.isEmptyOrNull(rcInterval)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "rcinterval", rcInterval);
      }

      if (!CommonUtils.isEmptyOrNull(rcElapsed)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "rcelapsed", rcElapsed);
      }

      if (!CommonUtils.isEmptyOrNull(rcHistoryUrl)) {
        confParams.put(RelaxGamingConfiguration.CONF_PARAMS_PREFIX + "rcHistoryUrl", rcHistoryUrl);
      }

      if (!confParams.isEmpty()) {
        rq.setConfParams(confParams);
      }

      if (!CommonUtils.isEmptyOrNull(ctx.getLanguage()) || !CommonUtils.isEmptyOrNull(callerIp)) {
        rq.setCtx(Maps.newHashMap());
      }

      if (!CommonUtils.isEmptyOrNull(ctx.getLanguage())) {
        rq.getCtx().put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_LANG, ctx.getLanguage());
      }

      if (!CommonUtils.isEmptyOrNull(callerIp)) {
        Map<String, Object> meta = Maps.newHashMap(ctx.getMetaData());
        meta.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_GAME_ID, Long.parseLong(gameId));
        meta.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS, callerIp);
        rq.getCtx().put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_META_DATA, meta);
      }

      result =
          launcherClientService.launch(
              CommonUtils.authorizationBearer(ctx.getAccessToken()),
              ctx.getTimezone(),
              ctx.getCurrency(),
              ctx.getUuid().toString(),
              ctx.getLanguage(),
              rq);
    } catch (WebApplicationException e) {
      throw com.dashur.integration.commons.domain.impl.DomainServiceImpl.Error(e);
    }

    if (result.hasError()) {
      throw new ApplicationException(
          200,
          (Integer) result.getError().getOrDefault("code", 500),
          "Unable to call remote services, un-classified error arises = [%s]",
          result.getError().getOrDefault("message", "Error arises but unable to find details"));
    }

    url = result.getData();

    try {
      return Response.temporaryRedirect(new URI(url)).build();
    } catch (URISyntaxException e) {
      log.error("Unable to convert url to uri [{}]", url);
      throw new ApplicationException("Unable to convert url to uri");
    }
  }

  /**
   * ackPromotions from verifytoken response before launch
   * 
   * @param companyId
   * @param request
   * @param response
   * @return campaignId
   */
  private List<Long> ackPromotions(Long companyId, 
    VerifyTokenRequest request, VerifyTokenResponse response) {
    AckPromotionAddRequest ackRequest = null;
    List<Long> campaignIds = null;

    Long campaignId = null;
    String currency = response.getCurrency();

    if (Objects.nonNull(response.getPromotions())) {
      for (Promotion p : response.getPromotions()) {
        if (p.getPromotionType() == "freerounds" && // or "featuretrigger"
          p.getGameRef() == request.getGameRef() &&
          p.getPlayerId() == response.getPlayerId()) {     

          if (Objects.isNull(ackRequest)) {
            ackRequest = new AckPromotionAddRequest();
            ackRequest.setPromotions(new ArrayList<AckPromotion>());
          }
          if (Objects.isNull(campaignIds)) {
            campaignIds = new ArrayList<Long>();
          }

          // relax-[promotionId]:promoCode
          String campaignExtRef = String.format("%s%d:", relaxConfig.CAMPAIGN_PREFIX, p.getPromotionId());
          if (!CommonUtils.isEmptyOrNull(p.getPromoCode())) {
            campaignExtRef += p.getPromoCode();
          }
          campaignId = createOrJoinCampaign(companyId, campaignExtRef, currency, p);
          campaignIds.add(campaignId);

          AckPromotion ackPromotion = new AckPromotion();
          AckPromotionData data = new AckPromotionData();
          data.setChannel(request.getChannel());
          data.setFreespinsId(campaignId.toString());
          ackPromotion.setPlayerId(response.getPlayerId());
          ackPromotion.setPromotionId(p.getPromotionId());
          ackPromotion.setTxId(p.getTxId());
          ackPromotion.setData(data);
          ackRequest.getPromotions().add(ackPromotion);
        }
      }
    }

    if (Objects.nonNull(ackRequest)) {
      RelaxGamingConfiguration.CompanySetting setting = 
          relaxConfig.getCompanySettings().get(companyId);
      String auth = setting.getOperatorCredential();
      Integer partnerId = setting.getPartnerId();
      getClientService(companyId).ackPromotionAdd(auth, partnerId, ackRequest);
    }
    return campaignIds;
  }  

  /**
   * createOrJoinCampaign
   * 
   * @param companyId
   * @param campaignExtRef
   * @param currency
   * @param promotion
   * @return campaignId
   */
  private Long createOrJoinCampaign(Long companyId, 
    String campaignExtRef, String currency, Promotion promotion) {
    RelaxGamingConfiguration.CompanySetting setting = 
        relaxConfig.getCompanySettings().get(companyId);

    RequestContext ctx = RequestContext.instance();
    ctx = ctx.withAccessToken(
      commonService.companyAppAccessToken(
          ctx,
          setting.getLauncherAppClientId(),
          setting.getLauncherAppClientCredential(),
          setting.getLauncherAppApiId(),
          setting.getLauncherAppApiCredential()));

    CampaignModel campaign = null;
    try {
      campaign = domainService.searchCampaign(ctx, campaignExtRef);
    } catch (Exception e) {
      // do nothing
    }

    if (Objects.isNull(campaign)) {
      campaign = createCampaign(ctx,
        campaignExtRef,
        currency,
        RelaxGamingConnectorServiceImpl.Utils.getItemId(promotion.getGameRef()),
        promotion.getFreespinValue(),
        RelaxGamingConnectorServiceImpl.Utils.toDate(promotion.getExpires()),
        promotion.getAmount(),
        setting.getCompanyId());
    }

    if (Objects.isNull(campaign)) {
      throw new EntityNotExistException("Campaign not exist, despite created. Please check.");
    }

    SimpleAccountModel memberAccount = domainService.getAccountByExtRef(ctx, promotion.getPlayerId().toString());
    if (Objects.isNull(memberAccount)) {
      throw new EntityNotExistException("User with ext-ref [%d] does not exists", promotion.getPlayerId());
    }

    domainService.addCampaignMembers(
        ctx, campaign.getId(), Lists.newArrayList(memberAccount.getId().toString()));

    return campaign.getId();
  }


  /**
   * create campaign
   * 
   * @param ctx
   * @param campaignExtRef
   * @param currency
   * @param itemId
   * @param betAmount
   * @param expires
   * @param numGames
   * @param accountId
   * @return campaign
   */
  private CampaignModel createCampaign(
    RequestContext ctx,
    String campaignExtRef,
    String currency,
    Long itemId,
    Long betAmount,
    Date expires,
    Integer numGames,
    Long accountId) {

    RestResponseWrapperModel<List<String>> currencyResp = campaignClientService.currency(
      CommonUtils.authorizationBearer(ctx.getAccessToken()),
      ctx.getTimezone(),
      ctx.getCurrency(),
      ctx.getUuid().toString(),
      ctx.getLanguage(),
      itemId);
    if (!currencyResp.getData().contains(RelaxGamingConfiguration.DEFAULT_CURRENCY)) {
      throw new ValidationException("game %s does not accept %s as campaign currency", itemId, RelaxGamingConfiguration.DEFAULT_CURRENCY);
    }
    if (!currencyResp.getData().contains(currency)) {
      throw new ValidationException("game %s does not accept %s as campaign currency", itemId, currency);
    }

    RestResponseWrapperModel<CampaignBetLevelModel> betlevelResp = campaignClientService.betLevel(
      CommonUtils.authorizationBearer(ctx.getAccessToken()),
      ctx.getTimezone(),
      ctx.getCurrency(),
      ctx.getUuid().toString(),
      ctx.getLanguage(),
      itemId,
      RelaxGamingConfiguration.DEFAULT_CURRENCY);

    int level = 1;
    for (BigDecimal amount : betlevelResp.getData().getLevels()) {
      if (amount.multiply(BigDecimal.valueOf(100L)).longValue() == betAmount) {
        break;
      }
      level++;
    }
    if (level >= betlevelResp.getData().getLevels().size()) {
      throw new ValidationException("bet amount of %f is not a valid level: %s", 
        (double)betAmount/100.0, 
        betlevelResp.getData().getLevels().toString());
    }

    Calendar now = Calendar.getInstance();
    now.add(Calendar.SECOND, 1);

    CampaignCreateModel create = new CampaignCreateModel();
    create.setEndTime(expires);
    create.setGameId(itemId);
    create.setName(campaignExtRef);
    create.setNumOfGames(numGames);
    create.setExtRef(campaignExtRef);
    create.setAccountId(accountId);
    create.setStatus(CampaignCreateModel.Status.ACTIVE);
    create.setType(CampaignCreateModel.Type.FREE_GAMES);
    create.setBetLevel(level);
    create.setCurrency(currency);
    create.setStartTime(now.getTime());

    return domainService.createCampaign(ctx, create);
  }

  /**
   * get operator's company settings
   *
   * @param partnerId
   * @param validateIp
   * @return
   */
  private RelaxGamingConfiguration.CompanySetting getCompanySettings(
      String partnerId, boolean validateIp) {
    String operatorIdKey = String.format("ext-%s", partnerId);
    if (!relaxConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
      throw new ValidationException("no configuration found for operator-id [%s]", partnerId);
    }

    Long companyId = relaxConfig.getOperatorIdMap().get(operatorIdKey);

    if (!relaxConfig.getCompanySettings().containsKey(companyId)) {
      throw new ValidationException("no configuration found for company-id [%s]", companyId);
    }

    if (validateIp) {
      connectorLocator
          .getConnector(RelaxGamingConfiguration.OPERATOR_CODE)
          .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));
    }

    return relaxConfig.getCompanySettings().get(companyId);
  }

  /**
   * get locale for i18n.
   *
   * @param language
   * @return
   */
  private Locale getLocale(String language) {
    if (CommonUtils.isEmptyOrNull(language)) {
      return Locale.ENGLISH;
    }

    try {
      return new Locale(language);
    } catch (Exception e) {
      log.debug("Unable to resolve language, return default 'en'");
      return Locale.ENGLISH;
    }
  }

  /**
   * validate request
   * 
   * @param credentials
   * @return
   */
  private boolean authenticate(String auth, Integer partnerId) {
    if (!getCompanySettings(String.valueOf(partnerId), false).getOperatorCredential().equals(auth)) {
      log.error("Basic authentication failed. Invalid credentials.");
      return false;
    }
    return true;
  }

  /**
   * toZonedDateTime
   * 
   * @param Date
   * @return ZonedDateTime in UTC
   */
  private ZonedDateTime toZonedDateTime(Date date) {
    return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
  }

  /**
   * toFreespinsId
   * 
   * WARNING: do not use with UUID
   * 
   * @param campaignId
   * @return Date
   */
  private String toFreespinsId(String campaignId) {
    String[] parts = campaignId.split("\\-");
    if (parts.length < 2) {
      return "";
    }
    return parts[1];
  }


  /**
   * getGameRef
   * 
   * @param gameId
   * @return RelaxGaming gameRef string
   */
  private String getGameRef(String gameId) {
    return String.format("rlx.%s.%s.%s", 
      relaxConfig.getPlatform(), 
      relaxConfig.getGamestudio(),
      gameId);
  }

  /**
   * getPrefixedRoundId
   * 
   * @param roundId
   * @return Dashur roundId
   */
  private String getPrefixedRoundId(String roundId) {
    return RelaxGamingConfiguration.ROUND_PREFIX + roundId;
  }

  /**
   * getClientService
   * 
   * @param companyId
   * @return RelaxGamingClientService
   */
  private RelaxGamingClientService getClientService(Long companyId) {
    RelaxGamingConnectorServiceImpl connector = (RelaxGamingConnectorServiceImpl)connectorLocator.getConnector(
      RelaxGamingConfiguration.OPERATOR_CODE);
    if (Objects.isNull(connector)) {
      return null;
    }
    return connector.clientService(companyId);
  }

  /**
   * getRgs
   * 
   * @return rgs service provider
   */
  private RgsServiceProvider getRgs() {
    return rgsService.getProvider(relaxConfig.getRgsProvider());
  }

}
