#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

HMAC_KEY="test"
CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{"roundid": "1040-79gw81PYSF+hXHjiUggUcQ==", "ended": "False", "category":"WAGER", "playerid": 2123, "timestamp": 1659007735716, "txtype": "withdraw", "amount": 60, "clientid": "test", "txid": "84430624498", "currency": "EUR", "sessionid": 167487, "requestid": "e5ffc1e7-ca48-482a-a300-248d71bd8808", "channel": "mobile", "gameref": "rlx.em.em.8243"}'
HMAC=$(echo -n "$REQ" | openssl dgst -hmac "$HMAC_KEY" -binary | base64)

echo $REQ

curl -v "$BASE_URL/v1/extw/connect/relaxgaming/$COMPANY_ID/v1/transaction" -d "$REQ" -H "X-DAS-HMAC: $HMAC"