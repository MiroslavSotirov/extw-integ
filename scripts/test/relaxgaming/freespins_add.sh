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

#TX_ID=114551608043791948 # $(uuid -F siv | cut -c 3-20)
TX_ID="1$(uuid -F siv | cut -c 3-20)"
PLAYER_ID=83224
GAMEREF=rlx.em.em.7985
CURRENCY=EUR
AMOUNT=10
FREESPINS_VALUE=100
EXPIRES=$(date -u -Iseconds --date "today + 7 days")
PROMOCODE="promo-$(uuid | cut -c -8)" 
echo "PROMOCODE:$PROMOCODE"
user_input "enter playerid (empty to cancel, default $PLAYER_ID):"
PLAYER_ID=$INPUT
echo "PLAYER_ID $PLAYER_ID"

echo "enter game id"
echo "Fox Tale:       7797"
echo "Spirit Hunters: 7985"
echo "Wizardz World:  8243"
echo "Battle ofMyths: 8359"
echo "Sword King:     8451"
user_input "(empty to cancel, default $GAMEREF):"
GAMEREF="rlx.em.em.$INPUT"
echo "GAMEREF $GAMEREF"

CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "credentials": '$CRED', "jurisdiction": "EU", "txid": '$TX_ID', "playerid": '$PLAYER_ID',  
	"partnerid": '$PARTNER_ID', "gameref": "'$GAMEREF'", "amount": '$AMOUNT', 
	"freespinvalue": '$FREESPINS_VALUE', "expires": "'$EXPIRES'", "currency": "'$CURRENCY'" }'
#	"freespinvalue": '$FREESPINS_VALUE', "expires": "'$EXPIRES'", "currency": "'$CURRENCY'", "promocode": "'$PROMOCODE'" }'

echo $REQ

curl -i "$BASE_URL/v1/extw/exp/relaxgaming/freespins/add" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: $AUTH"
