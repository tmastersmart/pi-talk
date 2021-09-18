<?php
// --------------------------------------------------
// (c) 1998/2021 WinnFreeNet.com All rights reserved
// Safe input scanner Post get injection detection
// Permission to use on Hubitat granted
//  
// v2021 Mods for rasbery PI
// v1998 Orginal Internal use 
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


array($fieldNames);
array($fieldValues);


// Scan for post
$HTTP_POST_VARS = $_POST;
$i = 0;
while (list($key, $value) = each($HTTP_POST_VARS)) {
  strip_tags($key);
  strip_tags($value);
  $value = str_replace(array('\\', '%'), array('\\\\', '%%'), $value);
  $key = str_replace("\r", "", $key);
  $key = str_replace("\n", "", $key);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldNames[$i] = preg_replace($find,"**Injection Detected**",$key);
  $value = str_replace("<", "_", $value);
  $value = str_replace(">", "_", $value);
  $value = str_replace(",", "_", $value);
  $value = str_replace(";", "_", $value);
  $value = str_replace("%", "_", $value);
  $value = str_replace("\"", "_", $value);
  $value = str_replace("php", "___", $value);
  $value = str_replace("\r", "", $value);
  $value = str_replace("\n", "", $value);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldValues[$i] = preg_replace($find,"**Injection Detected**",$value);
  $i++;
  $format="GET $i fields";
}

// Scan for Get
$HTTP_GET_VARS = $_GET;
while (list($key, $value) = each($HTTP_GET_VARS)) {
  strip_tags($key);
  strip_tags($value);   
//Remove line feeds
  $key = str_replace("\r", "", $key);
  $key = str_replace("\n", "", $key);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldNames[$i] = preg_replace($find,"**Injection Detected**",$key);
  $value = str_replace("<", "_", $value);
  $value = str_replace(">", "_", $value);
  $value = str_replace(",", "_", $value);
  $value = str_replace(";", "_", $value);
  $value = str_replace("%", "_", $value);
  $value = str_replace("\"", "_", $value);
  $value = str_replace("php", "___", $value);
  $value = str_replace("\r", "", $value);
  $value = str_replace("\n", "", $value);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldValues[$i] = preg_replace($find,"**Injection Detected**",$value);
  $i++;
  $format="POST $i fields";
}


?>
