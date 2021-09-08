# pi-talk
Hubitat to PI talking


Install as follows

run this to test your spearks working
aplay /usr/share/sounds/alsa/*
if ok do next if not you need to fix your speakers first

sudo apt-get install espeak

espeak "Text you wish to hear back" 2>/dev/null
once this is working


place talk.php in your webserver directory. 
/var/www/html/

If you dont have a webserver I recomend installing Pi hole it will set everything up for you and install a dns server. 

place talk.sh uin your home directory
/home/pi/

http://0.0.0.0/talk.php?talk=test should now create a .wav file and if you run
bash talk.sh   it should speak those works out the speaker.

Once this is all working import the driver in hubitat.







