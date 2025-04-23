#!/usr/bin/env python3

import time
import logging
import busio
import board
import digitalio
import adafruit_rfm9x
import socket
import os
import requests

# Load <db owner> (Base64 encoded Basic Auth token)
api_key = os.getenv("")
api_url_base = "https://api.capstone.sqid.ink/<table_name>"

# Encryption Key (must match sender)
KEY = b"<SECRET KEY>"

# XOR Encrypt/Decrypt
def xor_encrypt_decrypt(data, key):
    return bytes([data[i] ^ key[i % len(key)] for i in range(len(data))])

# Configure LoRa radio
CS = digitalio.DigitalInOut(board.CE1)
RESET = digitalio.DigitalInOut(board.D25)
spi = busio.SPI(board.SCK, MOSI=board.MOSI, MISO=board.MISO)
rfm9x = adafruit_rfm9x.RFM9x(spi, CS, RESET, 915.17)
rfm9x.sync_word = <chosen two byte hex i.e. 0xaa>  # Custom Sync Word
rfm9x.tx_power = 23

# Hostname of receiver
receiver_hostname = socket.gethostname()

# Log config
logging.basicConfig(
    filename='/home/ronin/Documents/LOGS/packets.log',
    level=logging.INFO,
    format='%(asctime)s - %(message)s'
)

print(f"[INFO] LoRa receiver active on {receiver_hostname}...")

# PATCH function to set availability
def patch_availability(hostname: str, available: bool):
    headers = {
        "Authorization": f"Basic {api_key}",
        "Content-Type": "application/json"
    }
    payload = {
        "parking_space_available": 1 if available else 0
    }

    try:
        r = requests.patch(f"{api_url_base}/{hostname}", json=payload, headers=headers)
        r.raise_for_status()
        print(f"[PATCH] {hostname}: parking_space_available = {payload['parking_space_available']}")
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] Failed to PATCH {hostname}: {e}")

# Receive loop
count = 0
while True:
    packet = rfm9x.receive()

    if packet is None:
        print("No message received...")
        count += 1
    else:
        try:
            decrypted_msg = xor_encrypt_decrypt(packet, KEY).decode("utf-8")
        except UnicodeDecodeError:
            print("[ERROR] Decryption failed")
            continue

        if ":" in decrypted_msg:
            sender_hostname, message = decrypted_msg.split(":", 1)
            sender_hostname = sender_hostname.strip()
            message = message.strip()
        else:
            sender_hostname = "Unknown"
            message = decrypted_msg

        print(f"[RECV] From {sender_hostname}: {message}")
        patch_availability(sender_hostname, False)  # Parking is now occupied
        logging.info(f"{sender_hostname}: {message}")
        count = 0

    # Heartbeat fail fallback (e.g., if kitsune hasn't sent msg in a while)
    if count >= 30:
        patch_availability("kitsune", True)  # Make parking space available
        count = 0

    time.sleep(1)
