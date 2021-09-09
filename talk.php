<?php
//  ------------------------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//
//   
//  notice to talk on PI
//  v2 9/09/2021
//  
//  ------------------------------------------------------------

include "input-scan.php";
for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {$talk= $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device= $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {$code= $fieldValues[$i]; }
if ($fieldNames[$i] == 'voice')   {$voice= $fieldValues[$i];} 
if ($fieldNames[$i] == 'lang')    {$lang= $fieldValues[$i]; }                                   
if ($fieldNames[$i] == 'flag')    {$flag= $fieldValues[$i]; }
}
if (!$lang) {$lang ="-ven-us";} // english us 'espeak --voices' for list
if (!$voice){$voice="+f1";}
if (!$talk) {$talk ="error (no post or get)";}

$log ="/home/pi/talk.log"; 
$cmd1="/home/pi/talk1.txt"; if(file_exists($cmd1))  { unlink ($cmd1);}
$cmd2="/home/pi/talk2.txt"; if(file_exists($cmd2))  { unlink ($cmd2);}

// v1 wav file
//$file="/home/pi/talk.wav";  if(file_exists($file))  { unlink ($file);} 
//$save="$lang$voice '$talk'";
//$send="espeak $lang$voice -w $file '$talk'";
//exec($send, $output, $return_var ); 

$datum = date('[Y-m-d H:i:s]'); 
$status = "$datum : Message:$talk From:$code $device status:$format $return_var";
print $status;

# build the v2 text command files
$fileOUT = fopen($cmd1, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "'$talk'") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$fileOUT = fopen($cmd2, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$lang$voice") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);


// save the log
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);


