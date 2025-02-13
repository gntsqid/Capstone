#!/usr/bin/env python3

import time
import board
import adafruit_hcsr04
import subprocess

sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D20, echo_pin=board.D21)

while True:
    try:
        distance = sonar.distance
        print(f"Distance: {distance:.2f} cm")

        # Check if object is within 150 cm
        if distance < 150:
            print(f"Object detected within {distance:.2f} cm! Sending via LoRa...")

            # Run LoRa transmission script with distance as argument
            subprocess.run([
                "python3", "/home/ronin/Documents/LoRa/lora-secure-transmit.py", f"{distance:.2f} cm"
            ], check=True)
        else:
            print("No object detected.")

    except RuntimeError:
        print("Retrying sensor read...")

    time.sleep(1.5)
