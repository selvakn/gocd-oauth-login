#!/bin/sh
rm -rf dist/
mkdir dist

mvn clean install --batch-mode -DskipTests -P gitlab.oauth.login
cp target/gitlab-oauth-*.jar dist/

