#!/bin/bash
#  ------------------------------------------------------------
#  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
#  Permission granted to install and use with hubitat for free   
#  https://github.com/tmastersmart/pi-talk
#   
#   Creats a png image of your temp data at 
#  /var/www/html/images/temp.png
#  http://127.0.0.1/images/temp.png
# 
#  v1.1 09-18-2021 Log file moved 
#  v1.0 09-17-2021 First version Manual setup
#
#  needs sudo apt-get -y install gnuplot
#   
#     place .sh files in /home/pi
#     
#     We read from /var/log/hub-temp.dat  with root permissions
#     This file must be log rotated daily for proper charts
#----------------------------------------------------------------
#// you need to install these files on your pi 
#// place php files in /var/www/html/
#// 
#// talk.php <-- this reveives commands from HUB
#// temp.php <-- this file post to HUB
#// input-scan.php <-- Safe loading of get and post
#// talk.sh  <-- this runs in a loop to take action
#// temp-chart.sh <-- Draws a png temp chart in /images 
#//
#// install-talk.sh <-- Installs extra programs you need
#//
#//https://github.com/tmastersmart/pi-talk
#//----------------------------------------------------

thetime=`date +%Y-%m-%d` #
gnuplot <<- EOF
set terminal png size 300,200 enhanced font ",5"
set output '/var/www/html/images/temp.png'

set key off
set title "Temperature Log"

set xdata time
set timefmt "%H:%M" 

set format x "%H:%M"
set xrange [ "00:00":"23:59" ]
set yrange [ 20.0 : 80.0 ]
set ylabel "Temp in \°C"
set xlabel "Time"

set grid

plot '/var/log/hub-temp.dat' using 1:2 with line
EOF
