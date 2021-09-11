#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free
# 
# v2.4 9/11/2021 moving files into webserver directory
# 
# Play the talk file v2.0
# 
# run with 'watch -n 10 talk2.sh'   for looping every 10 sec
# launch on start up.

# 
# place file in /home/pi/
# place audio files in /home/pi/
# place audio files in /home/pi/Music/ 
# 
# For Hubitat users 
# -------------------------------------------------------------

PLAY=/var/www/html/chime.txt
MSG1=/var/www/html/talk1.txt
MSG2=/var/www/html/talk2.txt
 LOG=/var/www/html/talk.log
DATE=`date +"%Y-%m-%d %T"`
if [[  -f $MSG1 ]] ; then
    
 talk=$(cat $MSG1)
 lang=$(cat $MSG2)
 
 talk=${talk//[$'\t\r\n']}
 lang=${lang//[$'\t\r\n']}
 
 #echo "espeak $lang "$talk" "
 echo "[$DATE] : Spoke Message $lang "$talk" " >> $LOG
 espeak $lang "$talk" 2> /dev/null
 rm $MSG1
 rm $MSG2
fi

#chime module 
if [[  -f $PLAY ]] ; then

 chime=$(cat $PLAY)
 chime=${chime//[$'\t\r\n']}
#    echo "playing chime $chime"
    killall omxplayer.bin  2> /dev/null
    killall omxplayer 2> /dev/null
    sleep 2
    nohup /usr/bin/omxplayer --no-keys --no-osd -o local $chime <&- >&- 2>&- & disown
    echo "[$DATE] : Play file $chime" >> $LOG
    sleep 10
    killall omxplayer.bin  2> /dev/null
    killall omxplayer 2> /dev/null
    rm $PLAY
 fi

