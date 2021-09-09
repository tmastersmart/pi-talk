# pi-talk
Hubitat to PI talking 
----------------------------------------------------------------------------------------
(c) 2021 by tmastersmart winnfreenet.com
Licensed for use on hubitat


I created this set of scripts because I could not find one online that would allow
me to make my PI thats outside on a PA speaker speak from commands sent by hubitat.
This allows your pi to speak offline. 
=========================================================================================


Install as follows

run this to test
aplay /usr/share/sounds/alsa/*
if ok do next if not you need to fix your speakers first

sudo apt update
sudo apt-get install espeak

espeak "this is a test of pi talk" 
once this is working


place .php scripts in your webserver directory. 
/var/www/html/

If you dont have a webserver I recomend installing Pi hole it will set everything up for you and install a dns server. 

place talk.sh in your home directory
/home/pi/

http://0.0.0.0/talk.php?talk=test should now create a .wav file and if you run
bash talk.sh   it should speak those works out the speaker.

Once this is all working import the driver in hubitat.
https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

save it and add a virtual device using this driver.
Go to the driver and enter the url to your pi the name of your server and the voice you want to use
on the pi type 'espeak --voices' to get a list

send a message using the option in the driver then run
bash talk.sh  and it should speak


Create a chron to run every min and load bash talk.sh

or open a terminal and type 'watch -n 10 /home/pi/talk.sh'



You also need to add a log rotation for /home/pi/talk.log

Webmin is recomended with it you can set up log rotation and start up options using a web interface. 
https://www.webmin.com/deb.html

You may modify espeak command the docs are here
http://espeak.sourceforge.net/commands.html


if you use this please post a comment here
http://www.winnfreenet.com/wp/2021/09/pi-talk-hubitat-to-rasbery-pi-talking-script/


Notes: v1 using talk.sh plays the wav file using oxplayer install using
sudo apt-get install omxplaye

v2 using talk2.sh plays the message using espeak from the text file.
use the version that works best for you....


