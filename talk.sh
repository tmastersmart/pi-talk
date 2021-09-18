#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free
# 
# 
# v2.6 9/13/2021 now loops with 10sec delay run once.
# v2.5 
# v2.4 9/11/2021 moving files into webserver directory
# 
#  
# launch on start up.   set chron to launch on startup 
# loops every 10 sec and runns all the time
# nohup bash /home/pi/talk.sh > /dev/null 2>&1

#----------------------------------------------------------------
#// you need to install these files on your pi 
#// place .sh files in /home/pi/
#// 
#// talk.php <-- this reveives commands from HUB
#// temp.php <-- this file post to HUB
#// input-scan.php <-- Safe loading of get and post
#// talk.sh  <-- this runs in a loop to take action
#// temp-chart.sh <-- Draws a png temp chart in /images 
#//
#// install-talk.sh <-- Installs extra programs you need
#//
# https://github.com/tmastersmart/pi-talk
# -------------------------------------------------------------
PLAY=/var/www/html/chime.txt
MSG1=/var/www/html/talk1.txt
MSG2=/var/www/html/talk2.txt
 LOG=/var/www/html/talk.log
 
while true
do
 
 
 
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
#    sleep 10
#    killall omxplayer.bin  2> /dev/null
#    killall omxplayer 2> /dev/null
#    not doing above may result in omxplayer staying in memory
#    we will kill it on run
     rm $PLAY
 fi

#  adjust this to change loop speed 10 sec
  sleep 10
done 
