package com.dashur.integration.extw.connectors.parimatch;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.connectors.parimatch.PariMatchConnectorServiceImpl.Utils;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceRequest;
import com.dashur.integration.extw.connectors.parimatch.data.BalanceResponse;
import com.dashur.integration.extw.connectors.parimatch.data.BetRequest;
import com.dashur.integration.extw.connectors.parimatch.data.TransactionResponse;
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

@Slf4j
@QuarkusTest
@QuarkusTestResource(PariMatchMockServer.class)
public class PariMatchClientServiceTest {

  @Inject @RestClient PariMatchClientService service;

  public PariMatchMockServer pariMatchServer;

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new PariMatchDispatcher());
    pariMatchServer.resetQueue(dispatcher);
  }

  @Test
  public void testPlayerInfo1() {
    BalanceRequest request = new BalanceRequest();
    request.setCasinoId(PariMatchDispatcher.CASINO_ID);
    request.setToken(PariMatchDispatcher.LAUNCH_TOKEN_2976011);

    BalanceResponse response = service.balance(PariMatchDispatcher.CONSUMER_ID, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getPlayerId(), is("2976011"));
    assertThat(response.getPlayerName(), is("Andrew"));
    assertThat(response.getCountry(), is("Ukraine"));
    assertThat(response.getCurrency(), is("EUR"));
    assertThat(response.getBalance(), is(100000));
  }

  @Test
  public void testPlayerInfo2() {
    BalanceRequest request = new BalanceRequest();
    request.setCasinoId(PariMatchDispatcher.CASINO_ID);
    request.setToken(PariMatchDispatcher.LAUNCH_TOKEN_2976011 + "-abc");

    try {
      BalanceResponse response = service.balance(PariMatchDispatcher.CONSUMER_ID, request);
      assertThat(response, is(IsNull.notNullValue())); // should not reach here
    } catch (WebApplicationException e) {
      assertThat(e, is(IsNull.notNullValue()));
      assertThat(e.getResponse(), is(IsNull.notNullValue()));
      assertThat(e.getResponse().getStatus(), is(422));
      Exception ex = Utils.toException(e);
      assertThat(ex instanceof AuthException, is(Boolean.TRUE));
    }
  }

  @Test
  public void testBet() {
    BalanceRequest balanceReq = new BalanceRequest();
    balanceReq.setCasinoId(PariMatchDispatcher.CASINO_ID);
    balanceReq.setToken(PariMatchDispatcher.LAUNCH_TOKEN_2976011);

    BalanceResponse balanceRes = service.balance(PariMatchDispatcher.CONSUMER_ID, balanceReq);
    assertThat(balanceRes, is(IsNull.notNullValue()));
    assertThat(balanceRes.getBalance(), greaterThan(0));

    BetRequest request = new BetRequest();
    request.setCasinoId(PariMatchDispatcher.CASINO_ID);
    request.setToken(PariMatchDispatcher.LAUNCH_TOKEN_2976011);
    request.setPlayerId("2976011");
    request.setGameId("game-id-01");
    request.setTxId(UUID.randomUUID().toString());
    request.setRoundId("round-id-01");
    request.setRoundClosed(Boolean.FALSE);
    request.setAmount(1000);
    request.setCurrency("EUR");

    TransactionResponse response = service.bet(PariMatchDispatcher.CONSUMER_ID, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getBalance(), is(balanceRes.getBalance() - request.getAmount()));
  }
}
