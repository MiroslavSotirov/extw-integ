#!/bin/sh

## set up script env for remote server connection

# prd env extw-integ
export BASE_URL="https://public.eu-1.elysiumstudios.se"
export COMPANY_ID=366276199
export PARTNER_ID=10
export AUTH="Basic ZW06UzdiQTB4NVh6ZG5TUTU3Uw=="

echo "server location set to $BASE_URL with partner id $PARTNER_ID and company id $COMPANY_ID"