#!/usr/bin/env python3

import time
import board
import adafruit_hcsr04
import subprocess

sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D20, echo_pin=board.D21)

# Track time since last detection
last_seen_time = time.monotonic()
object_previously_detected = False

while True:
    try:
        distance = sonar.distance
        print(f"Distance: {distance:.2f} cm")

        current_time = time.monotonic()

        if distance < 150:
            print(f"Object detected within {distance:.2f} cm!")

            # Only run subprocess if object was NOT previously detected (i.e., reappeared after 15s)
            if not object_previously_detected:
                print("Sending via LoRa...")
                subprocess.run([
                    "python3", "/home/ronin/Documents/LoRa/lora-secure-transmit.py", f"{distance:.2f} cm"
                ], check=True)
                object_previously_detected = True

            # Update last seen time to now
            last_seen_time = current_time
        else:
            print("No object detected.")

            # If object was previously detected, check if 15 seconds have passed
            if object_previously_detected and (current_time - last_seen_time) >= 15:
                print("Object absent for 15 seconds. Ready to trigger again.")
                object_previously_detected = False

    except RuntimeError:
        print("Retrying sensor read...")

    time.sleep(0.5)
