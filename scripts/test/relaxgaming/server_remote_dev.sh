#!/bin/sh

## set up script env for remote server connection

# dev env extw-integ
export BASE_URL="https://dev.elysiumstudios.se"
export COMPANY_ID=435224302 # 366276199
export PARTNER_ID=10
export AUTH="Basic ZW06dGVzdA=="

echo "server location set to $BASE_URL with partner id $PARTNER_ID and company id $COMPANY_ID"