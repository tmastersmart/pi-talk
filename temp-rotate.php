<?php
//  ------------------------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//  https://github.com/tmastersmart/pi-talk
//   
//  Internal log rotation script 
//  
//  
//  v1.0 
//   
// ---------------------------------------------------
// you need to install these files on your pi 
// place php files in /var/www/html/
// 
// talk.php <-- this reveives commands from HUB
// temp.php <-- this file post to HUB
// temp-rotate.php <--- run daily by chron
// input-scan.php <-- Safe loading of get and post
// talk.sh  <-- this runs in a loop to take action
// temp-chart.sh <-- Draws a png temp chart in /images 
//
// install-talk.sh <-- Installs extra programs you need
//
//https://github.com/tmastersmart/pi-talk
//----------------------------------------------------
//  
// ------------------------------------------------------------
// This script is to be run from chron as
// php /var/www/html/temp-rotate.php 

// =====================settings================================



   $log ="/var/www/html/hub-temp.log";$logSize=20000; 
$tempLog="/var/www/html/hub-temp.dat";unlink ($tempLog);
$store  ="/var/www/html/data.txt";    unlink ($store);
$setonce="/var/www/html/set.dat";     unlink ($setonce);
$backup= "/var/www/html/hub-temp-1.log";
// Log rotation 
$size= filesize($log); 
 if($size >= $logSize){
  if (file_exists($backup)) {unlink ($backup);}if (file_exists($backup)){$return_var="$return_var Error Del $backup";}
 rename ($log, $backup); if (file_exists($log)){$return_var="$return_var Error Renam $log";}
 if (!file_exists($log)){ $size=0;$return_var="$return_var Log Rotated";}
}




