package com.dashur.integration.extw.connectors.vgs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.dashur.integration.commons.testhelpers.ServerDispatcher;
import com.dashur.integration.commons.utils.CommonUtils;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemAuthResponse;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemChangeBalanceResponse;
import com.dashur.integration.extw.connectors.vgs.data.VgsSystemGetBalanceResponse;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@QuarkusTestResource(VgsMockServer.class)
public class VgsClientServiceTest {
  @Inject @RestClient VgsClientService service;

  private String passKey = VgsDispatcher.PASS_KEY;

  public VgsMockServer vgsServer;

  @BeforeEach
  public void setup() {
    ServerDispatcher dispatcher = new ServerDispatcher();
    dispatcher.register(new VgsDispatcher());
    vgsServer.resetQueue(dispatcher);
  }

  @Test
  public void auth1() {
    String token = VgsDispatcher.TOKEN_2976011;
    String hash = DigestUtils.md5Hex(String.format("%s%s", token, passKey));

    VgsSystemAuthResponse response =
        CommonUtils.xmlRead(VgsSystemAuthResponse.class, service.authenticate(token, hash));
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getToken(), is(token));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("OK"));
    assertThat(response.getResponse().getUserId(), is("2976011"));
  }

  @Test
  public void auth2() {
    String token = VgsDispatcher.TOKEN_2976011 + "abc-";
    String hash = DigestUtils.md5Hex(String.format("%s%s", token, passKey));

    VgsSystemAuthResponse response =
        CommonUtils.xmlRead(VgsSystemAuthResponse.class, service.authenticate(token, hash));
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getToken(), is(token));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("FAILED"));
  }

  @Test
  public void balance() {
    String token = VgsDispatcher.TOKEN_2976011;
    String userId = "";
    String hash = DigestUtils.md5Hex(String.format("%s%s", token, passKey));
    BigDecimal balance = BigDecimal.ZERO;

    {
      VgsSystemAuthResponse response =
          CommonUtils.xmlRead(VgsSystemAuthResponse.class, service.authenticate(token, hash));
      assertThat(response, is(IsNull.notNullValue()));
      assertThat(response.getRequest().getToken(), is(token));
      assertThat(response.getRequest().getHash(), is(hash));
      assertThat(response.getResponse().getResult(), is("OK"));
      assertThat(response.getResponse().getUserId(), is("2976011"));
      userId = response.getResponse().getUserId();
      balance = response.getResponse().getBalance();
      hash = DigestUtils.md5Hex(String.format("%s%s", userId, passKey));
    }

    VgsSystemGetBalanceResponse response =
        CommonUtils.xmlRead(VgsSystemGetBalanceResponse.class, service.balance(userId, hash));
    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getUserId(), is(userId));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("OK"));
    assertThat(response.getResponse().getBalance(), is(balance));
  }

  @Test
  public void wager() {
    String userId = "2976011";
    BigDecimal amount = new BigDecimal("10.00");
    String wagerTxId = UUID.randomUUID().toString();
    String txType = "BET";
    String txDesc = "";
    String roundId = wagerTxId;
    String gameId = "game-12345";
    String history = "";
    Boolean isRoundFinished = Boolean.FALSE;
    String hash =
        DigestUtils.md5Hex(
            String.format(
                "%s%s%s%s%s%s%s%s",
                userId, amount, txType, txDesc, roundId, gameId, history, passKey));

    String xml =
        service.transaction(
            userId,
            amount,
            gameId,
            txType,
            txDesc,
            roundId,
            gameId,
            history,
            isRoundFinished,
            hash);
    log.info("res: [{}]", xml);
    VgsSystemChangeBalanceResponse response =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getUserId(), is(userId));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("OK"));
    assertThat(response.getResponse().getBalance(), is(IsNull.notNullValue()));
  }

  @Test
  public void payout() {
    String userId = "2976011";
    BigDecimal amount = new BigDecimal("10.00");
    String wagerTxId = UUID.randomUUID().toString();
    String txType = "WIN";
    String txDesc = "";
    String roundId = wagerTxId;
    String gameId = "game-12345";
    String history = "";
    Boolean isRoundFinished = Boolean.FALSE;
    String hash =
        DigestUtils.md5Hex(
            String.format(
                "%s%s%s%s%s%s%s%s",
                userId, amount, txType, txDesc, roundId, gameId, history, passKey));

    String xml =
        service.transaction(
            userId,
            amount,
            gameId,
            txType,
            txDesc,
            roundId,
            gameId,
            history,
            isRoundFinished,
            hash);
    log.info("res: [{}]", xml);
    VgsSystemChangeBalanceResponse response =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getUserId(), is(userId));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("OK"));
    assertThat(response.getResponse().getBalance(), is(IsNull.notNullValue()));
  }

  @Test
  public void refund() {
    String userId = "2976011";
    BigDecimal amount = new BigDecimal("10.00");
    String wagerTxId = UUID.randomUUID().toString();
    String txType = "CANCELED_BET";
    String txDesc = "";
    String roundId = wagerTxId;
    String gameId = "game-12345";
    String history = "";
    Boolean isRoundFinished = Boolean.FALSE;
    String hash =
        DigestUtils.md5Hex(
            String.format(
                "%s%s%s%s%s%s%s%s",
                userId, amount, txType, txDesc, roundId, gameId, history, passKey));

    String xml =
        service.transaction(
            userId,
            amount,
            gameId,
            txType,
            txDesc,
            roundId,
            gameId,
            history,
            isRoundFinished,
            hash);
    log.info("res: [{}]", xml);
    VgsSystemChangeBalanceResponse response =
        CommonUtils.xmlRead(VgsSystemChangeBalanceResponse.class, xml);

    assertThat(response, is(IsNull.notNullValue()));
    assertThat(response.getRequest().getUserId(), is(userId));
    assertThat(response.getRequest().getHash(), is(hash));
    assertThat(response.getResponse().getResult(), is("OK"));
    assertThat(response.getResponse().getBalance(), is(IsNull.notNullValue()));
  }
}
