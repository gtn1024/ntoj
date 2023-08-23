#!/bin/sh

pm2 start sandbox -mount-conf /app/mount.yaml
pm2-runtime start pm2.json
