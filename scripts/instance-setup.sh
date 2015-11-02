#!/bin/bash -ex
# Must run as sudo.

echo '*** UPDATING SYSTEM ***'
add-apt-repository -y ppa:webupd8team/java
sh -c 'echo deb http://apt.newrelic.com/debian/ newrelic non-free > /etc/apt/sources.list.d/newrelic.list'
wget -O- https://download.newrelic.com/548C16BF.gpg | apt-key add -
apt-get -y update
# apt-get -y dist-upgrade

echo '*** Installing java8 ***'
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
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

echo '*** Init environment ***'
echo "JAVA_HOME='/usr/lib/jvm/java-8-oracle/jre/'" >> /etc/environment
source /etc/environment
service supervisor start || true

echo '*** Installing NR server monitoring ***'
apt-get -y install newrelic-sysmond
nrsysmond-config --set license_key=8e5b87f1e9001ae664607d18625c81d5727eda0e
/etc/init.d/newrelic-sysmond start

echo '*** Installing NGINX Redirector ***'
rm /etc/nginx/sites-enabled/default || true
echo "server {
       listen         80 default_server;
       return         301 https://www.vyllage.com$request_uri;
}
" > /etc/nginx/sites-enabled/vyllage
service nginx restart

echo '*** Installing codedeploy ***'
cd /home/ubuntu
aws s3 cp s3://aws-codedeploy-us-west-2/latest/install . --region us-west-2
chmod +x ./install
./install auto

echo '*** Done with Instance Setup ***'
