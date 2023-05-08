#!/bin/bash
//  ------------------------------------------------------------
//  (c) 2023 by lagmrs.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//  https://github.com/tmastersmart/pi-talk/tree/main/allstar
//  ------------------------------------------------------------
// Remember to change your node number

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
 asterisk -rx 'rpt localplay 1995 /tmp/talk'

 
fi

#  adjust this to change loop speed 10 sec
  sleep 10
done 
