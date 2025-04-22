import sys
import socket
import board
import busio
import digitalio
import adafruit_rfm9x

# Encryption Key
KEY = b"<SECRET KEY>"

# XOR Encryption Function
def xor_encrypt_decrypt(data, key):
    return bytes([data[i] ^ key[i % len(key)] for i in range(len(data))])

# Get system hostname
hostname = socket.gethostname()

# Check if a message was provided as an argument
if len(sys.argv) < 2:
    print("No message provided. Usage: python3 lora-secure-transmit.py '<message>'")
    sys.exit(1)

# Get the message from the command-line argument
message = sys.argv[1]

# Format message to include hostname
full_message = f"{hostname}:{message}"
print(f"📡 Preparing to send: {full_message}")

# Initialize SPI (SCK, MOSI, MISO)
spi = busio.SPI(board.SCK, MOSI=board.MOSI, MISO=board.MISO)

# Use GPIO22 (Pin 15) for CS (NSS) and GPIO27 (Pin 13) for Reset
cs = digitalio.DigitalInOut(board.D22)  # GPIO22 (Pin 15)
reset = digitalio.DigitalInOut(board.D27)  # GPIO27 (Pin 13)

# Initialize the RFM9x module with custom settings
try:
    rfm9x = adafruit_rfm9x.RFM9x(spi, cs, reset, 915.17)  # Custom Frequency
    rfm9x.sync_word = <CHOSEN TWO BYTE HEX i.e. 0xaa>  # Custom Sync Word
    print("RFM9x successfully initialized!")

    # Encrypt the message
    encrypted_msg = xor_encrypt_decrypt(full_message.encode(), KEY)

    # Send the encrypted message
    rfm9x.send(encrypted_msg)
    print(f"📡 Sent encrypted message: {full_message}")

except RuntimeError as e:
    print(f"RFM9x initialization failed: {e}")

# TODO: add timestamp and other logic
# Fix power supply
