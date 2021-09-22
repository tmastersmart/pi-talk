<?php
//  ------------------------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//  https://github.com/tmastersmart/pi-talk
//   
//  Pi talk,Chime, Siren,media,button
//  v2.9 9-18-2021 Internal log Rotation 
//  v2.8 9-17-2021 
//  v2.7 9-16-2021 server.txt file added
//  v2.6 9-15-2021 
//  v2.5 9/13/2021 GPIO added
//  v2.4 9/11/2021 
//  v2.3 9/11/2021
//  v2.2 9/10/2021
//  v2.1 9/10/2021
//  v2.0 9/09/2021
//  
//  
//  
// ---------------------------------------------------
// you need to install these files on your pi 
// place php files in /var/www/html/
// 
// talk.php <-- this reveives commands from HUB
// temp.php <-- this file post to 
// temp-rotate.php <-- log rotation by chron
// input-scan.php <-- Safe loading of get and post
// talk.sh  <-- this runs in a loop to take action
// temp-chart.sh <-- Draws a png temp chart in /images 
//
// install-talk.sh <-- Installs extra programs you need
//
//https://github.com/tmastersmart/pi-talk
//----------------------------------------------------

$server ="/var/www/html/server.txt"; 
$log    ="/var/www/html/talk.log";
$backup= "/var/www/html/talk-1.log";
$cmd1   ="/var/www/html/talk1.txt"; if(file_exists($cmd1))  { unlink ($cmd1);}
$cmd2   ="/var/www/html/talk2.txt"; if(file_exists($cmd2))  { unlink ($cmd2);}
$cmd3   ="/var/www/html/chime.txt"; if(file_exists($cmd3))  { unlink ($cmd3);}
$logSize= 30000;


include "input-scan.php";
for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {  $talk= $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device= $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {  $code= $fieldValues[$i]; }
if ($fieldNames[$i] == 'voice')   { $voice= $fieldValues[$i]; } 
if ($fieldNames[$i] == 'lang')    {  $lang= $fieldValues[$i]; }                                   
if ($fieldNames[$i] == 'play')    {  $play= $fieldValues[$i]; }
if ($fieldNames[$i] == 'gpio')    {  $gpio= $fieldValues[$i]; }
if ($fieldNames[$i] == 'switch')  {$switch= $fieldValues[$i]; }
if ($fieldNames[$i] == 'button')  {$button= $fieldValues[$i]; }
}
if (!$lang) {$lang ="-ven-us";} // english us 'espeak --voices' for list
if (!$voice){$voice="+f4";}// f4 works better than F1


$return_var =""; $ok= false; $header = true;// set 404 error

if($talk){
 $fileOUT = fopen($cmd1, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$talk") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
 $fileOUT = fopen($cmd2, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$lang$voice") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
 $header= false;
}

if($play){
  if(file_exists("/home/pi/$play.wav"))  { $ok=true; $play="/home/pi/$play.wav";}
  if(file_exists("/home/pi/$play.mp3"))  { $ok=true; $play="/home/pi/$play.mp3";}
  if(file_exists("/home/pi/Music/$play.wav"))  { $ok=true; $play="/home/pi/Music/$play.wav";}
  if(file_exists("/home/pi/Music/$play.mp3"))  { $ok=true; $play="/home/pi/Music/$play.mp3";}
 $talk="(Play $play)";
  if($ok){
        $fileOUT = fopen($cmd3, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$play") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
        $header=false;
       }
}

if($gpio){
   $header=true;$ok=false; 
// at this time I dont have a safe list of GPIOs
// https://elinux.org/RPi_Low-level_peripherals

 if($gpio == "7") { $ok=true; }//
 if($gpio == "8") { $ok=true; }//
 if($gpio == "9") { $ok=true; }//
 if($gpio == "10"){ $ok=true; } // 
 if($gpio == "11"){ $ok=true; } //
 if($gpio == "14"){ $ok=true; } // 
 if($gpio == "15"){ $ok=true; } // 
 if($gpio == "17"){ $ok=true; }// 
 if($gpio == "18"){ $ok=true; }//
 if($gpio == "22"){ $ok=true; }// 
 if($gpio == "23"){ $ok=true; }//
 if($gpio == "24"){ $ok=true; }// 
 if($gpio == "25"){ $ok=true; }// 


 $talk="GPIO $gpio $switch"; // tell the log what we are doing
 //  1on 2off 3press
 if($ok){
     $send="gpio-g mode $gpio out";    exec($send, $output, $return_var );
      if ($switch==0){$send="gpio-g write $gpio 0";exec($send, $output, $return_var );}
      if ($switch==1){$send="gpio-g write $gpio 1";exec($send, $output, $return_var );} 
      if ($switch==2){ 
      $send="gpio-g write $gpio 1";exec($send, $output, $return_var ); 
      sleep(3);
      $send="gpio-g write $gpio 0";exec($send, $output, $return_var ); 
     }
    // $return_var returning 127 after post
    $header= false; $return_var = "Button = $button";
 }
}


if ($header){
    header("HTTP/1.1 404 Not Found");
    header("Status: 404 Not Found");
    $return_var= "404 Not Found  $return_var";
   }


// Log rotation does not like the permisions in WWW directory so doing it by php
$size= filesize($log); 
 if($size >= $logSize){
  if (file_exists($backup)) {unlink ($backup);}if (file_exists($backup)){$return_var="$return_var Error Del $backup";}
 rename ($log, $backup); if (file_exists($log)){$return_var="$return_var Error Renam $log";}
 if (!file_exists($log)){ $size=0;$return_var="$return_var Log Rotated";}
}

// Save the log
$datum = date('[Y-m-d H:i:s]'); 
$status = "$datum : Message:$talk From:$code $device status:$format $return_var Size:$size";
print $status;
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);

// save server info 
if(!file_exists($server))  { 
$send="gpio -v >>$server";exec($send, $output, $return_var ); 
$send="gpio readall >>$server";exec($send, $output, $return_var ); 
$send="gpio allreadall >>$server";exec($send, $output, $return_var ); 
}
 



