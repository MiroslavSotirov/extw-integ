package com.dashur.integration.commons.utils;

import static org.hamcrest.CoreMatchers.is;

import com.dashur.integration.commons.Constant;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;

public class CommonUtilsTest {
  @Test
  public void testParseJwt() {
    String token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJFdmVyeU1hdHJpeDAxX2FwaSIsImN0eCI6NjIsInBp"
            + "ZCI6MjA1MTcsImFuIjoiRXZlcnlNYXRyaXgwMSIsInRpZCI6MywiY2xpZW50X2lkIjoiRXZlcnlNYXRyaXgwMV9jbGllbnRfaWQiLCJh"
            + "cCI6IjEsMjU3NDcwOSwyNjA4MjI1IiwidWlkIjoyNjI0Nzk0LCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06"
            + "ciIsInR4OnIiLCJjb21wbGlhbmNlOnIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5z"
            + "dGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2Nh"
            + "cHA6ciIsImNhdGVnb3J5OnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiYWNjb3VudDpyIl0sImF0aSI6IjQ4YmRmMGM0LTkw"
            + "MTctNGZjOS04ZTYxLWY1ZWZmZmEzMTdiZCIsImV4dHciOmZhbHNlLCJleHAiOjE1OTA3NDg0ODYsImFpZCI6MjYwODIyNSwidXIiOjIs"
            + "Imp0aSI6IjllOTZjZmY0LWIwNzEtNDA5ZS1iMzZlLTJjNWNkNDk3MjMxZSJ9.zz50B_D_GTmfuy_wd1KQWx8wQXRsx_CW7e_Jl5j6myA";
    Map<String, Object> claims = CommonUtils.parseJwt(token);
    MatcherAssert.assertThat(claims, is(IsNull.notNullValue()));
    MatcherAssert.assertThat(claims.isEmpty(), is(Boolean.FALSE));
    MatcherAssert.assertThat(claims.get("exp"), is(1590748486));
  }

  @Test
  public void testParseJwt2() {
    String token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJFdmVyeU1hdHJpeDAxX2FwaSIsImN0eCI6NjIsInBp"
            + "ZCI6MjA1MTcsImFuIjoiRXZlcnlNYXRyaXgwMSIsInRpZCI6MywiY2xpZW50X2lkIjoiRXZlcnlNYXRyaXgwMV9jbGllbnRfaWQiLCJh"
            + "cCI6IjEsMjU3NDcwOSwyNjA4MjI1IiwidWlkIjoyNjI0Nzk0LCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06"
            + "ciIsInR4OnIiLCJjb21wbGlhbmNlOnIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5z"
            + "dGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2Nh"
            + "cHA6ciIsImNhdGVnb3J5OnIiLCJhY2dW50OnciLCJpdGVtOnICJ0eDp3IiwiYWNjb3VudDpyIl0sImF0aSI6IjQ4YmRmMGM0LTkw"
            + "MTctNGZjOS04ZTYxLWY1ZWZmZmEzMTdiZCIsImV4dHciOmZhbHNlLCJleHAiOjE1OTA3NDg0ODYsImFpZCI6MjYwODIyNSwidXIiOjIs"
            + "Imp0aSI6IjllOTZjZmY0LWIwNzEtNDA5ZS1iMzZlLTJjNWNkNDk3MjMxZSJ9.zz50B_D_GTmfuy_wd1KQWx8wQXRsx_CW7e_Jl5j6myA";
    Map<String, Object> claims = CommonUtils.parseJwt(token);
    MatcherAssert.assertThat(claims, is(IsNull.notNullValue()));
    MatcherAssert.assertThat(claims.isEmpty(), is(Boolean.TRUE));
  }

  @Test
  public void testParseJwt3() {
    String token = null;
    Map<String, Object> claims = CommonUtils.parseJwt(token);
    MatcherAssert.assertThat(claims, is(IsNull.notNullValue()));
    MatcherAssert.assertThat(claims.isEmpty(), is(Boolean.TRUE));
  }

  @Test
  public void testIsTokenExpired() {
    String token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJFdmVyeU1hdHJpeDAxX2FwaSIsImN0eCI6NjIsInBp"
            + "ZCI6MjA1MTcsImFuIjoiRXZlcnlNYXRyaXgwMSIsInRpZCI6MywiY2xpZW50X2lkIjoiRXZlcnlNYXRyaXgwMV9jbGllbnRfaWQiLCJh"
            + "cCI6IjEsMjU3NDcwOSwyNjA4MjI1IiwidWlkIjoyNjI0Nzk0LCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06"
            + "ciIsInR4OnIiLCJjb21wbGlhbmNlOnIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5z"
            + "dGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2Nh"
            + "cHA6ciIsImNhdGVnb3J5OnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiYWNjb3VudDpyIl0sImF0aSI6IjQ4YmRmMGM0LTkw"
            + "MTctNGZjOS04ZTYxLWY1ZWZmZmEzMTdiZCIsImV4dHciOmZhbHNlLCJleHAiOjE1OTA3NDg0ODYsImFpZCI6MjYwODIyNSwidXIiOjIs"
            + "Imp0aSI6IjllOTZjZmY0LWIwNzEtNDA5ZS1iMzZlLTJjNWNkNDk3MjMxZSJ9.zz50B_D_GTmfuy_wd1KQWx8wQXRsx_CW7e_Jl5j6myA";
    ZonedDateTime notExpired =
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1590748486 - 3 * 60 * 60), Constant.DEFAULT_TIMEZONE);
    ZonedDateTime expired =
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(1590748486 + 5), Constant.DEFAULT_TIMEZONE);
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, notExpired), is(Boolean.FALSE));
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, expired), is(Boolean.TRUE));
  }

  @Test
  public void testIsTokenExpired2() {
    String token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJFdmVyeU1hdHJpeDAxX2FwaSIsImN0eCI6NjIsInBp"
            + "ZCI6MjA1MTcsImFuIjoiRXZlcnlNYXRyaXgwMSIsInRpZCI6MywiY2xpZW50X2lkIjoiRXZlcnlNYXRyaXgwMV9jbGllbnRfaWQiLCJh"
            + "cCI6IjEsMjU3NDcwOSwyNjA4MjI1IiwidWlkIjoyNjI0Nzk0LCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06"
            + "ciIsInR4OnIiLCJjb21wbGlIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5z"
            + "dGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2Nh"
            + "cHA6ciIsImNhdGVnb3J5OnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLDp3IiwiYWNjb3VudDpyIl0sImF0aSI6IjQ4YmRmMGM0LTkw"
            + "MTctNGZjOS04ZTYxLWY1ZWZmZmEzMTdiZCIsImV4dHciOmZhbHNlLCJleHAiOjE1OTA3NDg0ODYsImFpZCI6MjYwODIyNSwidXIiOjIs"
            + "Imp0aSI6IjllOTZjZmY0LWIwNzEtNDA5ZS1iMzZlLTJjNWNkNDk3MjMxZSJ9.zz50B_D_GTmfuy_wd1KQWx8wQXRsx_CW7e_Jl5j6myA";
    ZonedDateTime notExpired =
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1590748486 - 3 * 60 * 60), Constant.DEFAULT_TIMEZONE);
    ZonedDateTime expired =
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(1590748486 + 5), Constant.DEFAULT_TIMEZONE);
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, notExpired), is(Boolean.TRUE));
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, expired), is(Boolean.TRUE));
  }

  @Test
  public void testIsTokenExpired3() {
    String token = null;
    ZonedDateTime notExpired =
        ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(1590748486 - 3 * 60 * 60), Constant.DEFAULT_TIMEZONE);
    ZonedDateTime expired =
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(1590748486 + 5), Constant.DEFAULT_TIMEZONE);
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, notExpired), is(Boolean.TRUE));
    MatcherAssert.assertThat(CommonUtils.isTokenExpired(token, expired), is(Boolean.TRUE));
  }
}
