#!/bin/bash
# Must run as sudo.
echo '*** UPDATING ***'
add-apt-repository ppa:webupd8team/java
apt-get -y update
apt-get -y dist-upgrade

echo '*** Installing java8 ***'
apt-get -y install oracle-java8-installer
apt-get -y install oracle-java8-unlimited-jce-policy

echo '*** Installing ruby2 ***'
apt-get -y install ruby2.0

echo '*** Installing supervisor ***'
apt-get -y install supervisor

echo '*** Installing nginx ***'
apt-get -y install nginx

echo '*** Installing awscli ***'
apt-get -y install python-pip
pip install awscli

echo '*** Installing codedeploy ***'
cd /home/ubuntu
aws s3 cp s3://aws-codedeploy-us-west-2/latest/install . --region us-west-2
chmod +x ./install
./install auto

echo '*** Init environment ***'
echo "JAVA_HOME='/usr/lib/jvm/java-8-oracle/jre/'" >> /etc/environment
source /etc/environment
service supervisor start

echo '*** Installing NR server monitoring ***'
sh -c 'echo deb http://apt.newrelic.com/debian/ newrelic non-free > /etc/apt/sources.list.d/newrelic.list'
wget -O- https://download.newrelic.com/548C16BF.gpg | apt-key add -
apt-get -y install newrelic-sysmond
nrsysmond-config --set license_key=8e5b87f1e9001ae664607d18625c81d5727eda0e
/etc/init.d/newrelic-sysmond start

rm /etc/nginx/sites-enabled/default || true
echo "server {
       listen         80 default_server;
       return         301 https://www.vyllage.com$request_uri;
}
" > /etc/nginx/sites-enabled/vyllage
service nginx restart
