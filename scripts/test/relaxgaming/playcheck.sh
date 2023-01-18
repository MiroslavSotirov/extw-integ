#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

read -p 'enter roundid (empty to cancel):' ROUND_ID

if [ -z $ROUND_ID ];
then
	exit 1
fi



CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "roundid": "'$ROUND_ID'", "credentials": '$CRED', "jurisdiction": "EU", "locale": "EU" }'

echo $REQ

curl -i "$BASE_URL/v1/extw/exp/relaxgaming/playcheck" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: $AUTH"
