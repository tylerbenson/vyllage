#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"

mkdir -p /opt/vyllage/log/
touch /opt/vyllage/blank.jar
rm /opt/vyllage/*.jar
cp $DIR/../../site-*.jar /opt/vyllage/
ln -s /opt/vyllage/site-*.jar /opt/vyllage/site.jar
