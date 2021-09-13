#!/bin/bash
echo "runing backup to $1"
var=$1

#PGPASSWORD="smallcraft" pg_dump -h 192.168.30.10 -U smallcraft -F c -d smallcraft -f /data/pgdump/smallcraft-$(date +'%Y%m%d_%H%M').dump
filename=smallcraft-$(date +'%Y%m%d_%H%M').dump
if [ -z ${1+x} ]; then
  var='/data/pgdump';
else
  echo "var is set to '$1'";
fi
path="$var/$filename"
echo $path

PGPASSWORD="smallcraft" pg_dump -h 192.168.30.10 -U smallcraft -F c -d smallcraft -f "$filename"