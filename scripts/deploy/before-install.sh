#!/bin/bash

pwd

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd -P "$( dirname "$SOURCE" )" && pwd )"
sudo cp $DIR/../supervisord.conf /etc/supervisor/
sudo cp $DIR/../vyllage.conf /etc/supervisor/conf.d/
