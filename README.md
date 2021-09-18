pi-talk 
PI hubitat driver controler

capability Notification,Chime,Alarm,button,switch GPIO
Reverse connections for temp volts model#
----------------------------------------------------------------------------------------
(c) 2021 by tmastersmart winnfreenet.com
Licensed for use on hubitat


I created this set of scripts because I could not find one online that would allow to control
my PI. I want it to talk play musit and I need my hub to monitor when it gets to hot or stops
responding. More options are planned. This started out as PI TALK but has changed into a 
PI controler. This is written mostly in PHP because I use it everyday on websites.  
=========================================================================================

you need to install these files on your pi 
talk.php <-- this driver talks to this file
temp.php <-- this file post back to this driver
input-scan.php <-- Safe loading of get and post
talk.sh  <-- this runs in a loop to take action
temp-chart.sh <-- Draws a png temp chart in /images 

install-talk.sh <-- Installs extra programs on pi

Install as follows

place php files in /var/www/html
and .sh files in /home/pi/

Talking is done by espeak See this 
https://behind-the-scenes.net/using-espeak-to-make-a-raspberry-pi-talk/

Enter this command from terminal
aplay /usr/share/sounds/alsa/*
You should hear WAV files played through your speaker if not your going to have to stop and find out why thats behound this guide. 

run the install 
sudo bash install.sh

You need PHP and a webserver installed. Im sure you know how to do this.
If you dont have a webserver I recomend installing Pi hole it will set everything up for you and install a dns server.
This is what I use because its much smaller than apachie. I no longer use apachie on PI

 
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
Go to the driver set it up.  Maker API is needed if you want to use the temp.php script
enter the following
url to your pi
the name of your server
url to check presence
url polling time
the voice you want to use
the language dialict you want. on the pi type 'espeak --voices' to get a list
if you want to use relays enter the GPIO values   0=disabe
Enter the sound file you want the siren to play


On the PI you need to setup chron and log rotation. I use webmin on all my PI'S 
The PI I run this on is in my garden connected to a PA system to scare crows and
Play outside alarms. Webadmin is what I use to ciontrol it. https://www.webmin.com/deb.html

Add log rotations for

/var/www/html/talk.log  <-daily or weakly
/var/www/html/temp.log  <-daily or weakly
/var/www/html/teml.dat  <-daily 

Add Chron for (its very easy on webmin)

Add chron to start talk.sh at bootup
Add Chron to for temp.php to run every say 15 mins
Add Chron for temp-chart.sh to run at the same time as above but 1 min later

php /var/html/www/temp.php > /dev/null 2>&1    (start at boot time)
bash /home/pi/takl.sh > /dev/null 2>&1         (every 10 or 15 mins)
bash /home/pi/temp-chart.php > /dev/null 2>&1  (run 1 min after the above runs) 

Temp chart will create a file at /var/html/www/images/temp.png
you can load this from http://127.0.0.1/images/temp.png
I have this on my hub desktop. 
If you want more charts contact me I draw temp charts for everything on my PI
 

You may modify espeak command the docs are here
http://espeak.sourceforge.net/commands.html


If you need sounds just about any sound you can find online will work.
see   https://soundbible.com/tags-dog-bark.html  For copyright reasions no mp3s included....


Any thoughts on which pins you need is welcome. More work will be done on this, 
As of last version Buttons do as follows
Button 1 turns on 1
Button 2 turns off 1
Button 3 turns on 2
Button 4 turns off 2
Button 5 Pushes 3 and a delay on the PI turns it back off


Planned: I need a way to notify my hub when events on the PI happen.
I also need to buy a relay board so I can work on the GPIO button options.
And I need to have seymour flags to interact with external scripts. 

if you use this please post a comment here. 
So far I have no ideal if anyone else is even intrested. Im basicaly building this for 
myself right now. Once this is working if no one is intrested updates will only be internal.

http://www.winnfreenet.com/wp/2021/09/pi-talk-hubitat-to-rasbery-pi-talking-script/
