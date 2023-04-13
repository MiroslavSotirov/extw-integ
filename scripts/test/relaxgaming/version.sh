#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

URL="$BASE_URL/v1/extw/exp/relaxgaming/version"
CMD="curl -v $URL"
echo "$CMD"
curl -v "$URL" # "$BASE_URL/v1/extw/exp/relaxgaming/version"