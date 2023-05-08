#!/bin/bash

MSG1=/tmp/talk.txt
MSG2=/tmp/talk.ul
 LOG=/tmp/talk.log
 
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
