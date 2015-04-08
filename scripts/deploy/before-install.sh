#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"
sudo cp $DIR/../supervisord.conf /etc/supervisor/
sudo cp $DIR/../vyllage.conf /etc/supervisor/conf.d/
/usr/bin/supervisorctl reread

sudo mkdir -p /opt/vyllage/log/
sudo chmod -R 777 vyllage
touch /opt/vyllage/blank.jar
rm /opt/vyllage/*.jar
