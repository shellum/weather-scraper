#!/bin/bash
if [ -z $1 ];
then
    echo "Usage: build.sh <docker user> <version>"
    echo "This builds and pushes an updated image to a docker repo"
    exit 1
fi
sbt "clean; assembly"
docker build . -t $1/weather-scraper:$2
docker push $1/weather-scraper:$2