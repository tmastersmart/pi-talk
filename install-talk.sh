#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free

echo "you should hear talking"
aplay /usr/share/sounds/alsa/*
echo "now installing espeak omxplayer"
sudo apt-get update
sudo apt-get install espeak
sudo apt-get install omxplayer
sudo apt-get install gnuplot
espeak "Your Install is finished" 2> /dev/null

