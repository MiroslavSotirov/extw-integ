package com.dashur.integration.extw.connectors.relaxgaming;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.AuthException;
import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.extw.Constant;
import com.dashur.integration.extw.ExtwIntegConfiguration;
import com.dashur.integration.extw.connectors.ConnectorServiceLocator;
import com.dashur.integration.extw.data.DasAuthRequest;
import com.dashur.integration.extw.data.DasAuthResponse;
import com.dashur.integration.extw.data.DasBalanceRequest;
import com.dashur.integration.extw.data.DasBalanceResponse;
import com.dashur.integration.extw.data.DasEndRoundRequest;
import com.dashur.integration.extw.data.DasEndRoundResponse;
import com.dashur.integration.extw.data.DasTransactionCategory;
import com.dashur.integration.extw.data.DasTransactionRequest;
import com.dashur.integration.extw.data.DasTransactionResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import com.google.common.collect.Maps;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(RelaxGamingMockServer.class)
public class RelaxGamingConnectorServiceTest {
  static final String OPERATOR_CODE = Constant.OPERATOR_RELAXGAMING;
  static final String LAUNCH_TOKEN = RelaxGamingDispatcher.LAUNCH_TOKEN_2976012;
  static final AtomicLong TX_ID = new AtomicLong(100000L);

  @Inject ConnectorServiceLocator locator;

  @Inject ExtwIntegConfiguration configuration;

  public RelaxGamingMockServer relaxGamingServer;

  private Long companyId;

  private Map<String, Object> defaultCtx() {
    Map<String, Object> ctx = Maps.newHashMap();
    Map<String, Object> oprMeta = Maps.newHashMap();
    oprMeta.put("gameRef", "rlx.em.em.1");
    oprMeta.put("clientId", "mobile_app");
    oprMeta.put("channel", "web");
    oprMeta.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_IP_ADDRESS,
      "127.0.0.1");
    ctx.put(com.dashur.integration.commons.Constant.LAUNCHER_META_DATA_KEY_OPR_META,
      oprMeta);
    return ctx;
  }

  @PostConstruct
  public void initQuarkusTest() {
    companyId =
        configuration
            .configuration(OPERATOR_CODE, RelaxGamingConfiguration.class)
            .getDefaultCompanyId();
  }

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new RelaxGamingDispatcher());
    relaxGamingServer.resetQueue(dispatcher);
  }

  @Test
  public void testAuth1() {
    DasAuthRequest rq = new DasAuthRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());
    rq.setCtx(defaultCtx());

    log.error("TESTCONTEXT: {}", rq.getCtx());

    DasAuthResponse rs = locator.getConnector(OPERATOR_CODE).auth(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(LAUNCH_TOKEN));
    assertThat(rs.getAccountExtRef(), is("2976012"));
  }

  @Test
  public void testAuth2() {
    DasAuthRequest rq = new DasAuthRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN + "123");
    rq.setTimestamp(new Date());
    rq.setCtx(defaultCtx());

    assertThrows(
        AuthException.class, () -> locator.getConnector(OPERATOR_CODE).auth(companyId, rq));
  }
  
/*
  @Test
  public void testBalance1() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");

    DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(LAUNCH_TOKEN));
    assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
  }

  @Test
  public void testBalance2() {
    DasBalanceRequest rq = new DasBalanceRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN + "-abc");
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");

    assertThrows(
        AuthException.class, () -> locator.getConnector(OPERATOR_CODE).balance(companyId, rq));
  }

  @Test
  public void testWager1() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
      balance = rs.getBalance();
    }

    DasTransactionRequest rq = new DasTransactionRequest();
    rq.setReqId(UUID.randomUUID().toString());
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");
    rq.setAmount(new BigDecimal(amount));
    rq.setApplicationId(1L);
    rq.setCategory(DasTransactionCategory.WAGER);
    rq.setItemId(1L);
    rq.setRoundId(txId.toString());
    rq.setTxId(txId);

    DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
    assertThat(rs, is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(IsNull.notNullValue()));
    assertThat(rs.getToken(), is(LAUNCH_TOKEN));
    assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal(amount))));
  }

  @Test
  public void testWager2() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance1 = BigDecimal.ZERO;
    BigDecimal balance2 = BigDecimal.ZERO;

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
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
    rq.setToken(LAUNCH_TOKEN);
    rq.setTimestamp(new Date());
    rq.setAccountExtRef("2976012");
    rq.setCurrency("EUR");
    rq.setAmount(new BigDecimal(amount));
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
  public void testPayout1() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
      balance = rs.getBalance();
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal(amount))));
      balance = rs.getBalance();
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.PAYOUT);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(TX_ID.incrementAndGet());

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance.add(new BigDecimal(amount))));
    }
  }

  @Test
  public void testPayout2() {
    Long wagerTxId = TX_ID.incrementAndGet();
    Long payoutTxId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance1;
    BigDecimal balance2;

    {
      DasTransactionRequest wager = new DasTransactionRequest();
      wager.setReqId(UUID.randomUUID().toString());
      wager.setToken(LAUNCH_TOKEN);
      wager.setTimestamp(new Date());
      wager.setAccountExtRef("2976012");
      wager.setCurrency("EUR");
      wager.setAmount(new BigDecimal(amount));
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
    payout.setToken(LAUNCH_TOKEN);
    payout.setTimestamp(new Date());
    payout.setAccountExtRef("2976012");
    payout.setCurrency("EUR");
    payout.setAmount(new BigDecimal(amount));
    payout.setApplicationId(1L);
    payout.setCategory(DasTransactionCategory.PAYOUT);
    payout.setItemId(1L);
    payout.setRoundId(wagerTxId.toString());
    payout.setTxId(payoutTxId);

    balance2 =
        assertDoesNotThrow(
            () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout).getBalance());
    assertThat(balance1, is(balance2.subtract(new BigDecimal(amount))));

    balance2 =
        assertDoesNotThrow(
            () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout).getBalance());
    assertThat(balance1, is(balance2.subtract(new BigDecimal(amount))));

    payout.setTxId(TX_ID.incrementAndGet());
    payout.setRoundId(TX_ID.incrementAndGet() + "");
    assertThrows(
        ApplicationException.class,
        () -> locator.getConnector(OPERATOR_CODE).transaction(companyId, payout));
  }

  @Test
  public void testRefund1() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
      balance = rs.getBalance();
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal(amount))));
      balance = rs.getBalance();
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.REFUND);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setRefundTxId(txId);
      rq.setTxId(TX_ID.incrementAndGet());

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance.add(new BigDecimal(amount))));
    }
  }

  @Test
  public void testRefund2() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
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
  public void testEndRound1() {
    Long txId = TX_ID.incrementAndGet();
    Integer amount = 10;
    BigDecimal balance = BigDecimal.ZERO;

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
      balance = rs.getBalance();
    }

    {
      DasTransactionRequest rq = new DasTransactionRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setAmount(new BigDecimal(amount));
      rq.setApplicationId(1L);
      rq.setCategory(DasTransactionCategory.WAGER);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      DasTransactionResponse rs = locator.getConnector(OPERATOR_CODE).transaction(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance.subtract(new BigDecimal(amount))));
      balance = rs.getBalance();
    }

    {
      DasEndRoundRequest rq = new DasEndRoundRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
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
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(balance));
    }
  }

  @Test
  public void testEndRound2() {
    Long txId = TX_ID.incrementAndGet();

    {
      DasBalanceRequest rq = new DasBalanceRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");

      DasBalanceResponse rs = locator.getConnector(OPERATOR_CODE).balance(companyId, rq);
      assertThat(rs, is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(IsNull.notNullValue()));
      assertThat(rs.getToken(), is(LAUNCH_TOKEN));
      assertThat(rs.getBalance(), is(new BigDecimal("1000.00")));
    }

    {
      DasEndRoundRequest rq = new DasEndRoundRequest();
      rq.setReqId(UUID.randomUUID().toString());
      rq.setToken(LAUNCH_TOKEN);
      rq.setTimestamp(new Date());
      rq.setAccountExtRef("2976012");
      rq.setCurrency("EUR");
      rq.setApplicationId(1L);
      rq.setItemId(1L);
      rq.setRoundId(txId.toString());
      rq.setTxId(txId);

      assertThrows(
          ApplicationException.class,
          () -> locator.getConnector(OPERATOR_CODE).endRound(companyId, rq));
    }
  }
*/  
}
