# GPS Module Setup for Raspberry Pi

This document details the setup and configuration required to use the Adafruit Ultimate GPS Module on a Raspberry Pi. This guide assumes a fresh installation of the Raspberry Pi OS (or a compatible Debian-based OS) and includes all the necessary steps to configure the system, install dependencies, and run a Python script to capture a single GPS data point.

## Hardware Requirements
- **Raspberry Pi Zero W** (or any Raspberry Pi model with a USB port)
- **Adafruit Ultimate GPS Module**
- **USB to Serial Adapter** (if needed, depending on your GPS module model)
- **Micro-USB to USB-C Adapter Cable** (for the Pi Zero W)
- **SD Card** (at least 8GB) with Raspberry Pi OS installed
- **Power Supply** (5V/2.5A recommended)

## Software Requirements
- **Operating System**: Raspberry Pi OS (32-bit or 64-bit) or a Debian-based distribution (e.g., Ubuntu, Kali)
- **Python 3**
- **Python Serial Library (`pyserial`)**

## Configuration Steps

### 1. Flash Raspberry Pi OS
1. Download the latest Raspberry Pi OS image from the [official Raspberry Pi website](https://www.raspberrypi.org/software/operating-systems/).
2. Use [Raspberry Pi Imager](https://www.raspberrypi.org/software/) or [Etcher](https://www.balena.io/etcher/) to flash the OS onto your SD card.
3. Insert the SD card into the Raspberry Pi and boot up.

### 2. Initial Setup
1. Boot up the Raspberry Pi and open a terminal.
2. Update the system packages:

   ```bash
   sudo apt update && sudo apt upgrade -y
   ```

### 3. Install Required Software
1. Install Python and the required serial library:

   ```bash
   sudo apt install python3 python3-serial -y
   ```

2. Verify the `pyserial` library is installed correctly:

   ```bash
   python3 -m serial.tools.list_ports
   ```

   If `pyserial` is not found, manually install it:

   ```bash
   sudo pip3 install pyserial
   ```

### 4. Hardware Setup
1. Connect the GPS module to the Raspberry Pi via the USB port.
2. Confirm that the GPS module is recognized by checking the output of the following command:

   ```bash
   dmesg | grep tty
   ```

   Look for an entry like:

   ```
   usb 1-1: cp210x converter now attached to ttyUSB0
   ```

   This indicates that the GPS module is connected as `/dev/ttyUSB0`.

### 5. Configure Serial Permissions
1. Add your user to the `dialout` group to access the serial port without using `sudo`:

   ```bash
   sudo usermod -aG dialout $USER
   ```

2. Log out and back in for the changes to take effect.

3. Check the permissions on `/dev/ttyUSB0`:

   ```bash
   ls -l /dev/ttyUSB0
   ```

   The output should show:

   ```
   crw-rw---- 1 root dialout 188, 0 Oct  9 22:24 /dev/ttyUSB0
   ```

### 6. Create the GPS Python Script Directory
1. Create a directory for your project and navigate to it:

   ```bash
   mkdir -p ~/Documents/GPS
   cd ~/Documents/GPS
   ```

2. Create an empty Python script file named `read-gps-once.py`:

   ```bash
   touch read-gps-once.py
   ```

3. Open the file for editing:

   ```bash
   nano read-gps-once.py
   ```

4. Paste the Python script that reads a single GPS data point (provided separately).

5. Save and exit (`Ctrl + X`, then `Y`).

### 7. Run the GPS Script
1. Make sure the GPS module is connected to the correct serial port (`/dev/ttyUSB0`).
2. Run the script:

   ```bash
   python3 ~/Documents/GPS/read-gps-once.py
   ```

3. The output should display the current latitude and longitude based on a single data point:

   ```
   Latitude: 34.22186833333333, Longitude: -119.05761
   ```

### 8. Optional: Automate Script Execution with a Button
1. Use a GPIO button and a separate script to trigger `read-gps-once.py` whenever the button is pressed.
2. For example, use a Python GPIO library (e.g., `RPi.GPIO`) to detect button presses and execute the GPS script.

## Troubleshooting Tips
- **GPS Module Not Detected**:
  - Ensure the module is connected properly and check the serial port (`/dev/ttyUSB0`) with:

    ```bash
    ls -l /dev/ttyUSB0
    ```

- **Permission Errors**:
  - Make sure the user is added to the `dialout` group:

    ```bash
    sudo usermod -aG dialout $USER
    ```

- **No GPS Data**:
  - Use a tool like `minicom` to verify that raw data is coming through:

    ```bash
    sudo minicom -b 9600 -D /dev/ttyUSB0
    ```

- **Debugging**:
  - Use `dmesg` and `journalctl` to check system logs for any errors:

    ```bash
    dmesg | grep tty
    sudo journalctl -xe | grep gps
    ```

## References
- [Adafruit GPS Modules Documentation](https://learn.adafruit.com/adafruit-ultimate-gps)
- [Raspberry Pi Serial Communication Guide](https://www.raspberrypi.org/documentation/computers/using-serial/)
- [Python Serial Library (`pyserial`)](https://pyserial.readthedocs.io/en/latest/)
