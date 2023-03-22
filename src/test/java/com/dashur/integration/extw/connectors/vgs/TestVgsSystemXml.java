package com.dashur.integration.extw.connectors.vgs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.vgs.data.*;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
public class TestVgsSystemXml {
  @Test
  public void testAuthXml() {
    AuthRequest authRequest = new AuthRequest();
    authRequest.setToken(UUID.randomUUID().toString());
    authRequest.setHash(UUID.randomUUID().toString());

    AuthResponse authResponse = new AuthResponse();
    authResponse.setBalance(new BigDecimal("10.00"));
    authResponse.setCurrency("USD");
    authResponse.setEmail("user@user.com");
    authResponse.setFirstname("firstname");
    authResponse.setLastname("lastname");
    authResponse.setUserId(UUID.randomUUID().toString());
    authResponse.setUsername(UUID.randomUUID().toString());
    authResponse.setGameSessionId(UUID.randomUUID().toString());
    authResponse.setResult("OK");
    authResponse.setCode("0");

    VgsSystemAuthResponse root = new VgsSystemAuthResponse();
    root.setRequest(authRequest);
    root.setResponse(authResponse);
    root.setTime(now());

    String xml = CommonUtils.xmlToString(root);
    log.info("testAuthXml(): [{}]", xml);

    VgsSystemAuthResponse root2 = CommonUtils.xmlRead(VgsSystemAuthResponse.class, xml);
    assertThat(root2, is(IsNull.notNullValue()));
    assertThat(root2.getTime(), is(root.getTime()));
    assertThat(root2.getRequest().getToken(), is(root.getRequest().getToken()));
    assertThat(root2.getRequest().getHash(), is(root.getRequest().getHash()));
    assertThat(root2.getResponse().getCode(), is(root.getResponse().getCode()));
    assertThat(root2.getResponse().getResult(), is(root.getResponse().getResult()));
    assertThat(root2.getResponse().getCurrency(), is(root.getResponse().getCurrency()));
    assertThat(root2.getResponse().getBalance(), is(root.getResponse().getBalance()));
    assertThat(root2.getResponse().getEmail(), is(root.getResponse().getEmail()));
    assertThat(root2.getResponse().getFirstname(), is(root.getResponse().getFirstname()));
    assertThat(root2.getResponse().getLastname(), is(root.getResponse().getLastname()));
    assertThat(root2.getResponse().getUserId(), is(root.getResponse().getUserId()));
    assertThat(root2.getResponse().getUsername(), is(root.getResponse().getUsername()));
    assertThat(root2.getResponse().getGameSessionId(), is(root.getResponse().getGameSessionId()));
  }

  @Test
  public void testGetBalanceXml() {
    GetBalanceRequest rq = new GetBalanceRequest();
    rq.setUserId(UUID.randomUUID().toString());
    rq.setHash(UUID.randomUUID().toString());

    GetBalanceResponse rs = new GetBalanceResponse();
    rs.setBalance(new BigDecimal("10.00"));
    rs.setResult("OK");
    rs.setCode("0");

    VgsSystemGetBalanceResponse root = new VgsSystemGetBalanceResponse();
    root.setRequest(rq);
    root.setResponse(rs);
    root.setTime(now());

    String xml = CommonUtils.xmlToString(root);
    log.info("testGetBalanceXml(): [{}]", xml);

    VgsSystemGetBalanceResponse root2 = CommonUtils.xmlRead(VgsSystemGetBalanceResponse.class, xml);
    assertThat(root2, is(IsNull.notNullValue()));
    assertThat(root2.getTime(), is(root.getTime()));
    assertThat(root2.getRequest().getUserId(), is(root.getRequest().getUserId()));
    assertThat(root2.getRequest().getHash(), is(root.getRequest().getHash()));
    assertThat(root2.getResponse().getCode(), is(root.getResponse().getCode()));
    assertThat(root2.getResponse().getResult(), is(root.getResponse().getResult()));
    assertThat(root2.getResponse().getBalance(), is(root.getResponse().getBalance()));
  }

  @Test
  public void testChangeBalanceXml() {
    ChangeBalanceRequest rq = new ChangeBalanceRequest();
    rq.setUserId(UUID.randomUUID().toString());
    rq.setGameId(UUID.randomUUID().toString());
    rq.setRoundId(UUID.randomUUID().toString());
    rq.setAmount(new BigDecimal("10.00"));
    rq.setTransactionId(UUID.randomUUID().toString());
    rq.setTransactionType("BET");
    rq.setTransactionDescription("");
    rq.setHistory("");
    rq.setRoundFinished(Boolean.TRUE);
    rq.setHash(UUID.randomUUID().toString());

    ChangeBalanceResponse rs = new ChangeBalanceResponse();
    rs.setBalance(new BigDecimal("10.00"));
    rs.setResult("OK");
    rs.setEcSystemTransactionId(UUID.randomUUID().toString());
    rs.setCode("0");

    VgsSystemChangeBalanceResponse root = new VgsSystemChangeBalanceResponse();
    root.setRequest(rq);
    root.setResponse(rs);
    root.setTime(now());

    String xml = CommonUtils.xmlToString(root);
    log.info("testChangeBalanceXml(): [{}]", xml);

    VgsSystemChangeBalanceResponse root2 =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);
    assertThat(root2, is(IsNull.notNullValue()));
    assertThat(root2.getTime(), is(root.getTime()));
    assertThat(root2.getRequest().getHash(), is(root.getRequest().getHash()));
    assertThat(root2.getRequest().getUserId(), is(root.getRequest().getUserId()));
    assertThat(root2.getRequest().getAmount(), is(root.getRequest().getAmount()));
    assertThat(root2.getRequest().getTransactionId(), is(root.getRequest().getTransactionId()));
    assertThat(root2.getRequest().getTransactionType(), is(root.getRequest().getTransactionType()));
    assertThat(
        root2.getRequest().getTransactionDescription(),
        is(root.getRequest().getTransactionDescription()));
    assertThat(root2.getRequest().getGameId(), is(root.getRequest().getGameId()));
    assertThat(root2.getRequest().getRoundId(), is(root.getRequest().getRoundId()));
    assertThat(root2.getRequest().getHistory(), is(root.getRequest().getHistory()));
    assertThat(root2.getRequest().isRoundFinished(), is(root.getRequest().isRoundFinished()));
    assertThat(root2.getResponse().getCode(), is(root.getResponse().getCode()));
    assertThat(root2.getResponse().getResult(), is(root.getResponse().getResult()));
    assertThat(root2.getResponse().getBalance(), is(root.getResponse().getBalance()));
    assertThat(
        root2.getResponse().getEcSystemTransactionId(),
        is(root.getResponse().getEcSystemTransactionId()));
  }

  Date now() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = format.format(new Date());
    try {
      return format.parse(dateStr);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Unable to parse date");
    }
  }
}
