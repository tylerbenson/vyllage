#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"
sudo cp $DIR/../supervisord.conf /etc/supervisor/
sudo cp $DIR/../vyllage.conf /etc/supervisor/conf.d/

rm -rf /opt/vyllage/ || true
sudo mkdir -p /opt/vyllage/log/
sudo chmod -R 777 /opt/vyllage

/usr/bin/supervisorctl reread
/usr/bin/supervisorctl update
