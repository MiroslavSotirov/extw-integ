package com.dashur.integration.extw.connectors.relaxgaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.connectors.relaxgaming.RelaxGamingConnectorServiceImpl.Utils;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.VerifyTokenResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.BalanceResponse;
import com.dashur.integration.extw.connectors.relaxgaming.data.WithdrawRequest;
import com.dashur.integration.extw.connectors.relaxgaming.data.TransactionResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.http.HttpStatus;

@Slf4j
@QuarkusTest
@QuarkusTestResource(RelaxGamingMockServer.class)
public class RelaxGamingClientServiceTest {

  @Inject @RestClient RelaxGamingClientService service;

  public RelaxGamingMockServer relaxGamingServer;

  private <T> T readResponse(javax.ws.rs.core.Response res, Class<T> cls) {
    try {
      T responseInstance = res.readEntity(cls);
      return responseInstance;
    } catch (Exception e) {
      return null;
    }

  }

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new RelaxGamingDispatcher());
    relaxGamingServer.resetQueue(dispatcher);
  }

  @Test
  public void testPlayerInfo1() {
    VerifyTokenRequest request = new VerifyTokenRequest();
    request.setPartnerId(RelaxGamingDispatcher.PARTNER_ID);
    request.setToken(RelaxGamingDispatcher.LAUNCH_TOKEN_2976011);

    VerifyTokenResponse response = readResponse(service.verifyToken(
      RelaxGamingDispatcher.AUTH, RelaxGamingDispatcher.PARTNER_ID, request),
      VerifyTokenResponse.class);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getPlayerId(), is(2976011L));
    assertThat(response.getCustomerId(), is("86587851"));
    assertThat(response.getCountryCode(), is("UA"));
    assertThat(response.getCurrency(), is("EUR"));
    assertThat(response.getJurisdiction(), is("IM"));
    assertThat(response.getBalance(), is(100000L));
  }

  @Test
  public void testPlayerInfo2() {
    VerifyTokenRequest request = new VerifyTokenRequest();
    request.setPartnerId(RelaxGamingDispatcher.PARTNER_ID);
    request.setToken(RelaxGamingDispatcher.LAUNCH_TOKEN_2976011 + "123");

    try {
      VerifyTokenResponse response = readResponse(service.verifyToken(
        RelaxGamingDispatcher.AUTH, RelaxGamingDispatcher.PARTNER_ID, request),
        VerifyTokenResponse.class);
      assertThat(response, is(IsNull.notNullValue())); // should not reach here
    } catch (WebApplicationException e) {
      assertThat(e, is(IsNull.notNullValue()));
      assertThat(e.getResponse(), is(IsNull.notNullValue()));
      assertThat(e.getResponse().getStatus(), not(HttpStatus.SC_OK));
      Exception ex = Utils.toException(e);
      assertThat(ex instanceof AuthException, is(Boolean.TRUE));
    }
  }

  @Test
  public void testBet() {
    BalanceRequest balanceReq = new BalanceRequest();
    balanceReq.setPlayerId(2976011L);
    balanceReq.setGameRef("rlx.em.em.1");
    balanceReq.setCurrency("EUR");
    balanceReq.setSessionId(Long.parseLong(RelaxGamingDispatcher.LAUNCH_TOKEN_2976011));

    BalanceResponse balanceRes = readResponse(service.getBalance(
      RelaxGamingDispatcher.AUTH, RelaxGamingDispatcher.PARTNER_ID, balanceReq),
      BalanceResponse.class);
    assertThat(balanceRes, is(IsNull.notNullValue()));
    assertThat(balanceRes.getBalance(), greaterThan(0L));

    WithdrawRequest request = new WithdrawRequest();
    request.setChannel(RelaxGamingDispatcher.CHANNEL);
    request.setSessionId(Long.parseLong(RelaxGamingDispatcher.LAUNCH_TOKEN_2976011));
    request.setPlayerId(balanceReq.getPlayerId());
    request.setGameRef(balanceReq.getGameRef());
    request.setTxId(UUID.randomUUID().toString());
    request.setRoundId("round-id-01");
    request.setEnded(Boolean.FALSE);
    request.setAmount(1000L);
    request.setCurrency(balanceReq.getCurrency());

    TransactionResponse response = readResponse(service.withdraw(
      RelaxGamingDispatcher.AUTH, RelaxGamingDispatcher.PARTNER_ID, request),
      TransactionResponse.class);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getBalance(), is(balanceRes.getBalance() - request.getAmount()));
  }
}
