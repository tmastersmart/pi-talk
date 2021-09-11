<?php
//  ------------------------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//
//   
//  Pi talk,Chime, Siren,media
//  
//  v2.4 9/11/2021 
//  v2.3 9/11/2021
//  v2.2 9/10/2021
//  v2.1 9/10/2021
//  v2.0 9/09/2021
//  
//  place php files in /var/www/html/
//  ------------------------------------------------------------

include "input-scan.php";
for ($i=0; $i < sizeof($fieldNames); $i++) {
if ($fieldNames[$i] == 'talk')    {$talk= $fieldValues[$i]; }
if ($fieldNames[$i] == 'device')  {$device= $fieldValues[$i]; }
if ($fieldNames[$i] == 'code')    {$code= $fieldValues[$i]; }
if ($fieldNames[$i] == 'voice')   {$voice= $fieldValues[$i];} 
if ($fieldNames[$i] == 'lang')    {$lang= $fieldValues[$i]; }                                   
if ($fieldNames[$i] == 'play')    {$play= $fieldValues[$i]; }
}
if (!$lang) {$lang ="-ven-us";} // english us 'espeak --voices' for list
if (!$voice){$voice="+f1";}

$header = true;
$log ="/var/www/html/talk.log"; 
$cmd1="/var/www/html/talk1.txt"; if(file_exists($cmd1))  { unlink ($cmd1);}
$cmd2="/var/www/html/talk2.txt"; if(file_exists($cmd2))  { unlink ($cmd2);}
$cmd3="/var/www/html/chime.txt"; if(file_exists($cmd3))  { unlink ($cmd3);}

if($talk){
 $header= false;
// old v1 code testing only 
//$file="/home/pi/talk.wav";  if(file_exists($file))  { unlink ($file);}// cleanup for v1
// $send="espeak $lang$voice -w $file '$talk'";
// exec($send, $output, $return_var ); 
 
// build the v2 text command files
$fileOUT = fopen($cmd1, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$talk") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
$fileOUT = fopen($cmd2, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$lang$voice") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
}

if ($play){
// find the location of the file or 403 error
// look in home/pi/
if(file_exists("/home/pi/$play.wav"))  { $ok=true; $play="/home/pi/$play.wav";}
if(file_exists("/home/pi/$play.mp3"))  { $ok=true; $play="/home/pi/$play.mp3";}
// look in /home/pi/Music
if(file_exists("/home/pi/Music/$play.wav"))  { $ok=true; $play="/home/pi/Music/$play.wav";}
if(file_exists("/home/pi/Music/$play.mp3"))  { $ok=true; $play="/home/pi/Music/$play.mp3";}

$talk="(Play $play)";
if($ok){
        $fileOUT = fopen($cmd3, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$play") ;flock( $fileOUT, LOCK_UN );fclose ($fileOUT);
        $header=false;
       }
}
if ($header){
    header("HTTP/1.1 404 Not Found");
    header("Status: 404 Not Found");
    $return_var= "404 Not Found (Check permissions user www needs access to root)";
    
    }

$datum = date('[Y-m-d H:i:s]'); 
$status = "$datum : Message:$talk From:$code $device status:$format $return_var";
print $status;
// save the log
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);


