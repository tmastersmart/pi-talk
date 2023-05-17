#!/bin/bash
# (c)2023 by winnfreenet.com
# This script reads incomming text messages from the web port.
# you must also install talk.php
#
# Allows hubitat to send tts messages to the node like it was alexa
# Will also work with anything that wants to send a message to the web port.
#
# Watches for the talk.txt file in a loop and converts it.
# tts_audio.sh must already be setup with you key.
# 
# this file goes in /etc/asterisk/local/talk-loop.sh
# and must start when system boots
#
# v1.0 
# 
MSG1=/tmp/talk.txt
MSG2=/tmp/talk.ul
 LOG=/tmp/talk1.log
 
while true
do
DATE=`date +"%Y-%m-%d %T"`
if [[  -f $MSG1 ]] ; then
 
 talk=$(cat $MSG1)
 talk=${talk//[$'\t\r\n']}
 
 echo "[$DATE] : Spoke Message "$talk" " >> $LOG
# echo "[$DATE] $talk"
 tts_audio.sh $MSG1 
 rm $MSG1
 asterisk -rx 'rpt localplay 2955 /tmp/talk'

 
fi

#  adjust this to change loop speed 10 sec
  sleep 10
done 
