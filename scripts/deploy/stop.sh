#!/bin/bash -ex

# If this instance is in the ELB and healty:
# Increase auto scale count
# (Wait for new instance to be running)
# Query ELB API to verify a new instance is running before continuing

/usr/bin/supervisorctl stop vyllage
