<?php
//  ------------------------------------------------------------
//  (c) 2021 by tmastersmart winnfreenet.com all rights recerved
//  Permission granted to install and use wuith hubitat for free   
//  https://github.com/tmastersmart/pi-talk
//   
//  PI Temp and voltage post back tp PI controler
//  
// 
//  v1.0 09-17-2021 First version Manual setup
//   
// ---------------------------------------------------
// you need to install these files on your pi 
// place php files in /var/www/html/
// 
// talk.php <-- this reveives commands from HUB
// temp.php <-- this file post to HUB
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
// php /var/www/html/temp.php 
// 
// Install MAKER API first then give device permission
// =====================settings================================
$hub="192.168.0.13";
$maker="282";// device of maker api
$token = "0ba48ca1-f585-41b7-bfd4-2f9b7a134ed5";
$device ="814"; // Which device to post to

//===============================================================



   $log ="/var/www/html/temp.log";// Set log rotation on this file weekly
$tempLog="/var/www/html/temp.dat";// Set log rotation on this file daily
$setmodel=false;
$setonce="/var/www/html/set.dat";if(file_exists($setonce)) {$setmodel=true;}
$tempSensor="/opt/vc/bin/vcgencmd measure_temp >/var/www/html/data.txt";
$voltSensor="/opt/vc/bin/vcgencmd measure_volts >>/var/www/html/data.txt";
$versSensor="gpio -v >/var/www/html/set.txt";
exec($tempSensor, $tempIN, $return_var );
exec($voltSensor, $voltIN, $return_var );


$input = file("/var/www/html/data.txt");
$volt ="";$temp="";$ver="";$pos1="";$pos2="";$pos3="";$html="";$error="ok";$memory="";
$i=0;
foreach ($input as $item) {
$pos1 = strpos($item ,"emp=");if($pos1){$test=$item;$Lpos = strpos($test, '=');$Rpos = strpos($test, "'");$temp = substr($test, $Lpos+1,$Rpos-$Lpos-1);}
$pos2 = strpos($item ,"olt=");if($pos2){$test=$item;$Lpos = strpos($test, '=');$Rpos = strpos($test, 'v');$volt = substr($test, $Lpos+1,$Rpos-$Lpos-1);}

$i++;
}
//  Type: Pi 3, Revision: 02, Memory: 1024MB, Maker: Embest 



$datum = date('[Y-m-d H:i:s]'); 


$ip  = $hub;
$url = "/apps/api/$maker/devices/$device/setTemperature/$temp?access_token=$token";
$getheader = true; $htmlON=false;
$html = http_request('GET', $ip, 80 , "$url");
$MS_Error ="";
$MS_Error = strpos($html, '404'); if ($MS_Error){$error="404 Not Found";}
$MS_Error = strpos($html, '505'); if ($MS_Error){$error="505 Not Supported";}


$url = "/apps/api/$maker/devices/$device/setVolts/$volt?access_token=$token";
$getheader = true; $htmlON=false;
$html = http_request('GET', $ip, 80 , "$url");
$MS_Error ="";
$MS_Error = strpos($html, '404'); if ($MS_Error){$error="$error 404 Not Found";}
$MS_Error = strpos($html, '505'); if ($MS_Error){$error="$error 505 Not Supported";}


// we only post this one time
// Erase set.dat to rerun

if (!$setmodel){

exec($versSensor, $PIversion, $return_var );

$input = file("/var/www/html/set.txt");
$volt ="";$temp="";$ver="";$pos1="";$pos2="";$pos3="";$html="";$error="ok";$memory="";
$i=0;
foreach ($input as $item) {
$pos3 = strpos($item ,"*-->");if($pos3){$test = substr($item, ($pos3),60);$Lpos = strpos($test, '>');$Rpos = strpos($test, chr(10));$ver = substr($test, $Lpos+2,$Rpos-$Lpos-1);}
$pos4 = strpos($item ,"Memory");if($pos4){$test = substr($item, ($pos4),60);$Lpos = strpos($test, ':');$Rpos = strpos($test, ",");$memory = substr($test, $Lpos+2,$Rpos-$Lpos-2);}

$i++;
}




$ver = str_replace(chr(13), "", $ver);
$ver = str_replace(chr(10), "", $ver);
$logSave="$ver,$memory";
$ver = str_replace(" ", "%20", $ver);
$url = "/apps/api/$maker/devices/$device/setMemory/$memory?access_token=$token";
$getheader = true; $htmlON=false;
$html = http_request('GET', $ip, 80 , "$url");
$MS_Error ="";
$MS_Error = strpos($html, '404'); if ($MS_Error){$error="$error 404 Not Found";}
$MS_Error = strpos($html, '505'); if ($MS_Error){$error="$error 505 Not Supported";}

$url = "/apps/api/$maker/devices/$device/setModel/$ver?access_token=$token";
$getheader = true; $htmlON=false;
$html = http_request('GET', $ip, 80 , "$url");
$MS_Error ="";
$MS_Error = strpos($html, '404'); if ($MS_Error){$error="$error 404 Not Found";}
$MS_Error = strpos($html, '505'); if ($MS_Error){$error="$error 505 Not Supported";}

$fileOUT = fopen($setonce, "w") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$logSave\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);

}

$datum = date('[Y-m-d H:i:s]'); 

// Build a log
$status = "$datum : Volts:$volt Temp:$temp";
if ($memory){ $status="$status Memory:$memory";}
if ($ver)   {$ver = str_replace("%20"," ", $ver); $status="$status Model:$ver";}
$status = "$status ST:$error";
print $status;
// save the log
$fileOUT = fopen($log, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$status\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);

// save the temp log for charting with gnuplot in format 00:00 39.7
$time=date('H:i');
$fileOUT = fopen($tempLog, "a") ;flock( $fileOUT, LOCK_EX );fwrite ($fileOUT, "$time $temp\n");flock( $fileOUT, LOCK_UN );fclose ($fileOUT);


function http_request(
    $verb = 'GET',             /* HTTP Request Method (GET and POST supported) */
    $ip,                       /* Target IP/Hostname */
    $port = 80,                /* Target TCP port */
    $uri = '/',                /* Target URI */
    $getdata = array(),        /* HTTP GET Data ie. array('var1' => 'val1', 'var2' => 'val2') */
    $postdata = array(),       /* HTTP POST Data ie. array('var1' => 'val1', 'var2' => 'val2') */
    $cookie = array(),         /* HTTP Cookie Data ie. array('var1' => 'val1', 'var2' => 'val2') */
    $custom_headers = array(), /* Custom HTTP headers ie. array('Referer: http://localhost/ */
    $timeout = 5000,           /* Socket timeout in milliseconds */
    $req_hdr = false,          /* Include HTTP request headers */
    $res_hdr = false           /* Include HTTP response headers */
    )
{
global $BasicA,$agent,$version,$getheader,$htmlON,$responceHeader,$Postit,$req,$exede,$exedeCookie1,$exedeCookie2;

 $postdata_str="";
    if(!$agent){$agent="pi";}

    if($Postit) {$postdata=$Postit;}

    if ($getheader){$res_hdr = true; }
    $ret = '';
    $verb = strtoupper($verb);
    $cookie_str = '';
    $getdata_str = count($getdata) ? '?' : '';
//    $postdata_str = '';
if (!$postdata_str){$postdata_str = '';}

    foreach ($getdata as $k => $v)
        $getdata_str .= urlencode($k) .'='. urlencode($v).'&';

    foreach ($postdata as $k => $v)
        $postdata_str .= urlencode($k) .'='. urlencode($v) .'&';

    foreach ($cookie as $k => $v)
        $cookie_str .= urlencode($k) .'='. urlencode($v) .'; ';

    $crlf = "\r\n";
//    $req = $verb .' '. $uri . $getdata_str .' HTTP/1.1' . $crlf;

    $req = $verb .' '. $uri .' HTTP/1.1' . $crlf;
    $req .= 'Host: '. $ip . $crlf;
    $req .= 'Connection: close' . $crlf;
    $req .= 'User-Agent: Mozilla/5.0 '. $agent . $crlf;
if ($BasicA){$req .= 'Authorization: Basic '. $BasicA . $crlf;}    $req .= 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' . $crlf;
    $req .= 'Accept-Language: en-us,en;q=0.5' . $crlf;
//    $req .= 'Accept-Encoding: deflate' . $crlf;
    $req .= 'Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7' . $crlf;

    foreach ($custom_headers as $k => $v)
        $req .= $k .': '. $v . $crlf;

    if (!empty($cookie_str))
        $req .= 'Cookie: '. substr($cookie_str, 0, -2) . $crlf;

    if ($exede){
       $req .= "Cookie: _ga=$exedeCookie1" . $crlf;
       $req .= "Cookie: _mkto_trk=$exedeCookie2" . $crlf;
    }

    if ($verb == 'POST' && !empty($postdata_str))
    {
        $postdata_str = substr($postdata_str, 0, -1);
        $req .= 'Content-Type: application/x-www-form-urlencoded' . $crlf;
        $req .= 'Content-Length: '. strlen($postdata_str) . $crlf . $crlf;
        $req .= $postdata_str;
    }
    else $req .= $crlf;

    if ($req_hdr)
        $ret .= $req;

    if (($fp = @fsockopen($ip, $port, $errno, $errstr)) == false){
    if ($errno=10060){$errstr="Timed Out";}
    return " error! $errno: $errstr";
    }
    fputs($fp, $req); //print "$req

    stream_set_timeout($fp, 0, $timeout * 1000);
        $ret = fgets($fp); $responceHeader =$ret; // gets responce header
// if is a webpage stop loading at the /html Prevents looping.
 while ($line = fgets($fp)) {
    $ret .= $line;
    if($htmlON) {$EndOfLine = strpos($line, '/html>'); if ($EndOfLine) { break;}}
  }
    fclose($fp);
    if (!$res_hdr){ $ret = substr($ret, strpos($ret, "\r\n\r\n") + 4);}
    return $ret;
}

