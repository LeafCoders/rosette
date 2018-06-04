#!/bin/bash

export ROSETTE_JWTSECRET='youShouldReplaceThisWithYourOwn'
export ROSETTE_URL='http://localhost:9001'
export CORDATE_APPNAME='Cordate'
export CORDATE_URL='http://localhost:80'

docker-compose up