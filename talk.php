<?php
//  ------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//
//   
//  notice to talk on PI
//  v1.0
//  This converts the notice to a wav file 
//
//  
// notes  Install espeak
// see https://www.dexterindustries.com/howto/make-your-raspberry-pi-speak/
//
// aplay /usr/share/sounds/alsa/*
// sudo apt-get install espeak
// espeak "Text you wish to hear back" 2>/dev/null



include "input-scan.php";
for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {$talk= $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device= $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {$code= $fieldValues[$i]; }
if ($fieldNames[$i] == 'flag')    {$flag= $fieldValues[$i]; }
}

if (!$talk){$talk="error (no post or get)";}

$log="/home/pi/talk.log";           
$file="/home/pi/talk.wav"; if(file_exists($file)) { unlink ($file);}
// set a flag for bash script 
if ($flag){ 
   $flagFile="/home/pi/talk-flag.txt";
   $fileOUT = fopen($flagFile, "w") ;  
   $fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "Set\n ");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
   }          

$send="espeak -w $file $talk";
//session_write_close();
exec($send, $output, $return_var ); 
//session_start();
$datum = gmdate('[Y-m-d H:i:s]'); // utc_datum
$status = "$datum : Message:$talk From:$code $device status:$format $return_var";
print $status;


$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n ");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);


