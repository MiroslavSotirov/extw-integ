package com.dashur.integration.extw.connectors.raw;

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
import com.dashur.integration.commons.cache.CacheProvider;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.Service;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.connectors.raw.data.service.*;
import com.dashur.integration.extw.connectors.raw.data.*;
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
import java.util.Random;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
@Path("/v1/extw/exp/raw")
public class RawController {

  @Inject
  ExtwIntegConfiguration config;

  @Inject
  ConnectorServiceLocator connectorLocator;

  @Inject
  Service service;

  @Inject
  DomainService domainService;

  @Inject
  CommonService commonService;

  @Inject
  RgsService rgsService;

  @Context
  HttpRequest request;

  @Inject
  @RestClient
  LauncherClientService launcherClientService;

  @Inject
  CacheProvider cacheProvider;

  private RawConfiguration rawConfig;

  @PostConstruct
  public void init() {
    rawConfig = config.configuration(RawConfiguration.OPERATOR_CODE, RawConfiguration.class);

    cacheProvider.initCache(
        RawConfiguration.CACHE_NAME_PLAYER_CAMPAIGNS, String.class, String.class, 10_000, 7 * 24 * 60);
    cacheProvider.initCache(
        RawConfiguration.CACHE_NAME_PLAYERGAME_CAMPAIGN, String.class, Long.class, 10_000, 7 * 24 * 60);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/version")
  public String version() {
    return config.getVersion();
  }

  /**
   * Raw launch game url
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
      @QueryParam("siteId") String siteId,
      @QueryParam("gameId") String gameId,
      @QueryParam("currency") String currency,
      @QueryParam("locale") String locale,
      @QueryParam("gameMode") String gameMode,
      @QueryParam("serverAddress") String serverAddress,
      @QueryParam("apiAddress") String apiAddress,
      @QueryParam("channel") String channel,
      @QueryParam("groupingSeparator") String groupingSeparator,
      @QueryParam("decimalSeparator") String decimalSeparator) {
    try {

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/launch - [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}] [{}]",
            siteId,
            gameId,
            currency,
            locale,
            gameMode,
            serverAddress,
            apiAddress,
            channel,
            groupingSeparator,
            decimalSeparator);
      }

      return getLauncherInternal(
          siteId,
          gameId,
          currency,
          locale,
          gameMode,
          serverAddress,
          apiAddress,
          channel,
          groupingSeparator,
          decimalSeparator);
    } catch (Exception e) {
      log.error("Unable to launch game [{}] - [{}]", gameId, siteId, e);
      return Response.serverError()
          .entity(
              String.format(
                  "<html><header><title>%s</title></header><body><p>%s</p></body></html>",
                  CommonUtils.getI18nMessages("msg.launch.error.title", getLocale(locale)),
                  CommonUtils.getI18nMessages("msg.launch.error.description", getLocale(locale))))
          .build();
    }
  }

  @POST
  @Path("/round/getstate")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getState(
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final GetStateRequest request) {
    if (!authenticate(auth, request.getCredentials().getPartnerId())) {
      return Response.status(401).build();
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "/v1/extw/exp/raw/round/getstate - [{}] [{}] [{}]",
          request.getCredentials(),
          request.getRoundId(),
          request.getJurisdiction());
    }

    GetStateResponse resp = new GetStateResponse();

    String partnerId = String.valueOf(request.getCredentials().getPartnerId());
    RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

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
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/playcheck - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      String url = service.playcheckUrl(
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
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final GetReplayRequest request) {
    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/replay/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getRoundId());
      }

      String partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

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
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final AddFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    Long itemId = 0L;
    Long vendorId = 0L;
    String promoCode = null;
    String partnerId = null;
    String currency = null;
    String campaignExtRef = null;
    String accountExtRef = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/freespins/add - [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getGameRef(),
            request.getPlayerId());
      }

      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      currency = request.getCurrency();
      if (Strings.isNullOrEmpty(currency)) {
        currency = RawConfiguration.DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", currency);
      }

      itemId = RawConnectorServiceImpl.Utils.getItemId(request.getGameRef());
      // relax-api:promoCode
      promoCode = request.getPromoCode();
      if (CommonUtils.isEmptyOrNull(promoCode)) {
        promoCode = RawConfiguration.NOPROMO_PREFIX + UUID.randomUUID().toString();
      }
      campaignExtRef = String.format("%sapi:%s", RawConfiguration.CAMPAIGN_PREFIX, promoCode);
      accountExtRef = request.getPlayerId().toString();

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/freespins/add - [{}] [{}] [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId(),
            currency,
            itemId,
            campaignExtRef);
      }

      String cacheKey = getCampaignCacheKey(accountExtRef, itemId);
      if (Objects
          .nonNull(cacheProvider.get(RawConfiguration.CACHE_NAME_PLAYERGAME_CAMPAIGN, Long.class, cacheKey))) {
        log.error("player-campaign cache contains key [{}] already", cacheKey);
        return Response.status(Response.Status.FORBIDDEN)
            .type(MediaType.APPLICATION_JSON)
            .encoding("utf-8")
            .entity("A campaign exists for this player and game")
            .build();
      }

      ctx = ctx.withAccessToken(
          commonService.companyAppAccessToken(
              ctx,
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential()));

      SimpleAccountModel memberAccount = null;
      try {
        memberAccount = domainService.getAccountByExtRef(ctx, accountExtRef);
      } catch (EntityNotExistException e) {
      }

      if (Objects.nonNull(memberAccount)) {

        List<CampaignModel> campaigns = null;
        try {
          campaigns = domainService.availableCampaigns(ctx, memberAccount.getId(), true);
        } catch (EntityNotExistException e) {
          // don't do anything.
        }

        if (Objects.nonNull(campaigns)) {

          for (CampaignModel m : campaigns) {

            if (m.getGameId().equals(itemId)) {

              if (m.getName().startsWith(RawConfiguration.CAMPAIGN_PREFIX)) {

                if (m.getStatus().equals(CampaignModel.Status.ACTIVE)) {

                  Integer remaining = m.getNumOfGames();
                  log.debug("campaign is active with an initial amount of {} spins", remaining);
                  if (m.getMetaData().containsKey("spins_count")) {
                    Map<String, Object> spinsCount = (Map<String, Object>) m.getMetaData().get("spins_count");
                    if (spinsCount.containsKey("remaining")) {
                      remaining = (Integer) spinsCount.get("remaining");
                    }
                  }
                  if (remaining > 0) {

                    log.error("only one campaign can be active for player and game combination");
                    return Response.status(Response.Status.FORBIDDEN)
                        .type(MediaType.APPLICATION_JSON)
                        .encoding("utf-8")
                        .entity("A campaign exists for this player and game")
                        .build();
                  }
                }
              }
            }
          }
        }
      }

      CampaignModel campaign = createCampaign(ctx,
          campaignExtRef,
          currency,
          currency,
          itemId,
          request.getFreespinValue(),
          RawConnectorServiceImpl.Utils.toDate(request.getExpires()),
          request.getAmount(),
          setting.getCompanyId());

      if (Objects.isNull(campaign)) {
        throw new EntityNotExistException("Campaign not exist, despite created. Please check.");
      }

      try {
        CampaignAssignmentModel assignMember = new CampaignAssignmentModel();
        assignMember.setAccountExtRef(accountExtRef);
        assignMember.setCampaignId(campaign.getId());
        assignMember.setGameId(itemId);
        domainService.assignCampaignMember(
            ctx, assignMember);
      } catch (Exception ex) {
        log.info("Could not assign unknown player. Add it to campaign cache for assignment in the launcher api");
        addCachedCampaign(accountExtRef, itemId, campaign.getId());
      }

      AddFreeRoundsResponse resp = new AddFreeRoundsResponse();
      resp.setTxId(request.getTxId());
      resp.setFreespinsId(campaign.getId().toString());

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(resp).build();
    } catch (Exception e) {
      log.error("Unable to addFreespins [{}] - [{}] - [{}]", RawConfiguration.OPERATOR_CODE, partnerId, e);
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
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final GetFreeRoundsRequest request) {
    RequestContext ctx = RequestContext.instance();
    String campaignExtRef = null;
    String partnerId = null;
    String accountExtRef = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }
      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/freespins/get - [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getPlayerId());
      }
      accountExtRef = request.getPlayerId().toString();
      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      ctx = ctx.withAccessToken(
          commonService.companyAppAccessToken(
              ctx,
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential()));

      SimpleAccountModel memberAccount = null;
      try {
        memberAccount = domainService.getAccountByExtRef(ctx, accountExtRef);
      } catch (EntityNotExistException e) {
        log.debug("player with ext-ref [{}] is unknown", accountExtRef);
      }

      List<CampaignModel> campaigns = null;
      if (Objects.nonNull(memberAccount)) {
        try {
          campaigns = domainService.availableCampaigns(ctx, memberAccount.getId(), true);
        } catch (EntityNotExistException e) {
          // don't do anything.
        }
      }

      // check cache for campaigns
      String cacheKey = DigestUtils.sha256Hex(accountExtRef);
      String cachedCampaigns = cacheProvider.get(RawConfiguration.CACHE_NAME_PLAYER_CAMPAIGNS, String.class,
          cacheKey);
      if (!CommonUtils.isEmptyOrNull(cachedCampaigns)) {
        log.debug("cached campaigns found for this player [{}]", cachedCampaigns);
        for (String s : cachedCampaigns.split(",")) {

          try {
            RestResponseWrapperModel<CampaignModel> result = campaignClientService.get(
                CommonUtils.authorizationBearer(ctx.getAccessToken()),
                ctx.getTimezone(),
                ctx.getCurrency(),
                ctx.getUuid().toString(),
                ctx.getLanguage(),
                s);
            CampaignModel campaign = result.getData();
            if (Objects.isNull(campaigns)) {
              campaigns = new ArrayList<CampaignModel>();
            }
            boolean found = false;
            for (CampaignModel c : campaigns) {
              if (c.getId().equals(campaign.getId())) {
                found = true;
                break;
              }
            }
            if (!found) {
              campaigns.add(campaign);
            } else {
              log.debug("campaign was already available [{}]", s);
            }
          } catch (EntityNotExistException e) {
            // don't do anything.
          }
        }
      }

      // avoid re-fetching bet levels in case of multiple campaigns
      Map<Long, CampaignBetLevelModel> betLevelMap = Maps.newHashMap();

      GetFreeRoundsResponse resp = new GetFreeRoundsResponse();
      if (Objects.nonNull(campaigns)) {
        List<FreeRound> freeRounds = new ArrayList<FreeRound>();
        for (CampaignModel m : campaigns) {

          Integer remaining = m.getNumOfGames();
          if (m.getMetaData().containsKey("spins_count")) {
            Map<String, Object> spinsCount = (Map<String, Object>) m.getMetaData().get("spins_count");
            if (spinsCount.containsKey("remaining")) {
              remaining = (Integer) spinsCount.get("remaining");
              log.debug("meta data remaining spins {}", remaining);
            }
          }

          if (m.getStatus().equals(CampaignModel.Status.ACTIVE) && remaining > 0) {

            if (m.getName().startsWith(RawConfiguration.CAMPAIGN_PREFIX)) {

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
              r.setPromoCode(RawConnectorServiceImpl.Utils.getPromoCode(m.getExtRef()));
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
      log.error("Unable to getFreespins [{}] - [{}] - [{}]", RawConfiguration.OPERATOR_CODE, partnerId, e);
      return Response.ok()
          .type(MediaType.APPLICATION_JSON)
          .encoding("utf-8")
          .entity(e)
          .build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/relayClosePlay")
  public Response closePlay(
      @HeaderParam(RawConfiguration.AUTHORIZATION) String auth, final ClosePlayRequest request) {
    RequestContext ctx = RequestContext.instance();
    String partnerId = null;

    try {
      if (!authenticate(auth, request.getCredentials().getPartnerId())) {
        return Response.status(401).build();
      }

      if (log.isDebugEnabled()) {
        log.debug(
            "/v1/extw/exp/raw/relayClosePlay - [{}] [{}] [{}] [{}]",
            request.getCredentials().getPartnerId(),
            request.getSessionId(),
            request.getRgsPlayId(),
            request.getActionId());
      }

      partnerId = String.valueOf(request.getCredentials().getPartnerId());
      RawConfiguration.CompanySetting setting = getCompanySettings(partnerId, true);

      ctx = ctx.withAccessToken(
          commonService.companyAppAccessToken(
              ctx,
              setting.getLauncherAppClientId(),
              setting.getLauncherAppClientCredential(),
              setting.getLauncherAppApiId(),
              setting.getLauncherAppApiCredential()));

      boolean success = false;

      try {
        success = true;
      } catch (Exception e) {
        success = false;
      }

      RelayClosePlayResponse response = new RelayClosePlayResponse();

      if (request.getAsync()) {

        response.setSessionId(request.getSessionId());
        response.setMethod("relayClosePlay");
        response.setPending(true);

      } else {

        response.setActionResult(success == true ? "ok" : "error");
        response.setMethod("relayClosePlay");
        response.setSessionId(request.getSessionId());
        response.setActionId(request.getActionId());
        response.setToken(request.getToken());

        // .add("freePlaysData", freePlaysJson)
        // .add("casinoData", response.getValue("casinoData"))
        // .add("balance", response.getInt("balance"))
        // .add("freebalance", response.getInt("freebalance"))
        // .add("token", token)
      }

      return Response.ok().type(MediaType.APPLICATION_JSON).encoding("utf-8").entity(response).build();
    } catch (Exception e) {
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
   * @param currency
   * @param clientId
   * @param lobbyUrl
   * @param callerIp
   * @param rcEnable
   * @param rciFrameUrl
   * @return
   */
  private Response getLauncherInternal(
      String siteId,
      String gameId,
      String currency,
      String locale,
      String gameMode,
      String serverAddress,
      String apiAddress,
      String channel,
      String groupingSeparator,
      String decimalSeparator) {

    RawConfiguration.CompanySetting setting = getCompanySettings(siteId, false);

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
        throw new ValidationException("channel [%s] is not one of the configured channels [%s]", channel,
            setting.getChannel());
        /*
         * log.warn("channel {} is not equal to configured channel {} for partnerId {}",
         * channel, setting.getChannel(), partnerId);
         */
      }
    }

    Boolean isDemo = gameMode.equals("fun");
    String gameRef = getGameRef(gameId);
    Long itemId = Long.parseLong(gameId);

    RequestContext ctx = RequestContext.instance()
        .withLanguage(locale);
    if (isDemo) {
      if (Strings.isNullOrEmpty(currency)) {
        currency = RawConfiguration.DEFAULT_CURRENCY;
        log.info("defaulting currency to {}", currency);
      }
      log.info("launching in demo mode with currency {}", currency);
      ctx = ctx.withCurrency(currency);
    }
    ctx.getMetaData().put("clientId", clientId);
    ctx.getMetaData().put("gameRef", gameRef);
    ctx.getMetaData().put("channel", channel);
    log.debug("launcher request context: {}", ctx.getMetaData());

    ctx = ctx.withAccessToken(
        commonService.companyAppAccessToken(
            ctx,
            setting.getLauncherAppClientId(),
            setting.getLauncherAppClientCredential(),
            setting.getLauncherAppApiId(),
            setting.getLauncherAppApiCredential()));

    List<Long> campaignIds = null;
    if (!isDemo) {
      try {
        RawConnectorServiceImpl connector = (RawConnectorServiceImpl) connectorLocator
            .getConnector(RawConfiguration.OPERATOR_CODE);

        RawClientService clientService = getClientService(setting.getCompanyId());

        if (Objects.isNull(clientService)) {
          log.info("could not aquire a connector service for calling verifytoken");
        } else {
          VerifyTokenRequest operatorReq = new VerifyTokenRequest();
          operatorReq.setChannel(channel);
          operatorReq.setClientId(clientId);
          operatorReq.setToken(token);
          operatorReq.setGameRef(gameRef);
          operatorReq.setPartnerId(setting.getPartnerId());
          operatorReq.setIp(callerIp);
          String auth = setting.getOperatorCredential();
          javax.ws.rs.core.Response res = clientService.verifyToken(auth, setting.getPartnerId(), operatorReq);
          if (!RawConnectorServiceImpl.Utils.isSuccess(res.getStatus())) {
            log.error("verifytoken request error. Skip checking promotions and launch anyway.");
          } else {
            VerifyTokenResponse operatorRes = res.readEntity(VerifyTokenResponse.class);
            if (Objects.isNull(operatorRes)) {
              log.error("Could not read VerifyTokenResponse. Skip checking promotions and launch anyway.");
            } else {
              // ubo promotions test code
              /*
               * List<Promotion> promotions = new ArrayList<Promotion>();
               * Promotion p = new Promotion();
               * p.setPromotionType("freerounds");
               * // p.setPromotionId(10000L + (Math.abs(new Random(new
               * Date().getTime()).nextLong()) % 10000L));
               * p.setPromotionId(156L);
               * // p.setTxId(UUID.randomUUID().toString());
               * p.setTxId("dev-156-83348");
               * p.setPlayerId(operatorRes.getPlayerId());
               * p.setPartnerId(operatorRes.getPartnerId());
               * p.setGameRef(operatorReq.getGameRef());
               * p.setAmount(7);
               * p.setFreespinValue(100L);
               * // p.setExpires(ZonedDateTime.now().plus(1, ChronoUnit.DAYS));
               * // p.setPromoCode("ubopromo-" + UUID.randomUUID().toString());
               * p.setExpires(ZonedDateTime.parse("2022-12-26T11:20:03Z"));
               * p.setPromoCode("elysium_1test_22122022-PC");
               * promotions.add(p);
               * log.info("adding test promotion {}", p);
               * operatorRes.setPromotions(promotions);
               */
              campaignIds = ackPromotions(setting.getCompanyId(), operatorReq, operatorRes);

              // check cache for campaigns that should be assigned
              String accountExtRef = operatorRes.getPlayerId().toString();
              String cacheKey = getCampaignCacheKey(accountExtRef, itemId);
              Long cachedCampaign = cacheProvider.get(RawConfiguration.CACHE_NAME_PLAYERGAME_CAMPAIGN,
                  Long.class, cacheKey);
              if (Objects.isNull(cachedCampaign)) {

                log.debug("player [{}] with key [{}] has no cached campaign for game [{}]",
                    accountExtRef, cacheKey, itemId);
              } else {

                log.debug("player [{}] with key [{}] has cached campaign [{}] for game [{}]",
                    accountExtRef, cacheKey, cachedCampaign, itemId);

                if (Objects.isNull(campaignIds)) {
                  campaignIds = new ArrayList<Long>();
                }
                campaignIds.add(cachedCampaign);
                removeCachedCampaign(accountExtRef, itemId);
              }
            }
          }
        }
      } catch (Exception e) {
        log.error("Ignoring exception while processing promotions", e);
      }
    }

    String url;
    RestResponseWrapperModel<String> result;
    try {
      SimplifyLauncherItemModel rq = new SimplifyLauncherItemModel();
      rq.setDemo(isDemo);
      if (!isDemo)
        rq.setToken(token);
      rq.setAppId(setting.getLauncherItemApplicationId());
      rq.setItemId(itemId);
      rq.setCampaigns(campaignIds);
      rq.setExternal(Boolean.TRUE);

      Map<String, Object> confParams = Maps.newHashMap();

      if (!CommonUtils.isEmptyOrNull(lobbyUrl)) {
        confParams.put("lobby_url", lobbyUrl);
      }

      if (!CommonUtils.isEmptyOrNull(currency)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "currency", currency);
      }

      if (!CommonUtils.isEmptyOrNull(rcEnable)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "rcenable", rcEnable);
      }

      if (!CommonUtils.isEmptyOrNull(rciFrameUrl)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "rciframeurl", rciFrameUrl);
      }

      if (!CommonUtils.isEmptyOrNull(rcInterval)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "rcinterval", rcInterval);
      }

      if (!CommonUtils.isEmptyOrNull(rcElapsed)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "rcelapsed", rcElapsed);
      }

      if (!CommonUtils.isEmptyOrNull(rcHistoryUrl)) {
        confParams.put(RawConfiguration.CONF_PARAMS_PREFIX + "rcHistoryUrl", rcHistoryUrl);
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
        meta.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_GAME_ID, itemId);
        meta.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS, callerIp);
        rq.getCtx().put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_META_DATA, meta);
      }

      result = launcherClientService.launch(
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
   * get operator's company settings
   *
   * @param partnerId
   * @param validateIp
   * @return
   */
  private RawConfiguration.CompanySetting getCompanySettings(
      String partnerId, boolean validateIp) {
    String operatorIdKey = String.format("ext-%s", partnerId);
    if (!rawConfig.getOperatorIdMap().containsKey(operatorIdKey)) {
      throw new ValidationException("no configuration found for operator-id [%s]", partnerId);
    }

    Long companyId = rawConfig.getOperatorIdMap().get(operatorIdKey);

    if (!rawConfig.getCompanySettings().containsKey(companyId)) {
      throw new ValidationException("no configuration found for company-id [%s]", companyId);
    }

    if (validateIp) {
      connectorLocator
          .getConnector(RawConfiguration.OPERATOR_CODE)
          .validateIp(companyId, CommonUtils.resolveIpAddress(this.request));
    }

    return rawConfig.getCompanySettings().get(companyId);
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
        rawConfig.getPlatform(),
        rawConfig.getGamestudio(),
        gameId);
  }

  /**
   * getPrefixedRoundId
   * 
   * @param roundId
   * @return Dashur roundId
   */
  private String getPrefixedRoundId(String roundId) {
    return RawConfiguration.ROUND_PREFIX + roundId;
  }

  /**
   * getClientService
   * 
   * @param companyId
   * @return RawClientService
   */
  private RawClientService getClientService(Long companyId) {
    RawConnectorServiceImpl connector = (RawConnectorServiceImpl) connectorLocator.getConnector(
        RawConfiguration.OPERATOR_CODE);
    if (Objects.isNull(connector)) {
      return null;
    }
    return connector.clientService(companyId);
  }

  /**
   * getCampaignCacheKey
   * 
   * @param accountExtRef
   * @param itemId
   * @return cache key
   */
  private String getCampaignCacheKey(String accountExtRef, Long itemId) {
    return String.format("%s:%d", DigestUtils.sha256Hex(accountExtRef), itemId);
  }

  private void addCachedCampaign(String accountExtRef, Long itemId, Long campaignId) {
    String key = DigestUtils.sha256Hex(accountExtRef);
    String campaigns = cacheProvider.get(RawConfiguration.CACHE_NAME_PLAYER_CAMPAIGNS, String.class, key);
    if (CommonUtils.isEmptyOrNull(campaigns)) {
      cacheProvider.put(RawConfiguration.CACHE_NAME_PLAYER_CAMPAIGNS, key, campaignId.toString());
    } else {
      cacheProvider.put(RawConfiguration.CACHE_NAME_PLAYER_CAMPAIGNS, key,
          campaigns + "," + campaignId.toString());
    }
    cacheProvider.put(RawConfiguration.CACHE_NAME_PLAYERGAME_CAMPAIGN, String.format("%s:%d", key, itemId),
        campaignId);
    log.debug("player [{}] with key [{}] added campaign [{}] to [{}]", accountExtRef, key, campaignId, campaigns);
  }

  /**
   * getRgs
   * 
   * @return rgs service provider
   */
  private RgsServiceProvider getRgs() {
    return rgsService.getProvider(rawConfig.getRgsProvider());
  }

}
