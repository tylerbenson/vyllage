#!/bin/bash -ex

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"
sudo cp $DIR/../supervisord.conf /etc/supervisor/
sudo cp $DIR/../vyllage.conf /etc/supervisor/conf.d/

rm -rf /opt/vyllage/ || true
sudo mkdir -p /opt/vyllage/log/
sudo chmod -R 777 /opt/vyllage

sudo /usr/bin/supervisorctl reread
sudo /usr/bin/supervisorctl update
