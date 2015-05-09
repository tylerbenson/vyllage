#!/bin/bash

sudo chmod 777 /opt/vyllage/newrelic
ln -s /opt/vyllage/site-*.jar /opt/vyllage/site.jar
ln -s /opt/vyllage/newrelic/newrelic-agent-*.jar /opt/vyllage/newrelic/newrelic.jar
