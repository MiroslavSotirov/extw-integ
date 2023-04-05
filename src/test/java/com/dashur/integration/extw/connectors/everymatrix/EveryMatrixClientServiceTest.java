package com.dashur.integration.extw.connectors.everymatrix;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.connectors.everymatrix.data.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(EveryMatrixMockServer.class)
public class EveryMatrixClientServiceTest {

  @Inject @RestClient EveryMatrixClientService service;

  private final String provider = EveryMatrixDispatcher.PROVIDER;
  private final String hashPassword = EveryMatrixDispatcher.HASH_PASSWORD;

  public EveryMatrixMockServer everyMatrixServer;

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new EveryMatrixDispatcher());
    everyMatrixServer.resetQueue(dispatcher);
  }

  @Test
  public void testAuthenticate1() {
    AuthenticateRequest request = new AuthenticateRequest();
    request.setRequestScope("country, age");
    request.setLaunchToken(EveryMatrixDispatcher.LAUNCH_TOKEN_2976011);

    AuthenticateResponse response = service.authenticate(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Ok"));
    assertThat(response.getToken(), is(EveryMatrixDispatcher.TOKEN_2976011));
    assertThat(response.getCurrency(), is("EUR"));
    assertThat(response.getTotalBalance(), is(new BigDecimal("10000")));
  }

  @Test
  public void testAuthenticate2() {
    AuthenticateRequest request = new AuthenticateRequest();
    request.setRequestScope("country, age");
    request.setLaunchToken(EveryMatrixDispatcher.LAUNCH_TOKEN_2976011 + "-abc");

    AuthenticateResponse response = service.authenticate(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Failed"));
  }

  @Test
  public void testAuthenticate3() {
    AuthenticateRequest request = new AuthenticateRequest();

    AuthenticateResponse response = service.authenticate(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Failed"));
  }

  @Test
  public void testGetBalance1() {
    BalanceRequest request = new BalanceRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
    request.setCurrency("EUR");
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

    BalanceResponse response = service.balance(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Ok"));
    assertThat(response.getTotalBalance(), is(new BigDecimal("10000")));
  }

  @Test
  public void testGetBalance2() {
    BalanceRequest request = new BalanceRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011 + "-abc");
    request.setCurrency("EUR");
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

    BalanceResponse response = service.balance(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Failed"));
  }

  @Test
  public void testGetBalance3() {
    BalanceRequest request = new BalanceRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
    request.setCurrency("EUR");
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request) + "-failed");

    BalanceResponse response = service.balance(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Failed"));
  }

  @Test
  public void testBet1() {
    BigDecimal balance = BigDecimal.ZERO;
    {
      BalanceRequest request = new BalanceRequest();
      request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
      request.setCurrency("EUR");
      request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

      BalanceResponse response = service.balance(provider, request);
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatus(), is("Ok"));
      assertThat(response.getTotalBalance(), is(new BigDecimal("10000")));
      balance = response.getTotalBalance();
    }

    BetRequest request = new BetRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
    request.setCurrency("EUR");
    request.setGameId("game-id-01");
    request.setExternalId(UUID.randomUUID().toString());
    request.setRoundId("round-id-01");
    request.setAmount(new BigDecimal("10"));
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

    BetResponse response = service.bet(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Ok"));
    assertThat(response.getTotalBalance(), is(balance.subtract(request.getAmount())));
  }

  @Test
  public void testWin1() {
    BigDecimal balance = BigDecimal.ZERO;
    String wagerTxId = "";
    {
      BalanceRequest request = new BalanceRequest();
      request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
      request.setCurrency("EUR");
      request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

      BalanceResponse response = service.balance(provider, request);
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatus(), is("Ok"));
      assertThat(response.getTotalBalance(), is(new BigDecimal("10000")));
      balance = response.getTotalBalance();
    }
    {
      BetRequest request = new BetRequest();
      request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
      request.setCurrency("EUR");
      request.setGameId("game-id-01");
      request.setExternalId(UUID.randomUUID().toString());
      request.setRoundId("round-id-01");
      request.setAmount(new BigDecimal("10"));
      request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

      BetResponse response = service.bet(provider, request);
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatus(), is("Ok"));
      assertThat(response.getTotalBalance(), is(balance.subtract(request.getAmount())));

      wagerTxId = request.getExternalId();
      balance = response.getTotalBalance();
    }

    WinRequest request = new WinRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
    request.setCurrency("EUR");
    request.setGameId("game-id-01");
    request.setExternalId(UUID.randomUUID().toString());
    request.setRoundId("round-id-01");
    request.setAmount(new BigDecimal("10"));
    request.setBetExternalId(wagerTxId);
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

    WinResponse response = service.win(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Ok"));
    assertThat(response.getTotalBalance(), is(balance.add(request.getAmount())));
  }

  @Test
  public void testCancel1() {
    BigDecimal balance = BigDecimal.ZERO;
    String wagerTxId = "";
    BigDecimal amount = BigDecimal.ZERO;

    {
      BalanceRequest request = new BalanceRequest();
      request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
      request.setCurrency("EUR");
      request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

      BalanceResponse response = service.balance(provider, request);
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatus(), is("Ok"));
      assertThat(response.getTotalBalance(), is(new BigDecimal("10000")));
      balance = response.getTotalBalance();
    }
    {
      BetRequest request = new BetRequest();
      request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
      request.setCurrency("EUR");
      request.setGameId("game-id-01");
      request.setExternalId(UUID.randomUUID().toString());
      request.setRoundId("round-id-01");
      request.setAmount(new BigDecimal("10"));
      request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

      BetResponse response = service.bet(provider, request);
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getStatus(), is("Ok"));
      assertThat(response.getTotalBalance(), is(balance.subtract(request.getAmount())));

      wagerTxId = request.getExternalId();
      balance = response.getTotalBalance();
      amount = request.getAmount();
    }

    CancelRequest request = new CancelRequest();
    request.setToken(EveryMatrixDispatcher.TOKEN_2976011);
    request.setExternalId(UUID.randomUUID().toString());
    request.setCanceledExternalId(wagerTxId);
    request.setHash(EveryMatrixConnectorServiceImpl.Utils.hash(hashPassword, request));

    CancelResponse response = service.cancel(provider, request);
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getStatus(), is("Ok"));
    assertThat(response.getTotalBalance(), is(balance.add(amount)));
  }
}
