#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free
# 
# Play the talk file v1.1
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
 LOG=/home/pi/talk.log
DATE=`date +"%Y-%m-%d %T"` 
if [ -f "$FILE" ]; then
    killall omxplayer.bin  2> /dev/null
    killall omxplayer 2> /dev/null
    sleep 4
    nohup /usr/bin/omxplayer --no-keys --no-osd -o local /home/pi/talk.wav <&- >&- 2>&- & disown
    echo "[$DATE] : Spoke Message $lang "$talk" " >> $LOG
    sleep 10
    killall omxplayer.bin  2> /dev/null
    killall omxplayer 2> /dev/null
    rm $FILE
     
fi






