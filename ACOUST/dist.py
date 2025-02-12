#!/usr/bin/env python3

import time
import board
import adafruit_hcsr04
import subprocess
import os  # Required to access environment variables

sonar = adafruit_hcsr04.HCSR04(trigger_pin=board.D5, echo_pin=board.D6)

while True:
    try:
        distance = sonar.distance
        print(f"Distance: {distance:.2f} cm")

        # Check if object is within 150 cm
        if distance < 150:
            print("Object detected within 150 cm! Running MariaDB query...")

            # Retrieve the SHOGUN password from environment variables
            shogun_password = os.getenv("SHOGUN")
            # Execute MariaDB command
            subprocess.run([
                "mariadb",
                "-h", "sqid.ink",
                "-u", "shogun",
                f"--password={shogun_password}",
                "capstone",
                "--execute=SELECT * FROM machines;"
            ], check=True)
        else:
            print("Nothing there")

    except RuntimeError:
        print("Retrying!")

    time.sleep(10)
