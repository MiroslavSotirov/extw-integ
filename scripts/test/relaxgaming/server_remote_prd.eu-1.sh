#!/bin/sh

## set up script env for remote server connection

# prd.eu-1 env
export BASE_URL="https://publicdeployment.eu-1.elysiumstudios.se"
export COMPANY_ID=435224302
export PARTNER_ID=273
export AUTH="Basic ZW06M2VaZkhORWJvZUQxVm52OA=="

echo "server location set to $BASE_URL with partner id $PARTNER_ID and company id $COMPANY_ID"