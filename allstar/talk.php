<?php
//  ------------------------------------------------------------
//  (c) 2023 by lagmrs.com all rights reserved
//  Permission granted to install and use with 
//  hubitat,gmrs,hamvoip.No modifications   
//  https://github.com/tmastersmart/pi-talk/tree/main/allstar
//  ------------------------------------------------------------
//
//  This file goes in /srv/http/supermon or in /srv/http.
//  Requires input-scan.php input protection script 
//
//  Accepts text messages from the hubitat hub or anything that wants to post.
//
//  get or post
// talk.php?talk=your message&code=GMRS
// talk.php?temp=80&code=GMRS   stores the temp in a temp text file for later processing
// talk.php?weather=string&code=GMRS   stores a weather string text file for later processing
//

$Version= "1.2";
//session_start([ 'name' => "talk" ]);

$password = "GMRS"; // If open to the internet you must set a pwd 

$root="/tmp";
$log    ="$root/talk.log";
$backup ="$root/talk-1.log";
$cmd1   ="$root/talk.txt"; 
$cmd2   ="$root/weather.txt";
$cmd3   ="$root/temperature.txt";

if(file_exists($cmd1))  { unlink ($cmd1);}

$logSize= 30000;// rotation. PI clears on reboot anyway
$talk="";$device="";$code="";$voice="";$play="";
$gpio="";$switch="";$button="";$ok="ERROR"; 
$header = true;$temp="";$weather=""; $msg="?";

include "input-scan.php";

for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {  $talk = $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device = $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {  $code = $fieldValues[$i]; }
if ($fieldNames[$i] == 'weather') {$weather= $fieldValues[$i]; }
if ($fieldNames[$i] == 'temp')    {   $temp= $fieldValues[$i]; }
if ($fieldNames[$i] == 'ver')    {    $ver = $fieldValues[$i]; }
}

if ($code <> $password){ $talk="";$temp="";$weather="";$ok="401 Unauthorized";$msg="wrong pwd";}
if (!$device){$device= "WEB";}
$datum = date('[Y-m-d H:i:s]'); 


if($talk){
$fileOUT = fopen($cmd1, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$talk") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$header= false; $ok="ok"; $msg="talk";
$status = "$datum : Save talk.txt $talk From Device:$device v$ver";
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
}

if($weather){
$fileOUT = fopen($cmd2, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$weather") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$header= false; $ok="ok";  $msg="weather";
$status = "$datum : Save weather.txt $weather From Device:$device v$ver";
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);

}
if($temp){
$fileOUT = fopen($cmd3, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$temp") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$header= false; $ok="ok";  $msg="temp";
$status = "$datum : Save temp  $temp From Device:$device v$ver";
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
}


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



if ($header){
    header("HTTP/1.1 404 Not Found");
    header("Status: 404 Not Found");
    $ok= "404 Not Found  $ok";
    $msg="ERROR";
    $status = "$datum : ERROR $ok  device$device v$ver";
    $fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
   }

$size= filesize($log); 
 if($size >= $logSize){
 if (file_exists($backup)) {unlink ($backup);}
 if (file_exists($backup)){$msg="$msg Error Del $backup";}
 rename ($log, $backup); if (file_exists($log)){$msg="$msg Error Renam $log";}
 if (!file_exists($log)){ $size=0;$msg="$msg - Log Rotated";}
}

$datum = date('[Y-m-d H:i:s]'); 

$status = "$datum : Message:$msg Device:$device Status:$ok";
print $status;

// Save the log
//$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
