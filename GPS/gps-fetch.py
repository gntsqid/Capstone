import serial

# Open the serial port where the GPS module is connected
ser = serial.Serial('/dev/ttyUSB0', baudrate=9600, timeout=1)

# Read until a valid RMC sentence is found
while True:
    line = ser.readline().decode('ascii', errors='replace')
    if line.startswith('$GNRMC'):
        fields = line.split(',')
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
