#!/bin/bash

pwd

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"

mkdir -p /opt/vyllage/log/
touch /opt/vyllage/blank.jar
rm /opt/vyllage/*.jar
cp $DIR/../../site-*.jar /opt/vyllage/
