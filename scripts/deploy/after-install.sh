#!/bin/bash

sudo chmod 777 /opt/vyllage/newrelic
ln -s /opt/vyllage/site-*.jar /opt/vyllage/site.jar
ln -s /opt/vyllage/newrelic/newrelic-agent-*.jar /opt/vyllage/newrelic/newrelic.jar

REV=`aws deploy list-application-revisions --application-name=vyllage --sort-by lastUsedTime --sort-order descending | grep "key" | head -n 1 | sed -r 's/.*"vyllage-(.+)\.zip"/\1/'`
java -jar /opt/vyllage/newrelic/newrelic.jar deployment --revision=$REV || true
