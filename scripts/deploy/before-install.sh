#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"
sudo cp $DIR/../supervisord.conf /etc/supervisor/
sudo cp $DIR/../vyllage.conf /etc/supervisor/conf.d/

sudo mkdir -p /opt/vyllage/log/
sudo chmod -R 777 /opt/vyllage
sudo chmod -R 777 /opt/vyllage/newrelic

/usr/bin/supervisorctl reread
/usr/bin/supervisorctl update

rm /opt/vyllage/*.jar || true
rm /opt/vyllage/appspec.yml || true
rm -r /opt/vyllage/scripts || true
