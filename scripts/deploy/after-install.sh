#!/bin/bash -ex

sudo chmod 777 /opt/vyllage/newrelic
ln -s /opt/vyllage/site-*.jar /opt/vyllage/site.jar
ln -s /opt/vyllage/newrelic/newrelic-agent-*.jar /opt/vyllage/newrelic/newrelic.jar

REV=`aws deploy list-application-revisions --application-name=vyllage --sort-by lastUsedTime --sort-order descending --region us-west-2 | grep "key" | head -n 1 | sed -r 's/.*"vyllage-(.+)\.zip"/\1/'`
java -jar /opt/vyllage/newrelic/newrelic.jar deployment --revision=$REV & # Probably don't want this holding up the deploy.
