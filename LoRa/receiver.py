import time
import logging
import busio
import board
import digitalio
import adafruit_rfm9x
import socket

# Encryption Key
KEY = b"<SECRET KEY>"

# XOR Encryption/Decryption Function
def xor_encrypt_decrypt(data, key):
    return bytes([data[i] ^ key[i % len(key)] for i in range(len(data))])

# Configure LoRa Radio
CS = digitalio.DigitalInOut(board.CE1)  # Chip Select
RESET = digitalio.DigitalInOut(board.D25)  # Reset Pin
spi = busio.SPI(board.SCK, MOSI=board.MOSI, MISO=board.MISO)

# Initialize the RFM9x module with custom settings
rfm9x = adafruit_rfm9x.RFM9x(spi, CS, RESET, 915.17)  # Custom Frequency
rfm9x.sync_word = <chosen two byte hex i.e. 0xaa>  # Custom Sync Word
rfm9x.tx_power = 23  # Max power

# Get the receiver's hostname
receiver_hostname = socket.gethostname()

# Configure logging
logging.basicConfig(filename='/home/ronin/Documents/LOGS/packets.log', level=logging.INFO, format='%(asctime)s - %(message)s')

print(f"📡 LoRa Receiver on {receiver_hostname} is listening...\n")

while True:
    packet = rfm9x.receive()  # Check for incoming packet

    if packet is None:
        print("No message...")
    else:
        decrypted_msg = xor_encrypt_decrypt(packet, KEY).decode("utf-8")

        # Split message into hostname and actual message
        if ":" in decrypted_msg:
            sender_hostname, message = decrypted_msg.split(":", 1)  # Split only on the first `:`
            sender_hostname = sender_hostname.strip()
            message = message.strip()
        else:
            sender_hostname = "Unknown"
            message = decrypted_msg

        # Print formatted output
        print(f"Received message from {sender_hostname}: {message}")

        # Log the message
        logging.info(f"{sender_hostname}: {message}")

    time.sleep(1)  # Small delay before checking again

# TODO: ADD TIMESTAMP AND OTHER DATA
