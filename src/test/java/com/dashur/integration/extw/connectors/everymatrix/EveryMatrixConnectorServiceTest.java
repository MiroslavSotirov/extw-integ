package com.dashur.integration.extw.connectors.everymatrix;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.data.*;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(EveryMatrixMockServer.class)
public class EveryMatrixConnectorServiceTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_EVERYMATRIX;
  static final String LAUNCH_TOKEN = EveryMatrixDispatcher.LAUNCH_TOKEN_2976012;
  static final String TOKEN = EveryMatrixDispatcher.TOKEN_2976012;
  static final AtomicLong TX_ID = new AtomicLong(100000L);

  @Inject ConnectorServiceLocator locator;
  @Inject ExtwIntegConfiguration configuration;
  private Long companyId;

  @PostConstruct
  public void initQuarkusTest() {
    companyId =
        configuration
            .configuration(OPERATOR_CODE, EveryMatrixConfiguration.class)
            .getDefaultCompanyId();
  }

  public EveryMatrixMockServer everyMatrixServer;

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new EveryMatrixDispatcher());
    everyMatrixServer.resetQueue(dispatcher);
  }

  @Test
  public void testValidate() {
    String hmacHash = "B40WhjGpDR6tTXH00DMN54Nznkl19feswzF7NvtVg3g=";
    String raw =
        "{\"req_id\":\"a9475eee-c7ee-43f7-b980-f1c5d5048a4a\",\"timestamp\":\"2020-04-07 06:20:51.827\",\"token\":\"d3678f4a23ab6848f57dea9aa7545f56\",\"account_ext_ref\":\"Jordan01\"}";
    assertDoesNotThrow(
        () -> locator.getConnector(OPERATOR_CODE).validate(companyId, hmacHash, raw));
  }

  @Test
  public void testAuth() {
    DasAuthRequest rq = new DasAuthRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());

    DasAuthResponse rs = locator.getConnector(OPERATOR_CODE).auth(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getAccountExtRef(), is("2976012"));
  }

  @Test
  public void testAuth2() {
    DasAuthRequest rq = new DasAuthRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN + "-abc");
    rq.setTimestamp(new Date());

    assertThrows(
        AuthException.class, () -> locator.getConnector(OPERATOR_CODE).auth(companyId, rq));
  }

  @Test
  public void testBalance() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");

    DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getBalance(), is(new BigDecimal("10000")));
  }

  @Test
  public void testBalance2() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN + "-abc");
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");

    assertThrows(
        AuthException.class, () -> locator.getConnector(OPERATOR_CODE).balance(companyId, rq));
  }

  @Test
  public void testTransactionWager() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("10000")));
      balance = rs.getBalance();
    }

    DasTransactionRequest rq = new DasTransactionRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");
    rq.setAmount(new BigDecimal("10"));
    rq.setApplicationId(1L);
    rq.setCategory(DasTransactionCategory.WAGER);
    rq.setItemId(1L);
    rq.setRoundId(txId.toString());
    rq.setTxId(txId);

    DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(TOKEN));
    assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));
  }

  @Test
  public void testTransactionWager2() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance1 = BigDecimal.ZERO;
    BigDecimal balance2 = BigDecimal.ZERO;

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      balance1 =
          assertDoesNotThrow(
              () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, rq).getBalance());
    }

    DasTransactionRequest rq = new DasTransactionRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");
    rq.setAmount(new BigDecimal("10"));
    rq.setApplicationId(1L);
    rq.setCategory(DasTransactionCategory.WAGER);
    rq.setItemId(1L);
    rq.setRoundId(txId.toString());
    rq.setTxId(txId);

    // ensure that there isn't error thrown. by right error code should be thrown, but it was not.
    balance2 =
        assertDoesNotThrow(
            () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, rq).getBalance());
    assertThat(balance1, is(balance2));
  }

  @Test
  public void testTransactionPayout() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("10000")));
      balance = rs.getBalance();
    }
    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));

      balance = rs.getBalance();
    }
    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.PAYOUT);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(TX_ID.incrementAndGet());

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.add(new BigDecimal("10"))));
    }
  }

  @Test
  public void testTransactionPayout2() {
    Long wagerTxId = TX_ID.incrementAndGet();
    Long payoutTxId = TX_ID.incrementAndGet();
    BigDecimal balance1;
    BigDecimal balance2;

    {
      DasTransactionRequest wager = new DasTransactionRequest();
      wager.setReqId(UUID.randomUUID().toString());
      wager.setToken(TOKEN);
      wager.setTimestamp(new Date());
      wager.setAccountExtRef("2976012");
      wager.setCurrency("EUR");
      wager.setAmount(new BigDecimal("10"));
      wager.setApplicationId(1L);
      wager.setCategory(DasTransactionCategory.WAGER);
      wager.setItemId(1L);
      wager.setRoundId(wagerTxId.toString());
      wager.setTxId(wagerTxId);

      balance1 =
          assertDoesNotThrow(
              () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, wager).getBalance());
    }

    DasTransactionRequest payout = new DasTransactionRequest();
    payout.setReqId(UUID.randomUUID().toString());
    payout.setToken(TOKEN);
    payout.setTimestamp(new Date());
    payout.setAccountExtRef("2976012");
    payout.setCurrency("EUR");
    payout.setAmount(new BigDecimal("10"));
    payout.setApplicationId(1L);
    payout.setCategory(DasTransactionCategory.PAYOUT);
    payout.setItemId(1L);
    payout.setRoundId(wagerTxId.toString());
    payout.setTxId(payoutTxId);

    balance2 =
        assertDoesNotThrow(
            () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout).getBalance());
    assertThat(balance1, is(balance2.subtract(new BigDecimal("10"))));
    balance2 =
        assertDoesNotThrow(
            () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout).getBalance());
    assertThat(balance1, is(balance2.subtract(new BigDecimal("10"))));
    payout.setTxId(TX_ID.incrementAndGet());
    payout.setRoundId(TX_ID.incrementAndGet() + "");
    assertThrows(
        EntityNotExistException.class,
        () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout));
  }

  @Test
  public void testTransactionRefund() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("10000")));
      balance = rs.getBalance();
    }
    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));

      balance = rs.getBalance();
    }
    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.REFUND);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setRefundTxId(txId);
      rq.setTxId(TX_ID.incrementAndGet());

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.add(new BigDecimal("10"))));
    }
  }

  @Test
  public void testTransactionRefund2() {
    Long txId = TX_ID.incrementAndGet();

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.REFUND);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setRefundTxId(txId);
      rq.setTxId(TX_ID.incrementAndGet());

      assertDoesNotThrow(() -> locator.getConnector(OPERATOR_CODE).transaction(companyId, rq));
    }
  }

  @Test
  public void testEndRound() {
    Long txId = TX_ID.incrementAndGet();
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("10000")));
      balance = rs.getBalance();
    }
    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal("10"));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal("10"))));

      balance = rs.getBalance();
    }
    {
      DasEndRoundRequest rq = new DasEndRoundRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setApplicationId(1L);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasEndRoundResponse rs = locator.getConnector(OPERATOR_CODE).endRound(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(TOKEN));
      assertThat(rs.getBalance(), is(balance));
    }
  }
}
