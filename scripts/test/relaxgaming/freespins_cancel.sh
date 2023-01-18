#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi


INPUT=""
user_input() {
	read -p "$1" INPUT
	if [ -z $INPUT ];
	then
		exit 1;
	fi
}

PLAYER_ID=83224
FREESPINS_ID=1107

user_input "enter playerid (empty to cancel. example $PLAYER_ID):"
PLAYER_ID=$INPUT
user_input "enter freespinsid (empty to cancel. example $FREESPINS_ID):"
FREESPINS_ID=$INPUT

CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "credentials": '$CRED', "jurisdiction": "EU", "playerid": '$PLAYER_ID',  
	"partnerid": '$PARTNER_ID', "freespinsid": "'$FREESPINS_ID'"}'

echo $REQ

curl -i "$BASE_URL/v1/extw/exp/relaxgaming/freespins/cancel" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: $AUTH"
