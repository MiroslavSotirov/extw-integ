package com.dashur.integration.commons.testhelpers;

import com.dashur.integration.commons.rest.model.CampaignModel;
import com.dashur.integration.commons.utils.CommonUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import org.apache.http.HttpStatus;

@Slf4j
public class BackendServer extends MockServer {

  static Map<String, String> services = new HashMap<>();

  static {
    services.putAll(
        Map.of(
            "AuthClientService", "",
            "AccountClientService", "",
            "UserClientService", "",
            "TransactionClientService", "",
            "CampaignClientService", "",
            "ApplicationClientService", "",
            "ItemClientService", "",
            "WalletClientService", "",
            "LauncherClientService", "",
            "FeedTransactionClientService", ""));
    services.putAll(
        Map.of(
            "com.dashur.integration.commons.testhelpers.LauncherTestService", "",
            "com.dashur.integration.commons.testhelpers.AuthClientTestService", ""));
  }

  public BackendServer() {
    super("backendServer", services);
  }

  /** auth app id */
  public void authAppId() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"trustedFacets\":[{\"version\":{\"major\":1,\"minor\":0},\"ids\":[\"https://das-api.stargazer.com.sg\",\"https://u.stargazer.com.sg\",\"https://das-ui.stargazer.com.sg\",\"https://ap.stargazer.com.sg\",\"https://localhost:4433\"]}]}"));
  }

  /** queue app client login rs */
  public void authAppClientLoginRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdCI6MSwic2NvcGUiOlsicHVzaGZlZWQ6ciIsInRva2VuOnciLCJ1c2VyOnIiLCJncm91cDpyIiwiYWNjb3VudDpyIiwiYWNjYXBwOnIiLCJhcHBsaWNhdGlvbjpyIl0sInBpZCI6ODA1MywiZXhwIjoxNTgyODAxNzcxLCJhaWQiOjEsImFuIjoiU1lTVEVNIiwianRpIjoiN2RmNTQ0NGItY2I1Zi00Y2RlLTliNjUtMzQ5OTU0YTgwM2U1IiwiY2xpZW50X2lkIjoiaW50ZWdfbmVfdDIiLCJhcCI6IjEifQ.rWSKL9aQX8ezLXiKnB-kOe3xLU-on31aN_BPHqidREQ\",\"token_type\":\"bearer\",\"scope\":\"pushfeed:r token:w user:r group:r account:r accapp:r application:r\"}"));
  }

  /** queue company login rs */
  public void authCompanyLoginRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJUZXN0Q29tcGFueUhEX2FwaSIsImN0eCI6MjM2LCJwaWQiOjkwNzYsImFuIjoiVGVzdENvbXBhbnlIRCIsInRpZCI6MSwiY2xpZW50X2lkIjoiVGVzdENvbXBhbnlIRF9jbGllbnRfaWQiLCJhcCI6IjEsMTgxNjg0NiwyNTI4MjUyIiwidWlkIjoyNTQxODkzLCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInR4OnIiLCJjb21wbGlhbmNlOnIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5zdGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiYWNjb3VudDpyIl0sImV4cCI6MTU3MzQ0ODIzMCwiYWlkIjoyNTI4MjUyLCJ1ciI6MywianRpIjoiNjFhYzA1MWYtYWYzOC00ZmNiLWI5ZTYtZTg3Y2U4Y2ZmYWJlIn0.JM9Ovtee-0vnc584xuIQhqYrhLsfu4FDxLoN8-QxfPg\",\"token_type\":\"bearer\",\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJUZXN0Q29tcGFueUhEX2FwaSIsImN0eCI6MjM2LCJwaWQiOjkwNzYsImFuIjoiVGVzdENvbXBhbnlIRCIsInRpZCI6MSwiY2xpZW50X2lkIjoiVGVzdENvbXBhbnlIRF9jbGllbnRfaWQiLCJhcCI6IjEsMTgxNjg0NiwyNTI4MjUyIiwidWlkIjoyNTQxODkzLCJhdCI6Mywic2NvcGUiOlsiYXVkaXQ6ciIsImxhdW5jaGVyX2l0ZW06ciIsInR4OnIiLCJjb21wbGlhbmNlOnIiLCJhcHBfbmFtZTpyIiwiZXhjaGFuZ2VfcmF0ZXM6ciIsImNhbXBhaWduOnciLCJhcHBfaW5zdGFsbGVkOnIiLCJ1c2VyOnciLCJ3YWxsZXQ6ciIsImNhbXBhaWduOnIiLCJ0b2tlbjp3IiwicmVwb3J0OnIiLCJ1c2VyOnIiLCJhY2NhcHA6ciIsImNhdGVnb3J5OnIiLCJhY2NvdW50OnciLCJpdGVtOnIiLCJ0eDp3IiwiYWNjb3VudDpyIl0sImF0aSI6IjYxYWMwNTFmLWFmMzgtNGZjYi1iOWU2LWU4N2NlOGNmZmFiZSIsImV4cCI6MTU3MzQ1NTQzMCwiYWlkIjoyNTI4MjUyLCJ1ciI6MywianRpIjoiNmRiMjE1MDUtZDQ1MC00MmI4LWI2OTItMzZhZDVmODc0NDUyIn0.Ps71kNH1aOhOU1gks7daRollaeAIxgVrHzPU--oqqiQ\",\"expires_in\":3599,\"scope\":\"audit:r launcher_item:r tx:r compliance:r app_name:r exchange_rates:r campaign:w app_installed:r user:w wallet:r campaign:r token:w report:r user:r accapp:r category:r account:w item:r tx:w account:r\",\"jti\":\"61ac051f-af38-4fcb-b9e6-e87ce8cffabe\"}"));
  }

  /** auth member refresh token */
  public void authMemberRefreshTokenRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"access_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJUZXN0Q29tcGFueUhEX00wMTo3ODg4IiwiY3R4Ijo3NTEsInBpZCI6OTA3NiwiYW4iOiJUZXN0Q29tcGFueUhEX00wMSIsInRpZCI6MSwiY2xpZW50X2lkIjoiVGVzdENvbXBhbnlIRF9jbGllbnRfaWQiLCJhcCI6IjEsMTgxNjg0NiwyNTI4MjUyLDI1MjgyNjEiLCJ1aWQiOjI1NDE5MDIsImF0Ijo1LCJzY29wZSI6WyJ3YWxsZXQ6ciIsInR4OnciLCJ0eDpyIl0sImV4dHciOmZhbHNlLCJleHAiOjE1NzM0NDg4NzcsImFpZCI6MjUyODI2MSwidXIiOjMsImp0aSI6IjEwNGRhZGI3LWI0ZjMtNGVlNy04NTJjLTdkYmRlZWZiNDRjOCJ9.TLSYdGLgs_fv94Xvp6aHvNQ6R26T-lbtJDB3crCKWFQ\",\"token_type\":\"bearer\",\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJUZXN0Q29tcGFueUhEX00wMTo3ODg4IiwiY3R4IjoyMTQsInBpZCI6OTA3NiwiYW4iOiJUZXN0Q29tcGFueUhEX00wMSIsInRpZCI6MSwiY2xpZW50X2lkIjoiVGVzdENvbXBhbnlIRF9jbGllbnRfaWQiLCJhcCI6IjEsMTgxNjg0NiwyNTI4MjUyLDI1MjgyNjEiLCJ1aWQiOjI1NDE5MDIsImF0Ijo1LCJzY29wZSI6WyJ3YWxsZXQ6ciIsInR4OnciLCJ0eDpyIl0sImF0aSI6IjEwNGRhZGI3LWI0ZjMtNGVlNy04NTJjLTdkYmRlZWZiNDRjOCIsImV4dHciOmZhbHNlLCJleHAiOjE1NzM0NTYwNTgsImFpZCI6MjUyODI2MSwidXIiOjMsImp0aSI6ImV5SmhiR2NpT2lKSVV6STFOaUlzSW5SNWNDSTZJa3BYVkNKOS5leUp6ZENJNkltWTNPVGsyTTJNNExXRm1Zemd0TkRjMFlTMWlZemczTFRRMU5ETmxaREprTURnd1lTSXNJbU4wZUNJNk1qRTBMQ0oxYzJWeVgyNWhiV1VpT2lKVVpYTjBRMjl0Y0dGdWVVaEVYMDB3TVRvM09EZzRJaXdpZFdsa0lqb3lOVFF4T1RBeUxDSmhhV1FpT2pJMU1qZ3lOakY5LnpScGc4b0NwZVhnN3lJbW1xREpYSnRydUcyOGhxRTNxZE5ESnRIcjBaelEifQ.DHT2XxFh4nC9QL3Kn3CscT7nCMBzttL6EURwLGzEwRA\",\"expires_in\":3599,\"scope\":\"wallet:r tx:w tx:r\",\"jti\":\"104dadb7-b4f3-4ee7-852c-7dbdeefb44c8\"}"));
  }

  /** queue member login rs */
  public void authMemberLoginRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJtZW1iZXJfMjM0MzM1MV8wMDE6NjcyMyIsImN0eCI6MjkxOCwicGlkIjo4MDUzLCJhbiI6Im1lbWJlcl8yMzQzMzUxXzAwMSIsInRpZCI6MiwiY2xpZW50X2lkIjoiaW50ZWdfbmVfdDIiLCJhcCI6IjEsMjMzNjk1MCwyMzQzMzUxLDIzNDMzNTMiLCJ1aWQiOjIzNTQ3MDcsImF0Ijo1LCJzY29wZSI6W10sImV4cCI6MTU4Mjg1ODkyNSwiYWlkIjoyMzQzMzUzLCJ1ciI6MywianRpIjoiM2U2YTMxODgtZTc4ZS00YmY4LWJmMmEtNGFiNGU3ODliNjNlIn0.N9AxTi6KASo8fUlN-55hVLSJ5TOEKl0_NwdeXtskDc4\",\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJtZW1iZXJfMjM0MzM1MV8wMDE6NjcyMyIsImN0eCI6MjkxOCwicGlkIjo4MDUzLCJhbiI6Im1lbWJlcl8yMzQzMzUxXzAwMSIsInRpZCI6MiwiY2xpZW50X2lkIjoiaW50ZWdfbmVfdDIiLCJhcCI6IjEsMjMzNjk1MCwyMzQzMzUxLDIzNDMzNTMiLCJ1aWQiOjIzNTQ3MDcsImF0Ijo1LCJzY29wZSI6W10sImF0aSI6IjNlNmEzMTg4LWU3OGUtNGJmOC1iZjJhLTRhYjRlNzg5YjYzZSIsImV4cCI6MTU4Mjg2NjEyNSwiYWlkIjoyMzQzMzUzLCJ1ciI6MywianRpIjoiMjNkYmE3MmYtMzA4My00MjAyLWEyOGMtYTZjNmZiNzZkZWRlIn0.SA8-oVmeAwPTMOB82TinoAVFNSEGrFGJkm-l6TDOy4w\"}}"));
  }

  /** launch rs */
  public void launcherRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":58},\"data\":\"http://redirect.dirdelivery.com/Casino/Default.aspx?applicationid=1024&variant=vanguard&theme=Launch98&serverid=3003&user=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdCI6ImY4ZjJjODYzLWJiMWYtNDFmNi1hNmNjLWQ1NDQ5OGU1MjI4YSIsImN0eCI6MjEzLCJ1c2VyX25hbWUiOiJUZXN0Q29tcGFueUhEX00wMTo3ODg4IiwidWlkIjoyNTQxOTAyLCJhaWQiOjI1MjgyNjF9.pNkGOB_5dR4HRS5u1NzHWPYLwuuRkmdWGgtUYpbOUIU&gameid=game_launch_id&ul=zh\"}"));
  }

  public void accountBalanceRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\": {\"currency\": \"USD\", \"time_zone\": \"UTC\", \"transaction_id\": \"DEFAULT-TX-ID\", \"processing_time\": 1}, \"data\": {\"currency\": \"CNY\", \"balance\": 100}}"));
  }

  public void accountBalanceEurRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\": {\"currency\": \"EUR\", \"time_zone\": \"UTC\", \"transaction_id\": \"DEFAULT-TX-ID\", \"processing_time\": 1}, \"data\": {\"currency\": \"EUR\", \"balance\": 100}}"));
  }

  public void accountRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":4},\"data\":{\"id\":2536547,\"test\":false,\"effective_test\":false,\"my_path\":\"1,2536527,2536528,2536547\",\"effective_status\":\"ENABLED\",\"version\":16,\"parent_id\":2536528,\"name\":\"HDTestHO_CO1_M01\",\"ext_ref\":\"HDTestHO_CO1_M01\",\"type\":\"MEMBER\",\"currency_unit\":\"USD\",\"status\":\"ENABLED\",\"ip_whitelist\":false,\"created_by\":21,\"created\":\"2019-11-13 03:50:05.110\",\"updated_by\":21,\"updated\":\"2019-12-04 07:21:18.382\"}}"));
  }

  public void accountAppSettingsRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":8},\"data\":[{\"id\":17688,\"account_id\":2536528,\"app_id\":9088,\"status\":\"ENABLED\",\"version\":1,\"created_by\":21,\"created\":\"2019-11-13 03:48:01.290\",\"updated_by\":21,\"updated\":\"2019-11-13 03:48:01.290\"},{\"id\":17689,\"account_id\":2536528,\"app_id\":9087,\"status\":\"ENABLED\",\"wallet_code\":\"CASH1\",\"settings\":{\"server_id\":\"0\"},\"version\":1,\"created_by\":21,\"created\":\"2019-11-13 03:48:44.036\",\"updated_by\":21,\"updated\":\"2019-11-13 03:48:44.036\"},{\"id\":17750,\"account_id\":2536528,\"app_id\":9114,\"status\":\"ENABLED\",\"wallet_code\":\"CASH1\",\"settings\":{\"product_id\":\"8818\"},\"version\":1,\"created_by\":21,\"created\":\"2019-11-15 12:39:22.878\",\"updated_by\":21,\"updated\":\"2019-11-15 12:39:22.878\"},{\"id\":17751,\"account_id\":2536528,\"app_id\":9115,\"status\":\"ENABLED\",\"wallet_code\":\"CASH1\",\"settings\":{\"product_id\":\"8818\",\"lobby_url\":\"https://www.google.com\"},\"version\":3,\"created_by\":21,\"created\":\"2019-11-15 12:39:28.500\",\"updated_by\":2588626,\"updated\":\"2020-01-16 03:02:26.736\"},{\"id\":18050,\"account_id\":2536528,\"app_id\":9247,\"status\":\"ENABLED\",\"wallet_code\":\"CASH1\",\"settings\":{\"redirect_url\":\"https://api.t2.dashur.io\"},\"version\":2,\"created_by\":21,\"created\":\"2020-01-08 09:43:06.890\",\"updated_by\":21,\"updated\":\"2020-01-08 09:50:29.876\"},{\"id\":18051,\"account_id\":2536528,\"app_id\":9248,\"status\":\"ENABLED\",\"wallet_code\":\"CASH1\",\"settings\":{\"redirect_url\":\"http://api.t2.dashur.io\"},\"version\":2,\"created_by\":21,\"created\":\"2020-01-08 09:43:16.077\",\"updated_by\":21,\"updated\":\"2020-01-08 09:50:38.842\"},{\"id\":1,\"account_id\":1,\"app_id\":1,\"status\":\"ENABLED\",\"version\":6,\"created_by\":0,\"created\":\"2017-04-05 03:21:49.685\",\"updated_by\":1,\"updated\":\"2019-01-11 01:48:40.567\"},{\"id\":2,\"account_id\":1,\"app_id\":5,\"status\":\"ENABLED\",\"version\":3,\"created_by\":0,\"created\":\"2017-04-05 03:21:49.685\",\"updated_by\":1,\"updated\":\"2018-05-02 01:59:27.077\"},{\"id\":3,\"account_id\":1,\"app_id\":3,\"status\":\"ENABLED\",\"version\":6,\"created_by\":0,\"created\":\"2017-04-05 03:21:49.742\",\"updated_by\":1,\"updated\":\"2019-01-11 01:48:45.388\"},{\"id\":4,\"account_id\":1,\"app_id\":9,\"status\":\"ENABLED\",\"version\":5,\"created_by\":0,\"created\":\"2017-05-03 03:47:44.480\",\"updated_by\":1,\"updated\":\"2019-01-11 01:48:53.259\"},{\"id\":21,\"account_id\":1,\"app_id\":21,\"status\":\"ENABLED\",\"version\":5,\"created_by\":0,\"created\":\"2019-04-22 04:08:32.653\",\"updated_by\":21,\"updated\":\"2019-05-12 11:42:43.610\"},{\"id\":22,\"account_id\":1,\"app_id\":25,\"status\":\"ENABLED\",\"version\":1,\"created_by\":0,\"created\":\"2019-04-22 04:08:34.021\",\"updated_by\":0,\"updated\":\"2019-04-22 04:08:34.021\"},{\"id\":31,\"account_id\":1,\"app_id\":31,\"status\":\"ENABLED\",\"version\":1,\"created_by\":0,\"created\":\"2019-11-14 16:05:02.730\",\"updated_by\":0,\"updated\":\"2019-11-14 16:05:02.730\"},{\"id\":35,\"account_id\":1,\"app_id\":35,\"status\":\"ENABLED\",\"version\":1,\"created_by\":0,\"created\":\"2019-11-14 16:06:40.541\",\"updated_by\":0,\"updated\":\"2019-11-14 16:06:40.541\"},{\"id\":2212,\"account_id\":1,\"app_id\":2495,\"status\":\"ENABLED\",\"version\":9,\"created_by\":1,\"created\":\"2017-05-18 09:42:37.572\",\"updated_by\":1,\"updated\":\"2019-01-11 01:48:56.003\"},{\"id\":4001,\"account_id\":1,\"app_id\":4001,\"status\":\"ENABLED\",\"version\":3,\"created_by\":-1,\"created\":\"2018-02-02 07:37:25.440\",\"updated_by\":1,\"updated\":\"2018-05-02 01:59:27.084\"},{\"id\":7503,\"account_id\":1,\"app_id\":4955,\"status\":\"ENABLED\",\"version\":3,\"created_by\":1,\"created\":\"2018-02-06 08:27:28.889\",\"updated_by\":1,\"updated\":\"2018-05-02 01:59:28.477\"},{\"id\":13350,\"account_id\":1,\"app_id\":7271,\"status\":\"ENABLED\",\"version\":1,\"created_by\":1,\"created\":\"2018-11-27 09:50:39.600\",\"updated_by\":1,\"updated\":\"2018-11-27 09:50:39.600\"},{\"id\":14267,\"account_id\":1,\"app_id\":7673,\"status\":\"ENABLED\",\"version\":2,\"created_by\":1,\"created\":\"2019-04-25 01:53:05.392\",\"updated_by\":1,\"updated\":\"2019-04-25 01:53:14.572\"},{\"id\":15089,\"account_id\":1,\"app_id\":8053,\"status\":\"ENABLED\",\"version\":1,\"created_by\":21,\"created\":\"2019-05-23 03:47:40.782\",\"updated_by\":21,\"updated\":\"2019-05-23 03:47:40.782\"},{\"id\":30144,\"account_id\":1,\"app_id\":37,\"status\":\"ENABLED\",\"version\":1,\"created_by\":21,\"created\":\"2020-03-16 03:05:28.034\",\"updated_by\":21,\"updated\":\"2020-03-16 03:05:28.034\"},{\"id\":30694,\"account_id\":1,\"app_id\":38,\"status\":\"ENABLED\",\"version\":1,\"created_by\":21,\"created\":\"2020-04-01 04:56:00.058\",\"updated_by\":21,\"updated\":\"2020-04-01 04:56:00.058\"}]}"));
  }

  public void applicationRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":0},\"data\":{\"id\":9114,\"vendor_id\":1045,\"type\":\"TX\",\"platform\":\"FLASH\",\"owner_id\":1,\"version\":4,\"name\":\"PNG Desktop\",\"status\":\"ENABLED\",\"settings_schema\":\"{\\\"properties\\\":{\\\"product_id\\\":{\\\"type\\\":\\\"string\\\",\\\"minLength\\\":1,\\\"maxLength\\\":20},\\\"redirect_url\\\":{\\\"type\\\":\\\"string\\\",\\\"minLength\\\":0,\\\"maxLength\\\":255}},\\\"required\\\":[\\\"product_id\\\"]}\",\"js_methods\":\"function get_lang(pm) {\\n  var ul = pm[\\\"ul\\\"];\\n\\n  if (pm[\\\"tag\\\"] && pm[\\\"tag\\\"].startsWith(\\\"firebird\\\")) {\\n    if (!(ul)) { return \\\"en-us\\\"; }\\n    if (ul.startsWith(\\\"zh\\\")) { return \\\"zh-cn\\\" }\\n    else if (ul.startsWith(\\\"ja\\\")) { return \\\"ja-jp\\\"; }\\n    else if (ul.startsWith(\\\"th\\\")) { return \\\"th-th\\\"; }\\n    else if (ul.startsWith(\\\"vi\\\")) { return \\\"vi-vn\\\"; }\\n    return \\\"en-us\\\";\\n  }\\n\\n  if (!(ul)) { return \\\"en\\\"; }\\n  if (ul.startsWith(\\\"zh\\\")) {\\n    if (ul == \\\"zh\\\" || ul.toLowerCase().indexOf(\\\"cn\\\") > 0) { return \\\"zh-cn\\\"; } else { return \\\"zh-tw\\\"; }\\n  }\\n  else if (ul.startsWith(\\\"ja\\\")) { return \\\"ja\\\"; }\\n  else if (ul.startsWith(\\\"ko\\\")) { return \\\"ko\\\"; }\\n  else if (ul == \\\"in\\\") {\\n    if (pm[\\\"tag\\\"] && pm[\\\"tag\\\"].startsWith(\\\"livedealer\\\")) { return \\\"in\\\"; } else { return \\\"id\\\"; }\\n  }\\n  else if (\\\"|th|vi|\\\".indexOf(\\\"|\\\" + ul + \\\"|\\\") >= 0) { return ul; }\\n  return \\\"en\\\";\\n}\\n\\nfunction get_launch_url(pm) {\\n  pm = JSON.parse(pm);\\n  if (!(pm)) { return \\\"\\\"; }\\n  var ul = get_lang(pm);\\n  if (ul == \\\"zh-cn\\\") { ul = \\\"zh\\\"; }\\n  if (pm[\\\"demo\\\"]) {\\n    return \\\"https://asistage.playngonetwork.com/casino/ContainerLauncher?pid=8818&gid=\\\" + pm[\\\"launch_id\\\"] + \\\"&lang=\\\" + ul + \\\"&practice=1&channel=desktop&user=\\\" + pm[\\\"token\\\"];\\n  }\\n  return \\\"https://asistage.playngonetwork.com/casino/ContainerLauncher?pid=8818&gid=\\\" + pm[\\\"launch_id\\\"] + \\\"&lang=\\\" + ul + \\\"&practice=0&channel=desktop&user=\\\" + pm[\\\"token\\\"];\\n}\\n\\nfunction get_tx_detail_url(pm) {\\n  pm = JSON.parse(pm);\\n  if (!(pm)) { return \\\"\\\"; }\\n  var ul = get_lang(pm);\\n  if (!(pm[\\\"tag\\\"]) && ul == \\\"zh-cn\\\") { ul = \\\"zh\\\"; }\\n  if (pm[\\\"png\\\"]) {\\n    var png = pm[\\\"png\\\"];\\n    return \\\"https://asistage.playngonetwork.com/casinohistory/Details/\\\"+pm[\\\"round_id\\\"]+\\\"?userId=\\\" + pm[\\\"account_id\\\"] + \\\"_\\\" + pm[\\\"user_id\\\"] + \\\"&pid=8818&lang=\\\" + ul;\\n  } else {\\n    return \\\"https://asistage.playngonetwork.com/casinohistory/?userId=\\\" + pm[\\\"account_id\\\"] + \\\"_\\\" + pm[\\\"user_id\\\"] + \\\"&pid=8818&lang=\\\" + ul;\\n  }\\n}\\n\",\"client_id\":\"PNGDesktop_client_id\",\"scopes\":[\"campaign:w\",\"user:w\",\"wallet:w\",\"wallet:r\",\"audit:r\",\"campaign:r\",\"report:r\",\"user:r\",\"tx:r\",\"category:r\",\"compliance:r\",\"account:w\",\"item:r\",\"tx:w\",\"exchange_rates:r\",\"account:r\"],\"allowed_account_types\":[\"MEMBER\",\"HEAD_OFFICE\",\"COMPANY\",\"SUB_COMPANY\"],\"allowed_user_types\":[\"USER\",\"API\"],\"grant_types\":[\"refresh_token\",\"password\"],\"linked\":false,\"created_by\":21,\"created\":\"2019-11-15 12:27:29.364\",\"updated_by\":21,\"updated\":\"2019-12-04 07:08:45.775\"}}"));
  }

  public void itemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":34},\"data\":{\"id\":12135,\"app_items\":[{\"id\":13281,\"app_id\":9087,\"item_id\":12135,\"version\":2,\"ext_ref\":\"\",\"status\":\"DISABLED\",\"meta_data\":{\"launch_id\":\"\"},\"created_by\":21,\"created\":\"2019-11-15 02:01:16.882\",\"updated_by\":21,\"updated\":\"2019-11-15 12:32:04.684\"},{\"id\":13292,\"app_id\":9114,\"item_id\":12135,\"version\":1,\"ext_ref\":\"287\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"towerquest\"},\"created_by\":21,\"created\":\"2019-11-15 12:32:04.684\",\"updated_by\":21,\"updated\":\"2019-11-15 12:32:04.684\"},{\"id\":13293,\"app_id\":9115,\"item_id\":12135,\"version\":1,\"ext_ref\":\"100287\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"towerquestmobile\"},\"created_by\":21,\"created\":\"2019-11-15 12:32:04.684\",\"updated_by\":21,\"updated\":\"2019-11-15 12:32:04.684\"}],\"version\":2,\"category_id\":3240,\"vendor_id\":1045,\"name\":\"Tower Quest\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2019-11-15 02:01:16.881\",\"updated_by\":21,\"updated\":\"2019-11-15 12:32:04.683\"}}"));
  }

  public void userRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"ac9dca30-7adb-4a51-ba98-ad58007efac7\",\"processing_time\":6},\"data\":[{\"id\":2594769,\"user_type\":\"USER\",\"version\":1,\"account_id\":2579232,\"name\":\"member_2579231_001\",\"username\":\"member_2579231_001:8980\",\"status\":\"ENABLED\",\"role\":\"NORMAL_USER\",\"password_reset\":false,\"two_factor_auth\":\"NONE\",\"created_by\":21,\"created\":\"2020-03-10 15:30:30.074\",\"updated_by\":21,\"updated\":\"2020-03-10 15:30:30.074\"}]}"));
  }

  public void theYearOfZhuItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":23},\"data\":{\"id\":12344,\"app_items\":[{\"id\":13546,\"app_id\":9240,\"item_id\":12344,\"version\":1,\"ext_ref\":\"the-year-of-zhu\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"the-year-of-zhu\"},\"created_by\":2588627,\"created\":\"2020-01-14 08:46:18.104\",\"updated_by\":2588627,\"updated\":\"2020-01-14 08:46:18.104\"}],\"version\":1,\"category_id\":4046,\"vendor_id\":1047,\"name\":\"The Year of Zhu\",\"pool_percentage\":0,\"created_by\":2588627,\"created\":\"2020-01-14 08:46:18.104\",\"updated_by\":2588627,\"updated\":\"2020-01-14 08:46:18.104\"}}"));
  }

  public void valkyrieItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":34},\"data\":{\"id\":26280,\"app_items\":[{\"id\":28455,\"app_id\":21297,\"item_id\":26280,\"version\":1,\"ext_ref\":\"10029\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"10029\"},\"created_by\":21,\"created\":\"2020-05-22 16:09:58.487\",\"updated_by\":21,\"updated\":\"2020-05-22 16:09:58.487\"},{\"id\":28456,\"app_id\":21298,\"item_id\":26280,\"version\":1,\"ext_ref\":\"10029\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"10029\"},\"created_by\":21,\"created\":\"2020-05-22 16:09:58.487\",\"updated_by\":21,\"updated\":\"2020-05-22 16:09:58.487\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1057,\"name\":\"Valkyrie\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-05-22 16:09:58.486\",\"updated_by\":21,\"updated\":\"2020-05-22 16:09:58.486\"}}"));
  }

  public void iceWolfItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":34},\"data\":{\"id\":26289,\"app_items\":[{\"id\":28467,\"app_id\":21297,\"item_id\":26289,\"version\":1,\"ext_ref\":\"10038\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"10038\"},\"created_by\":21,\"created\":\"2020-05-25 14:48:12.136\",\"updated_by\":21,\"updated\":\"2020-05-25 14:48:12.136\"},{\"id\":28468,\"app_id\":21298,\"item_id\":26289,\"version\":1,\"ext_ref\":\"10038\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"10038\"},\"created_by\":21,\"created\":\"2020-05-25 14:48:12.136\",\"updated_by\":21,\"updated\":\"2020-05-25 14:48:12.136\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1057,\"name\":\"Ice Wolf\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-05-25 14:48:12.135\",\"updated_by\":21,\"updated\":\"2020-05-25 14:48:12.135\"}}"));
  }

  public void wildCauldronItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":34},\"data\":{\"id\":24022,\"app_items\":[{\"id\":25636,\"app_id\":9247,\"item_id\":24022,\"version\":1,\"ext_ref\":\"wildcauldron\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"wildcauldron\"},\"created_by\":21,\"created\":\"2020-03-18 19:04:13.598\",\"updated_by\":21,\"updated\":\"2020-03-18 19:04:13.598\"},{\"id\":25637,\"app_id\":9248,\"item_id\":24022,\"version\":1,\"ext_ref\":\"wildcauldron\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"wildcauldron\"},\"created_by\":21,\"created\":\"2020-03-18 19:04:13.598\",\"updated_by\":21,\"updated\":\"2020-03-18 19:04:13.598\"}],\"version\":1,\"category_id\":2773,\"vendor_id\":1052,\"name\":\"Wild Cauldron\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-03-18 19:04:13.594\",\"updated_by\":21,\"updated\":\"2020-03-18 19:04:13.594\"}}"));
  }

  public void templeTumbleItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":34},\"data\":{\"id\":12342,\"app_items\":[{\"id\":13543,\"app_id\":9247,\"item_id\":12342,\"version\":1,\"ext_ref\":\"templetumble\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"templetumble\"},\"created_by\":21,\"created\":\"2020-01-08 17:42:24.467\",\"updated_by\":21,\"updated\":\"2020-01-08 17:42:24.467\"},{\"id\":13544,\"app_id\":9248,\"item_id\":12342,\"version\":1,\"ext_ref\":\"templetumble\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"templetumble\"},\"created_by\":21,\"created\":\"2020-01-08 17:42:24.467\",\"updated_by\":21,\"updated\":\"2020-01-08 17:42:24.467\"}],\"version\":1,\"category_id\":2773,\"vendor_id\":1052,\"name\":\"Temple Tumble\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-01-08 17:42:24.458\",\"updated_by\":21,\"updated\":\"2020-01-08 17:42:24.458\"}}"));
  }

  public void winterberriesItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"42b9f3ce-4a1f-42c9-896c-6c976a2d7dc0\",\"processing_time\":28},\"data\":{\"id\":24751,\"app_items\":[{\"id\":26546,\"app_id\":20424,\"item_id\":24751,\"version\":1,\"ext_ref\":\"7302\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"7302\"},\"created_by\":21,\"created\":\"2020-04-16 15:57:07.440\",\"updated_by\":21,\"updated\":\"2020-04-16 15:57:07.440\"},{\"id\":26547,\"app_id\":20425,\"item_id\":24751,\"version\":1,\"ext_ref\":\"7302\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"7302\"},\"created_by\":21,\"created\":\"2020-04-16 15:57:07.440\",\"updated_by\":21,\"updated\":\"2020-04-16 15:57:07.440\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1055,\"name\":\"Winterberries\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-04-16 15:57:07.440\",\"updated_by\":21,\"updated\":\"2020-04-16 15:57:07.440\"}}"));
  }

  public void moonPrincessItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"5950e788-0fac-472e-967a-e4876fc1c329\",\"processing_time\":33},\"data\":{\"id\":29809,\"app_items\":[{\"id\":32889,\"app_id\":9114,\"item_id\":29809,\"version\":1,\"ext_ref\":\"334\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"moonprincess\"},\"created_by\":2678318,\"created\":\"2020-11-17 13:03:11.836\",\"updated_by\":2678318,\"updated\":\"2020-11-17 13:03:11.836\"},{\"id\":32890,\"app_id\":9115,\"item_id\":29809,\"version\":1,\"ext_ref\":\"334\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"moonprincessmobile\"},\"created_by\":2678318,\"created\":\"2020-11-17 13:03:11.836\",\"updated_by\":2678318,\"updated\":\"2020-11-17 13:03:11.836\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1045,\"name\":\"Moon Princess\",\"pool_percentage\":0,\"created_by\":2678318,\"created\":\"2020-11-17 13:03:11.830\",\"updated_by\":2678318,\"updated\":\"2020-11-17 13:03:11.830\"}}"));
  }

  public void greatPandaItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"2ead9d00-d1ad-4227-92ea-2bee2865b7bb\",\"processing_time\":43},\"data\":{\"id\":27008,\"app_items\":[{\"id\":29366,\"app_id\":21765,\"item_id\":27008,\"version\":1,\"ext_ref\":\"181\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"181\"},\"created_by\":21,\"created\":\"2020-06-16 15:45:31.583\",\"updated_by\":21,\"updated\":\"2020-06-16 15:45:31.583\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1059,\"name\":\"Great Panda\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2020-06-16 15:45:31.582\",\"updated_by\":21,\"updated\":\"2020-06-16 15:45:31.582\"}}"));
  }

  public void bookOfOilItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"2ead9d00-d1ad-4227-92ea-2bee2865b7bb\",\"processing_time\":43},\"data\":{\"id\":35905,\"app_items\":[{\"id\":40760,\"app_id\":25439,\"item_id\":35905,\"version\":1,\"ext_ref\":\"41199d76bffde4da79fa9166337f736a600dbe1a\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"41199d76bffde4da79fa9166337f736a600dbe1a\"},\"created_by\":21,\"created\":\"2021-09-27 15:11:05.542\",\"updated_by\":21,\"updated\":\"2021-09-27 15:11:05.542\"}],\"version\":1,\"category_id\":14829,\"vendor_id\":1076,\"name\":\"Book of Oil\",\"pool_percentage\":0,\"created_by\":21,\"created\":\"2021-09-27 15:11:05.542\",\"updated_by\":21,\"updated\":\"2021-09-27 15:11:05.542\"}}"));
  }

  public void steamTowerItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"7ccdb46d-99f0-4d40-8f87-7e998030fd63\",\"processing_time\":48},\"data\":{\"id\":10125,\"app_items\":[{\"id\":10949,\"app_id\":8054,\"item_id\":10125,\"version\":2,\"ext_ref\":\"steamtower_mobile_html_sw\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"steamtower_mobile_html_sw\"}},{\"id\":10950,\"app_id\":8055,\"item_id\":10125,\"version\":2,\"ext_ref\":\"steamtower_not_mobile_sw\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"steamtower_not_mobile_sw\"}},{\"id\":38562,\"app_id\":24674,\"item_id\":10125,\"version\":1,\"ext_ref\":\"steamtower\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"steamtower000000\"}}],\"version\":2,\"category_id\":3252,\"vendor_id\":1020,\"name\":\"Steam Tower™\",\"pool_percentage\":0}}"));
  }

  public void deadOrAlive2ItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC+08:00\",\"transaction_id\":\"5461dce0-8af2-425b-85b9-45a3b9fb09bf\",\"processing_time\":59},\"data\":{\"id\":10082,\"app_items\":[{\"id\":10865,\"app_id\":8054,\"item_id\":10082,\"version\":2,\"ext_ref\":\"deadoralive2_mobile_html_sw\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"deadoralive2_mobile_html_sw\"}},{\"id\":10866,\"app_id\":8055,\"item_id\":10082,\"version\":2,\"ext_ref\":\"deadoralive2_not_mobile_sw\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"deadoralive2_not_mobile_sw\"}},{\"id\":38360,\"app_id\":24674,\"item_id\":10082,\"version\":1,\"ext_ref\":\"deadoralive2\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"deadoralive20000\"}}],\"version\":2,\"category_id\":3237,\"vendor_id\":1020,\"name\":\"Dead or Alive 2\",\"pool_percentage\":0}}"));
  }

  public void appItemRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":4},\"data\":{\"id\":13563,\"app_id\":9240,\"item_id\":12361,\"version\":1,\"ext_ref\":\"a-fairy-tale\",\"status\":\"ENABLED\",\"meta_data\":{\"launch_id\":\"a-fairy-tale\"},\"created_by\":2588627,\"created\":\"2020-01-21 14:01:09.071\",\"updated_by\":2588627,\"updated\":\"2020-01-21 14:01:09.071\"}}"));
  }

  public void walletRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":9},\"data\":[{\"type\":\"CASH\",\"id\":2559237,\"version\":1,\"multi_currency\":false,\"account_id\":2536547,\"code\":\"CASH1\",\"name\":\"Wallet Cash\",\"created_by\":21,\"created\":\"2019-11-13 03:50:05.111\",\"updated_by\":21,\"updated\":\"2019-11-13 03:50:05.111\",\"currency_unit\":\"USD\",\"cash_balance\":747.87}]}"));
  }

  public void txRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"CATEGORY\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txRsRefundWithoutBalance() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"CATEGORY\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":0,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txRsRefundWithoutBalance2() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"CATEGORY\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txWagerRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"WAGER\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txPayoutRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"PAYOUT\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txRefundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"REFUND\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txEndRoundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"ENDROUND\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txFeedRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":22,\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"PAYOUT\",\"sub_category\":\"\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"CREDIT\",\"amount\":20,\"balance\":2000},{\"id\":23,\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"ENDROUND\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"UNKNOWN\",\"amount\":0.00,\"balance\":2000,\"num_of_wager\":1,\"num_of_payout\":1,\"num_of_refund\":0}]}"));
  }

  public void txFeedWagerRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"wallet_code\":\"CASH1\",\"external_ref\":\"1076-db3e2e40e5b0471093912a2b2cb0ba91-W\",\"category\":\"WAGER\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"DEBIT\",\"amount\":0.50,\"meta_data\":{\"round_id\":\"1076-d473986f90e74ca0ab5931960c146a3f_13\",\"ext_item_id\":\"41199d76bffde4da79fa9166337f736a600dbe1a\",\"item_id\":35905},\"id\":167792026,\"parent_transaction_id\":167792026,\"account_id\":2916620,\"account_ext_ref\":\"member_2916618_01\",\"application_id\":25439,\"currency_unit\":\"EUR\",\"transaction_time\":\"2021-10-01 14:48:55.878\",\"balance\":1166.85,\"pool_amount\":0.00}}"));
  }

  public void txFeedPayoutRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"wallet_code\":\"CASH1\",\"external_ref\":\"1076-879767e92e2042c589eabfb192fd2be3-P\",\"category\":\"PAYOUT\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"CREDIT\",\"amount\":0.00,\"meta_data\":{\"round_id\":\"1076-d473986f90e74ca0ab5931960c146a3f_13\",\"ext_item_id\":\"41199d76bffde4da79fa9166337f736a600dbe1a\",\"item_id\":35905},\"id\":167792028,\"parent_transaction_id\":167792028,\"account_id\":2916620,\"account_ext_ref\":\"member_2916618_01\",\"application_id\":25439,\"currency_unit\":\"EUR\",\"transaction_time\":\"2021-10-01 14:48:56.214\",\"balance\":1166.85,\"pool_amount\":0.00}}"));
  }

  public void txFeedEndRoundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"wallet_code\":\"CASH1\",\"external_ref\":\"1076-879767e92e2042c589eabfb192fd2be3-E\",\"category\":\"ENDROUND\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"UNKNOWN\",\"amount\":0.00,\"meta_data\":{\"round_id\":\"1076-d473986f90e74ca0ab5931960c146a3f_13\",\"ext_item_id\":\"41199d76bffde4da79fa9166337f736a600dbe1a\",\"item_id\":35905},\"id\":167792028,\"parent_transaction_id\":167792028,\"account_id\":2916620,\"account_ext_ref\":\"member_2916618_01\",\"application_id\":25439,\"currency_unit\":\"EUR\",\"transaction_time\":\"2021-10-01 14:48:56.214\",\"balance\":1166.85,\"pool_amount\":0.00}}"));
  }

  public void txFeedWithVendorMetaRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"test\":true,\"num_of_wager\":1,\"sum_of_wager\":0.80,\"num_of_payout\":0,\"sum_of_payout\":0.00,\"num_of_refund\":0,\"sum_of_refund_credit\":0.00,\"sum_of_refund_debit\":0.00,\"revenue\":0.80,\"transaction_ids\":[1,2],\"wallet_code\":\"CASH1\",\"external_ref\":\"1068-51157328-E\",\"category\":\"ENDROUND\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"UNKNOWN\",\"amount\":0.00,\"meta_data\":{\"round_id\":\"1068-51157328\",\"ext_item_id\":\"whk\",\"item_id\":31521,\"vendor\":{\"ngscode\":\"15564443\",\"remotesessionenddate\":\"2021-01-01 00:00:00\",\"username\":\"FLOW__2594769\",\"token\":\"f81dc9cfb9f13b768f8bb775d51a16274c08b3e4e1927aabe340429df4437887\"}},\"id\":146190545,\"parent_transaction_id\":146190545,\"account_id\":2579232,\"account_ext_ref\":\"member_2579231_001\",\"application_id\":24350,\"currency_unit\":\"CNY\",\"balance\":2175.54,\"pool_amount\":0.00}}"));
  }

  public void txFeedMissingVendorMetaRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"test\":true,\"num_of_wager\":1,\"sum_of_wager\":0.80,\"num_of_payout\":0,\"sum_of_payout\":0.00,\"num_of_refund\":0,\"sum_of_refund_credit\":0.00,\"sum_of_refund_debit\":0.00,\"revenue\":0.80,\"transaction_ids\":[1,2],\"wallet_code\":\"CASH1\",\"external_ref\":\"1068-51157328-E\",\"category\":\"ENDROUND\",\"balance_type\":\"CASH_BALANCE\",\"type\":\"UNKNOWN\",\"amount\":0.00,\"meta_data\":{\"round_id\":\"1068-51157328\",\"ext_item_id\":\"whk\",\"item_id\":31521,\"vendor\":{}},\"id\":146190545,\"parent_transaction_id\":146190545,\"account_id\":2579232,\"account_ext_ref\":\"member_2579231_001\",\"application_id\":24350,\"currency_unit\":\"CNY\",\"balance\":2175.54,\"pool_amount\":0.00}}"));
  }

  public void txFeedRoundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":1,\"account_id\":1,\"account_ext_ref\":\"member_ext_ref\",\"application_id\":24350,\"wallet_code\":\"WALLET\",\"currency_unit\":\"SGD\",\"external_ref\":\"external_ref\",\"status\":\"CLOSED\",\"transaction_ids\":[1,2],\"num_of_wager\":1,\"sum_of_wager\":0.40,\"num_of_payout\":1,\"sum_of_payout\":0.05,\"num_of_refund\":0,\"sum_of_refund\":0.00,\"sum_of_refund_credit\":0.00,\"sum_of_refund_debit\":0.00,\"meta_data\":{\"round_id\":\"1068-51157326\",\"ext_item_id\":\"whk\",\"item_id\":31521,\"vendor\":{}},\"start_balance\":1000.35,\"last_balance\":1000.00,\"close_balance\":1000.00}]}"));
  }

  public void txRoundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":{\"id\":\"1\",\"account_id\":1,\"account_ext_ref\":\"member_001\",\"currency_unit\":\"SGD\",\"wallet_code\":\"WALLET\",\"external_ref\":\"external_ref\",\"status\":\"OPEN\",\"transaction_ids\":[190616008,190616009],\"num_of_wager\":1,\"num_of_payout\":1,\"num_of_refund\":0,\"sum_of_wager\":100,\"sum_of_payout\":200,\"sum_of_refund_credit\":0,\"sum_of_refund_debit\":0,\"start_balance\":1000,\"last_balance\":1000,\"close_balance\":1000,\"meta_data\":{\"round_id\":\"1000-123456789\",\"ext_item_id\":\"825\",\"item_id\":38063}}}"));
  }

  public void txRsWithConflict() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setResponseCode(HttpStatus.SC_CONFLICT)
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"CATEGORY\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txRsWithResponseWithRefund() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"1\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"CATEGORY\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}},{\"id\":\"2\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"REFUND\",\"sub_category\":\"\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txRefundOriginalNotFoundRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[{\"id\":\"2\",\"account_id\":1,\"currency_unit\":\"SGD\",\"transaction_time\":\"2019-11-01 00:00:00\",\"wallet_code\":\"WALLET\",\"category\":\"REFUND\",\"sub_category\":\"ORIGINAL_NOT_FOUND\",\"balance_type\":\"\",\"type\":\"\",\"amount\":100,\"balance\":1000,\"meta_data\":{\"meta\":\"data\"}}]}"));
  }

  public void txEmptyRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"data\":[]}"));
  }

  public void txErrRs() {
    put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_PAYMENT_REQUIRED)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":402,\"message\":\"Payment required\"}}"));
  }

  public void txErrRsNotExists() {
    put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_NOT_FOUND)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":404,\"message\":\"Not Exists\"}}"));
  }

  public void txErrRsAlreadyCancelled() {
    put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_BAD_REQUEST)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"INVALID_PARAMETERS\",\"code\":-54,\"message\":\"Transaction already cancelled\"}}"));
  }

  public void serverErrorRs() {
    put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":500,\"message\":\"Unexpected error\"}}"));
  }

  public void authErrorRs() {
    put(
        new MockResponse()
            .setResponseCode(HttpStatus.SC_UNAUTHORIZED)
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":-1},\"error\":{\"type\":\"HTTP_EXCEPTION\",\"code\":401,\"message\":\"Authentication failed\"}}"));
  }

  public void searchCampaignRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":8},\"data\":{\"id\":140,\"account_id\":2579231,\"name\":\"Test Campaign 01\",\"ext_ref\":\"campaign_ext_ref\",\"vendor_ref\":\"10029::campaign_ext_ref\",\"num_of_games\":10,\"status\":\"ACTIVE\",\"type\":\"FREE_GAMES\",\"game_id\":26280,\"bet_level\":1,\"currency\":\"CNY\",\"start_time\":\"2020-07-09 17:30:00.000\",\"end_time\":\"2020-07-10 17:30:00.000\",\"meta_data\":{\"cost_per_bet\":2.00},\"version\":4}}"));
  }

  public void searchCampaignEmptyRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(
                "{\"meta\":{\"currency\":\"USD\",\"time_zone\":\"UTC\",\"transaction_id\":\"DEFAULT-TX-ID\",\"processing_time\":5},\"error\":{\"type\":\"ENTITY_NOT_FOUND\",\"code\":-9,\"message\":\"Entity not found for Campaign(127)\"}}"));
  }

  public void updateCampaignRs() {
    put(
        new MockResponse()
            .setHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(CommonUtils.jsonToString(new CampaignModel())));
  }
}
