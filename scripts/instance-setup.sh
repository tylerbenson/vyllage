#!/bin/bash
#RUN as sudo.
add-apt-repository ppa:webupd8team/java
apt-get -y update
apt-get -y install awscli
apt-get -y install ruby2.0

apt-get -y install supervisor

cd /home/ubuntu
aws s3 cp s3://aws-codedeploy-us-west-2/latest/install . --region us-west-2
chmod +x ./install
./install auto

apt-get -y install oracle-java8-installer

echo "JAVA_HOME='/usr/lib/jvm/java-8-oracle/jre/'" >> /etc/environment
source /etc/environment
service supervisor start
