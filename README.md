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

---
**1/1/2025**\
I have created a postgresql database to host everything.\
This will be where the data gets piped to and has tables with columns like:
- parking space number
- available (bool)
- functional (bool)

The next step is to create a REST API to expose it.

I have also decided to create an Android app instead and am dropping PHP fully. 

---
**1/15/2025**\
I've begun work on the Android Application that will be used as part of the final product.\
Through quite a bit of troubleshooting, I have the mapbox API connected and am able to browse a static map.

I have also added a search feature that doesn't do anything but accept user input and will need to properly implement that soon.

Next steps are likely going to need to be getting a more dynamic map working such as enabling a simple search field (or perhaps storing it as a button such as "click here to go to school" and the map goes to there.\
Keep in mind I do not mean directions yet at this time with the route, only a search.

---
**4/7/2025**\
I have not been updating this as I should have, however a lot has happened in the past few weeks.\
For starters, Dr. Feister has approved the hardware design.\
It is good as is.

Next, it was decided that the app development was too costly in terms of time and skill and should be dropped in favor of web development.\
The web page is [capstone.sqid.ink](https://capstone.sqid.ink).\
The page is made with ASP.NET Razor pages after I followed several trainings on it.

Furthermore, the page (and the entire worflow) now gets and sends its data through an API.\
This API and the database are fully containerized docker compose files running as a proxy through my web server.\
This means we can fetch the data like so:
```Bash
curl -H "X-API-Key: $API_SECRET" https://api.capstone.sqid.ink/machines | jq
```

Finally, I am utilizing the web page to point parking space GPS locations to a mapbox service I am running on my subdomain [map.capstone.sqid.ink](https://map.capstone.sqid.ink/?lng=-119.0449353&lat=34.1608897

I simply now need to do the following:
- polish the front end
- decide if I want to incorporate map directions (though I need this costs money)
- ensure the radio is fully working
- do the poster
- ???
- graduate

