<?php
//  ------------------------------------------------------------
//  (c) 2023 by lagmrs.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//  https://github.com/tmastersmart/pi-talk/tree/main/allstar
//  ------------------------------------------------------------
include("session.inc");

$root="/tmp";
$log    ="$root/talk.log";
$backup ="$root/talk-1.log";
$cmd1   ="$root/talk.txt"; 

$password = "GMRS"; // If open to the internet Must set this 

if(file_exists($cmd1))  { unlink ($cmd1);}

$logSize= 30000;
$talk="";$device="";$code="";$voice="";$play="";
$gpio="";$switch="";$button="";$ok=""; $ok= false; 
$header = true;

include "input-scan.php";
for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {  $talk= $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device= $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {  $code= $fieldValues[$i]; }
}

if ($code <> $password){ $talk="";$ok="401 Unauthorized";}


if($talk){
$fileOUT = fopen($cmd1, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$talk") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$header= false;
//chdir('/tmp/');
//$d= exec("sudo bash /srv/http/supermon/talk.sh $talk",$output,$return_var);
//$d= exec("sudo echo '$talk' >> /tmp/talk2.txt",$output,$return_var);
//$send = "sudo bash /srv/http/supermon/tts_audio.sh /tmp/talk.txt";
//$d= exec($send,$output,$ok);
//$d= exec("sudo asterisk -rx 'rpt localplay 2955 /tmp/talk'",$output,$return_var);
//print "$d $return_val";
// exec($send, $output, $return_var);
//$d = output = shell_exec("bash /srv/http/talk.sh '$talk'");
// $out_stat="$send $return_var";
// $send = "asterisk -rx 'rpt localplay 2955 /home/talk'";
// exec($send, $output, $return_var);
// $out_stat="$out_stat $send $return_var";
}


if ($header){
    header("HTTP/1.1 404 Not Found");
    header("Status: 404 Not Found");
    $ok= "404 Not Found  $ok";
   }

// Log rotation
$size= filesize($log); 
 if($size >= $logSize){
  if (file_exists($backup)) {unlink ($backup);}if (file_exists($backup)){$return_var="$return_var Error Del $backup";}
 rename ($log, $backup); if (file_exists($log)){$return_var="$return_var Error Renam $log";}
 if (!file_exists($log)){ $size=0;$return_var="$return_var Log Rotated";}
}

$datum = date('[Y-m-d H:i:s]'); 
$status = "$datum : Message:$talk Password:OK $device status: 
$ok";
print $status;

// Save the log
$fileOUT = fopen($log1, "a") ;
flock( $fileOUT, LOCK_EX );
fwrite ($fileOUT, "$status\n");
flock( $fileOUT, LOCK_UN );
fclose ($fileOUT);
