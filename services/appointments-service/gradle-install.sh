#!/bin/bash

GRADLE_VERSION=8.7

cd /tmp
curl -LO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip

sudo mkdir -p /opt/gradle
sudo unzip -q gradle-${GRADLE_VERSION}-bin.zip -d /opt/gradle
sudo ln -sf /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle

gradle -v

