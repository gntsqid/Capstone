#!/usr/bin/env python3
import serial

# Open the serial port where the GPS module is connected
try:
    ser = serial.Serial('/dev/ttyUSB0', baudrate=9600, timeout=1)
except serial.SerialException as e:
    print(f"Error opening serial port: {e}")
    exit(1)

# Read until a valid RMC sentence with a fix is found
while True:
    try:
        line = ser.readline().decode('ascii', errors='replace').strip()
        if line.startswith('$GNRMC'):
            fields = line.split(',')

            # Check for GPS fix status (field[2] should be 'A')
            if fields[2] != 'A':
                print(f"No valid GPS fix yet. Sentence: {fields}")
                continue

            # Ensure required fields are not empty
            if len(fields) < 7 or not fields[3] or not fields[5]:
                print(f"Incomplete or invalid data in sentence: {fields}")
                continue

            lat = fields[3]
            lat_dir = fields[4]
            lon = fields[5]
            lon_dir = fields[6]

            # Convert latitude and longitude to decimal format
            lat_deg = float(lat[:2])
            lat_min = float(lat[2:])
            lat_decimal = lat_deg + (lat_min / 60.0)
            if lat_dir == 'S':
                lat_decimal = -lat_decimal

            lon_deg = float(lon[:3])
            lon_min = float(lon[3:])
            lon_decimal = lon_deg + (lon_min / 60.0)
            if lon_dir == 'W':
                lon_decimal = -lon_decimal

            print(f"Latitude: {lat_decimal}, Longitude: {lon_decimal}")
            break
    except Exception as e:
        print(f"Error processing line: {line if 'line' in locals() else '<unreadable>'}, Error: {e}")
