# Capstone
My capstone project for CSUCI Spring 2025.

## Dev-Notes
**10/09/2024**\
I have not looked into this project for some time.\
I have unfortunately forgotten where I left off and will now journal what my progress.\
Dr. Feister has advised me that the scope for having a rescue worker tool is too high and that I need to either limit it or truly reach out to rescue workers and see if it is remotely useful.
> From now on, I will dedicate myself to the "fire alarm" w/ "mesh warning tiers" collected in a "heatmap timeline"

I will need to look into whether or not the pi zeroes are GPS module ready or not.\
I do not remember if I ever implemented the module into one.\
I believe I only used dummy data...time to try the real thing out, get GPS logged, then send a real GPS point to the other pi.

---
**10/10/2024**\
After a few installs, I managed to test for the GPS module connected via USB.\
```Bash
dmesg | grep tty      # I should see 'cp210x converter is now attached to ttyUSBx'
lsusb | grep -i cp210 # verifies the above
ls -l /dev/ttyUSBx    # further verifies the connected and mount
sudo cat /dev/ttyUSBx # reads live the signals from the board
```
example board read:
```Bash
$GNGGA,134748.000,3413.3085,N,11903.4560,W,1,05,2.50,55.5,M,-33.7,M,,*76

$GPGSA,A,3,10,15,23,,,,,,,,,,2.67,2.50,0.95*0E

$GLGSA,A,3,72,87,,,,,,,,,,,2.67,2.50,0.95*1C

$GPGSV,4,1,13,23,71,346,28,18,56,066,,10,49,262,30,27,32,313,22*74

$GPGSV,4,2,13,15,31,050,26,16,21,268,21,29,20,152,16,32,18,192,*7B

$GPGSV,4,3,13,26,12,237,,24,10,105,,13,05,035,,08,02,323,*77

$GPGSV,4,4,13,43,,,*7C

$GLGSV,2,1,08,72,82,170,29,87,57,359,30,86,49,095,18,71,42,033,*6B

$GLGSV,2,2,08,65,23,207,20,88,09,319,,85,03,124,,78,01,304,*6A

$GNRMC,134748.000,A,3413.3085,N,11903.4560,W,0.84,295.70,101024,,,A*6A

$GNVTG,295.70,T,,M,0.84,N,1.55,K,A*27

$GNGGA,134748.000,3413.3085,N,11903.4560,W,1,05,2.50,55.5,M,-33.7,M,,*76
$GPGSA,A,3,10,15,23,,,,,,,,,,2.67,2.50,0.95*0E
```
No entirely sure about this, but we can also use minicom to connect to serial port:
```Bash
# minicom is a serial port reading tool
# '-b 9600' is baud rate for most GPS units
# '-D /dev/ttyUSBx' designates which device we are conencting to
sudo minicom -b 9600 -D /dev/ttyUSB0
```
> Got it to work with Python!
>> See the gps-get.py above

---
**10/11/2024**\
I have learned that what I am doing is defined as (in some ways) [Predictive Policing](https://en.wikipedia.org/wiki/Predictive_policing).\
This in general involves huge analytical models to determine victims, perpetrators, etc., while I am only doing it as a commercially avaialable product for general safety. 

I have decided to architect the maps in the following way:
  - If no specific address is given, i.e. Oxnard,CA then add it to the boundary box/total count to avoid muddying heatmaps
  - On the note of city level boxes, make that the default and add a toggle button to make all cities there own heatmaps to switch back and forth.
  - Heatmaps will be gradients, blending in (for example 10) colors as a range given a percentage (such as 0 - 100%)
  - I am collecting GeoData from Sensors and may want that to be separate from harvested data (REVISIT) 
  - If harvestable data is abvailable, *make sure its reputable, legal, and redacts PII*. Potentially look into contracts with the Police Department for data or find online sources.


