#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free
# 
# Play the talk file v2.0
# 
# run with 'watch -n 10 talk.sh'   for looping every 10 sec
# launch on start up.
# 
# Add talk.log to file rotate
# webmin is recomended to set up log rotate and chron
# https://www.webmin.com/deb.html
# 
# For Hubitat users 
# -------------------------------------------------------------

#FILE=/home/pi/talk.wav
MSG1=/home/pi/talk1.txt
MSG2=/home/pi/talk2.txt
 LOG=/home/pi/talk.log
DATE=`date +"%Y-%m-%d %T"`
if [[ ! -f $MSG1 ]] ; then
    exit
fi

 talk=$(cat $MSG1)
 lang=$(cat $MSG2)
 
 talk=${talk//[$'\t\r\n']}
 lang=${lang//[$'\t\r\n']}
 
 #echo "espeak $lang "$talk" "
 echo "[$DATE] : Spoke Message $lang "$talk" " >> $LOG
 espeak $lang "$talk" 2> /dev/null
 rm $MSG1
 rm $MSG2
