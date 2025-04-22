#!/bin/bash

# Export environment variables from the file
set -a
source /etc/capstone.env
set +a

# DEBUG: Show what got loaded
#echo "[DEBUG] CAPSTONE_API_SECRET is: $CAPSTONE_API_SECRET" >&2

# Move to the project directory
cd /home/gwyn/CAPSTONE/CapstonePage

# Start the app
exec /usr/share/dotnet/dotnet run

