#!/bin/bash
#RUN as sudo.
add-apt-repository ppa:webupd8team/java
apt-get -y update
apt-get -y install ruby2.0
pip install awscli

apt-get -y install supervisor

cd /home/ubuntu
aws s3 cp s3://aws-codedeploy-us-west-2/latest/install . --region us-west-2
chmod +x ./install
./install auto

apt-get -y install oracle-java8-installer

echo "JAVA_HOME='/usr/lib/jvm/java-8-oracle/jre/'" >> /etc/environment
source /etc/environment
service supervisor start

sh -c 'echo deb http://apt.newrelic.com/debian/ newrelic non-free > /etc/apt/sources.list.d/newrelic.list'
wget -O- https://download.newrelic.com/548C16BF.gpg | apt-key add -
apt-get update
apt-get install newrelic-sysmond
nrsysmond-config --set license_key=8e5b87f1e9001ae664607d18625c81d5727eda0e
/etc/init.d/newrelic-sysmond start



apt-get update
apt-get dist-upgrade
