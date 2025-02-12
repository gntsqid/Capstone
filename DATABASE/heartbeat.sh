#!/bin/bash
#
# Author: Steven Lang
# Date: 02/12/2025
# Purpose: This program is designed to update the sensors by one of two means:
#          Either it pings a host to ensure it is alive and updates the "online" field
#          or it regularly sets offline to false if it has not received a heartbeat for X minutes
#

database="capstone"
table="machines"
user="loki"
password=$LOKI

machines=("oni" "kitsune")

for host in "${machines[@]}"; do
        echo $host
done

# Checking status of each machine
for host in "${machines[@]}"; do
    mariadb -u $user --password="$password" --database="$database" --execute="select * from $table where hostname='$host';"
done

query="select * from $table;"
mariadb -u $user --password="$password" --database="$database" --execute="$query"
