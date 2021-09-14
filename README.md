# pi-talk
Hubitat driver to connect to rasbery pi and talk 

capability Notification,Chime,Alarm,button,switch GPIO
----------------------------------------------------------------------------------------
(c) 2021 by tmastersmart winnfreenet.com
Licensed for use on hubitat


I created this set of scripts because I could not find one online that would allow
me to make my PI thats outside on a PA speaker speak from commands sent by hubitat.
This allows your pi to speak offline. 
=========================================================================================


v2 install 
Install as follows

place php files in /var/www/html
and .sh files in /home/pi/

run this to test
aplay /usr/share/sounds/alsa/*
if ok do next. If not see manual install https://behind-the-scenes.net/using-espeak-to-make-a-raspberry-pi-talk/


run the install 
sudo bash install.sh

If you dont have a webserver I recomend installing Pi hole it will set everything up for you and install a dns server.

launch this script at startup

nohup bash /home/pi/talk.sh > /dev/null 2>&1 

It stays running and loops every 10secs



http://0.0.0.0/talk.php?talk=test should say test

http://0.0.0.0/talk.php?play=1 should play 1.mp3 or 1.wav

http://0.0.0.0/talk.php?play=bell should play bell.mp3 or bell.wav

it will search /home/pi/Music and /home/pi for the file.

Web server needs permission to read from both directories or will
exit with 404 file not found

Load your sound files in one of those directories

you can play a unlimited # of chimes named 1 2 3 .wav or .mp3
Or using the music play command you can play a file by name do not include
the ext  http://0.0.0.0/talk.php?play=bell will play bell.mp3 


Once this is all working import the driver in hubitat.
https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

save it and add a virtual device using this driver.
Go to the driver and enter the url to your pi the name of your server and the voice you want to use
on the pi type 'espeak --voices' to get a list


You also need to add a log rotation for /var/www/html/talk.log

Webmin is recomended with it you can set up log rotation and start up options using a web interface. 
https://www.webmin.com/deb.html

You may modify espeak command the docs are here
http://espeak.sourceforge.net/commands.html


if you use this please post a comment here
http://www.winnfreenet.com/wp/2021/09/pi-talk-hubitat-to-rasbery-pi-talking-script/



If you need sounds just about any sound you can find online will work.
see   https://soundbible.com/tags-dog-bark.html  

Update:
Now supports changing GPIO states to switch relays. Caution be sure you know what
your doing. if you dont want to use keep GPIO set to 0 to disable.

Valid settings for switch or button 1 are 1,7,8,9,10,11,14,15,16,18,22,23,24,25

Any thoughts on which pins you need is welcome. More work will be done on this, 
Switch turns on or off 1 GPIO, Button Turns on a GPIO waits and then turns it off.



For copyright reasions do mp3s included....


