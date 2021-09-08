#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free
# 
# Play the talk file v1.0
# 
# run with 'watch -n 10 talk.sh'   for looping every 10 sec
# launch on start up.
# 
# Add talk.log to file rotate
# webmin is recomended to set up log rotate and chron
# https://www.webmin.com/deb.html
# 
# For Hubitat users to process the wav file
# -------------------------------------------------------------

FILE=/home/pi/talk.wav
FLAG=/home/pi/talk-flag.txt
if [ -f "$FILE" ]; then
    killall omxplayer.bin  
    killall omxplayer
    sleep 4
    nohup /usr/bin/omxplayer --no-keys --no-osd -o local /home/pi/talk.wav <&- >&- 2>&- & disown
    sleep 10
    killall omxplayer.bin  
    killall omxplayer
    rm $FILE
     
fi







