#!/bin/bash
#-------------------------------------------------------------
# (c) 2021 by tmastersmart winnfreenet.com all rights reserved
#  permission to use on hubiat for free

# Notice OS 11 has major problems unable to install 3 required programs. 

echo "the opt/vc directory must be copied over from a older install. if using OS 11"
#or see http://dev1galaxy.org/viewtopic.php?id=2967 to build it.


# see https://learn.sparkfun.com/tutorials/raspberry-gpio/all  to instakll GPIO
git clone https://github.com/WiringPi/WiringPi.git
cd WiringPi
git pull origin
./build
gpio -v


echo "you should hear talking"
aplay /usr/share/sounds/alsa/*
echo "now installing espeak omxplayer gnuplot libraspberrypi-bin"
sudo apt-get update
sudo apt-get install espeak

#  this no longer works in os 11 (sudo apt-get install omxplayer)
#  omxplayer will gicve not found. Dont know how to install in os 11
sudo apt-get install omxplayer

sudo apt-get install gnuplot
sudo apt-get install libraspberrypi-bin
sudo apt-get install python3-rpi-gpio
 
espeak "Your Install is finished" 2> /dev/null

