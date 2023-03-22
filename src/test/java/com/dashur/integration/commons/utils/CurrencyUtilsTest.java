package com.dashur.integration.commons.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class CurrencyUtilsTest {
  @Test
  public void testEnsureDigit1() {
    BigDecimal amount = new BigDecimal("100.00");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("USD", amount);
    assertThat(
        NumberFormat.getInstance(Locale.US).format(result),
        is(NumberFormat.getInstance(Locale.US).format(amount)));
  }

  @Test
  public void testEnsureDigit2() {
    BigDecimal amount = new BigDecimal("100.51");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("USD", amount);
    assertThat(
        NumberFormat.getInstance(Locale.US).format(result),
        is(NumberFormat.getInstance(Locale.US).format(amount)));
  }

  @Test
  public void testEnsureDigit3() {
    BigDecimal amount = new BigDecimal("100.515");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("USD", amount);
    assertThat(
        NumberFormat.getInstance(Locale.US).format(result),
        is(NumberFormat.getInstance(Locale.US).format(amount.setScale(2, RoundingMode.HALF_EVEN))));
  }

  @Test
  public void testEnsureDigit4() {
    BigDecimal amount = new BigDecimal("100.515");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("KRW", amount);
    assertThat(
        NumberFormat.getInstance(Locale.US).format(result),
        is(NumberFormat.getInstance(Locale.US).format(amount.setScale(0, RoundingMode.HALF_EVEN))));
  }

  @Test
  public void testEnsureDigit5() {
    BigDecimal amount = new BigDecimal("100.515");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("IDR", amount);
    assertThat(
        NumberFormat.getInstance(Locale.US).format(result),
        is(NumberFormat.getInstance(Locale.US).format(amount.setScale(2, RoundingMode.HALF_EVEN))));
  }

  @Test
  public void testEnsureDigit6() {
    String[] currencies =
        new String[] {
          "BIF", "CLP", "DJF", "GNF", "ISK", "JPY", "KMF", "KRW", "PYG", "RWF", "UGX", "VND", "VUV",
          "XAF", "XOF", "XPF"
        };
    BigDecimal amount = new BigDecimal("100.515");
    for (String currency : currencies) {
      BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency(currency, amount);
      assertThat(
          String.format("Failed for currencies: %s", currency),
          NumberFormat.getInstance(Locale.US).format(result),
          is(
              NumberFormat.getInstance(Locale.US)
                  .format(amount.setScale(0, RoundingMode.HALF_EVEN))));
    }
  }

  @Test
  public void testEnsureDigit7() {
    BigDecimal amount = new BigDecimal("100.51515");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("BTC", amount);
    assertThat(result.toString(), is(amount.toString()));
  }

  @Test
  public void testEnsureDigit8() {
    BigDecimal amount = new BigDecimal("100.51515");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("MBC", amount);
    assertThat(result.toString(), is(amount.toString()));
  }

  @Test
  public void testEnsureDigit9() {
    BigDecimal amount = new BigDecimal("825.70");
    BigDecimal result = CurrencyUtils.ensureAmountScaleByCurrency("JPY", amount);
    assertThat(result.toString(), is("826"));
  }
}
