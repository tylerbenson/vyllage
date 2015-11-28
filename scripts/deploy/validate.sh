#!/bin/bash -ex

result=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/status-local)

if [[ "$result" = "200" ]]; then
    exit 0
else
    exit 1
fi
