package com.dashur.integration.extw.connectors.qt;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.connectors.qt.data.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.Date;
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
@QuarkusTestResource(QtMockServer.class)
public class QtClientServiceTest {
  @Inject @RestClient QtClientService service;

  private String passKey = QtDispatcher.PASS_KEY;

  private String providerId = QtDispatcher.PROVIDER_ID;

  public QtMockServer qtServer;

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new QtDispatcher());
    qtServer.resetQueue(dispatcher);
  }

  @Test
  public void auth1() {
    AuthRequest rq = new AuthRequest();
    rq.setIpAddress("127.0.0.1");
    rq.setGameId("game-1");

    AuthResponse rs = service.authenticate(passKey, QtDispatcher.TOKEN_2976011, providerId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getPlayerId(), is("2976011"));
  }

  @Test
  public void auth2() {
    AuthRequest rq = new AuthRequest();
    rq.setIpAddress("127.0.0.1");
    rq.setGameId("game-1");

    try {
      AuthResponse rs =
          service.authenticate(passKey, QtDispatcher.TOKEN_2976011 + "-abc", providerId, rq);
      assertThat(rs, is(IsNull.nullValue())); // should not reach here
    } catch (WebApplicationException e) {
      assertThat(e, is(IsNull.notNullValue()));
      assertThat(e.getResponse(), is(IsNull.notNullValue()));
      assertThat(e.getResponse().getStatus(), is(400));
      Exception ex = QtConnectorServiceImpl.Utils.toException(e);
      assertThat(ex instanceof AuthException, is(Boolean.TRUE));
    }
  }

  @Test
  public void balance() {
    BalanceResponse rs =
        service.balance(passKey, QtDispatcher.TOKEN_2976011, providerId, "2976011");
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getBalance(), is(IsNull.notNullValue()));
    assertThat(rs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
  }

  @Test
  public void withdrawal() {
    BalanceResponse balanceRs =
        service.balance(passKey, QtDispatcher.TOKEN_2976011, providerId, "2976011");
    assertThat(balanceRs, is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));

    WithdrawalRequest withdrawalRq = new WithdrawalRequest();
    withdrawalRq.setPlayerId("2976011");
    withdrawalRq.setTxnId(UUID.randomUUID().toString());
    withdrawalRq.setCreated(new Date());
    withdrawalRq.setCompleted(Boolean.FALSE);
    withdrawalRq.setAmount(new BigDecimal("10.00"));
    withdrawalRq.setGameId("game-1");
    withdrawalRq.setRoundId(UUID.randomUUID().toString());

    WithdrawalResponse withdrawalRs =
        service.withdrawal(passKey, QtDispatcher.TOKEN_2976011, providerId, withdrawalRq);
    assertThat(withdrawalRs, is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
    assertThat(
        balanceRs
                .getBalance()
                .subtract(new BigDecimal("10.00"))
                .compareTo(withdrawalRs.getBalance())
            == 0,
        is(Boolean.TRUE));
  }

  @Test
  public void deposit() {
    BalanceResponse balanceRs =
        service.balance(passKey, QtDispatcher.TOKEN_2976011, providerId, "2976011");
    assertThat(balanceRs, is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));

    WithdrawalRequest withdrawalRq = new WithdrawalRequest();
    withdrawalRq.setPlayerId("2976011");
    withdrawalRq.setTxnId(UUID.randomUUID().toString());
    withdrawalRq.setCreated(new Date());
    withdrawalRq.setCompleted(Boolean.FALSE);
    withdrawalRq.setAmount(new BigDecimal("10.00"));
    withdrawalRq.setGameId("game-1");
    withdrawalRq.setRoundId(UUID.randomUUID().toString());

    WithdrawalResponse withdrawalRs =
        service.withdrawal(passKey, QtDispatcher.TOKEN_2976011, providerId, withdrawalRq);
    assertThat(withdrawalRs, is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
    assertThat(
        balanceRs
                .getBalance()
                .subtract(new BigDecimal("10.00"))
                .compareTo(withdrawalRs.getBalance())
            == 0,
        is(Boolean.TRUE));

    DepositRequest depositRq = new DepositRequest();
    depositRq.setPlayerId("2976011");
    depositRq.setTxnId(UUID.randomUUID().toString());
    depositRq.setCreated(new Date());
    depositRq.setCompleted(Boolean.FALSE);
    depositRq.setAmount(new BigDecimal("10.00"));
    depositRq.setGameId("game-1");
    depositRq.setRoundId(UUID.randomUUID().toString());
    depositRq.setBetId(withdrawalRq.getTxnId());

    DepositResponse depositRs =
        service.deposit(passKey, QtDispatcher.TOKEN_2976011, providerId, depositRq);
    assertThat(depositRs, is(IsNull.notNullValue()));
    assertThat(depositRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(depositRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
    assertThat(balanceRs.getBalance().compareTo(depositRs.getBalance()) == 0, is(Boolean.TRUE));
  }

  @Test
  public void rollback() {
    BalanceResponse balanceRs =
        service.balance(passKey, QtDispatcher.TOKEN_2976011, providerId, "2976011");
    assertThat(balanceRs, is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(balanceRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));

    WithdrawalRequest withdrawalRq = new WithdrawalRequest();
    withdrawalRq.setPlayerId("2976011");
    withdrawalRq.setTxnId(UUID.randomUUID().toString());
    withdrawalRq.setCreated(new Date());
    withdrawalRq.setCompleted(Boolean.FALSE);
    withdrawalRq.setAmount(new BigDecimal("10.00"));
    withdrawalRq.setGameId("game-1");
    withdrawalRq.setRoundId(UUID.randomUUID().toString());

    WithdrawalResponse withdrawalRs =
        service.withdrawal(passKey, QtDispatcher.TOKEN_2976011, providerId, withdrawalRq);
    assertThat(withdrawalRs, is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(withdrawalRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
    assertThat(
        balanceRs
                .getBalance()
                .subtract(new BigDecimal("10.00"))
                .compareTo(withdrawalRs.getBalance())
            == 0,
        is(Boolean.TRUE));

    RollbackRequest rollbackRq = new RollbackRequest();
    rollbackRq.setPlayerId("2976011");
    rollbackRq.setTxnId(UUID.randomUUID().toString());
    rollbackRq.setCreated(new Date());
    rollbackRq.setCompleted(Boolean.FALSE);
    rollbackRq.setAmount(new BigDecimal("10.00"));
    rollbackRq.setGameId("game-1");
    rollbackRq.setRoundId(UUID.randomUUID().toString());
    rollbackRq.setBetId(withdrawalRq.getTxnId());

    RollbackResponse rollbackRs =
        service.rollback(passKey, QtDispatcher.TOKEN_2976011, providerId, rollbackRq);
    assertThat(rollbackRs, is(IsNull.notNullValue()));
    assertThat(rollbackRs.getBalance(), is(IsNull.notNullValue()));
    assertThat(rollbackRs.getBalance().compareTo(BigDecimal.ZERO) > 0, is(Boolean.TRUE));
    assertThat(balanceRs.getBalance().compareTo(rollbackRs.getBalance()) == 0, is(Boolean.TRUE));
  }
}
