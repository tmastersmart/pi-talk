All Star TTS from hubitat.

Adds the allstar node as a speech device in hubitat.  Will repeat anything sent to is as voice.
Needs tts_audio.sh set up and working follow instructions at.
https://www.hamvoip.org/howto/tts-how-to.pdf

php files are installed in the /srv/http/supermon directory
nano /srv/http/supermon/talk.php
Copy the php script and save
IF this server will be exposed to the internet you need to change the password
The password must match the password in the hubitat driver

nano /srv/http/supermon/input-scan.php
Copy the php script and save

.sh files installed in /etc/asterisk/local directory
nano /etc/asterisk/local/talk-loop.sh
copy the .sh file ans save
Remember to add your node number where it has 1195

talk-loop.sh is started at boot time by adding it to 
/etc/rc.local
nano /etc/rc.local
add this above exit
bash /etc/asterisk/local/talk-loop.sh

logs are stored in /tml/talk.log and are auto rotated by the php script

====================================================================================
On the pi import the driver and exit your PIS url.

----------------------------------------------------------------
PI must be set to static IP or use the routers reserve IP setup.
You need to know your IP DNS server and router IP
cd /etc/systemd/network/
nano /etc/systemd/network/eth0.network
nano /etc/systemd/network/wlan0.network



