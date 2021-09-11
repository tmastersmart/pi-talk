# pi-talk
Hubitat driver to connect to rasbery pi and talk capability Notification,Chime,Alarm
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
if ok do next if not you need to fix your speakers first

run the install 
sudo bash install.sh

If you dont have a webserver I recomend installing Pi hole it will set everything up for you and install a dns server.


http://0.0.0.0/talk.php?talk=test should set files to talk

bash talk2.sh   should speak those works out the speaker.

http://0.0.0.0/talk.php?play=1 should set 1.mp3 or 1.wav  to be played
http://0.0.0.0/talk.php?play=bell should set bell.mp3 or bell.wav  to be played
it will seatch /home/pi/Music and /home/pi for the file.

Load your sound files in one of thiose directories

bash talk.sh   will play the file

you can play a unlimited # of chimes named 1 2 3 .wav or .mp3
Or using the music play command you can play a file by name do not include
the ext  http://0.0.0.0/talk.php?play=bell will play bell.mp3 


Once this is all working import the driver in hubitat.
https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

save it and add a virtual device using this driver.
Go to the driver and enter the url to your pi the name of your server and the voice you want to use
on the pi type 'espeak --voices' to get a list

send a message using the option in the driver then run
bash talk.sh  and it should speak

select PLAY 1 and it should set 1.mp3 to play
bash talk.sh should play 1.mp3


Create a chron to run every min and load bash talk.sh

or open a terminal and type 'watch -n 10 /home/pi/talk.sh'


You also need to add a log rotation for /var/www/html/talk.log

Webmin is recomended with it you can set up log rotation and start up options using a web interface. 
https://www.webmin.com/deb.html

The webserver needs permission to read from /home/pi/Music and /home/pi
if it cant do this and you see no log go to webmin access user www-data
and add it to group root. 


You may modify espeak command the docs are here
http://espeak.sourceforge.net/commands.html


if you use this please post a comment here
http://www.winnfreenet.com/wp/2021/09/pi-talk-hubitat-to-rasbery-pi-talking-script/



If you need sounds just about any sound you can find online will work.
see   https://soundbible.com/tags-dog-bark.html  

For copyright reasions I have to find PD files to include a sample


