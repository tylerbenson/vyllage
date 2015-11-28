#!/bin/bash -ex

/usr/bin/supervisorctl start vyllage

#Wait till the server opens the port
while ! nc -q 1 localhost 8080 </dev/null; do sleep 5; done
