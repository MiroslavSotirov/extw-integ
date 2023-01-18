#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

curl -i "$BASE_URL/v1/extw/exp/relaxgaming/version"