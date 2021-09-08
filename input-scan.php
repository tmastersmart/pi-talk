<?php
// input scanner.
// copyright winnfreenet.com 1998/2021
// all rights reserved 

array($fieldNames);
array($fieldValues);

$in_get = false;
$in_post = false;

$HTTP_POST_VARS = $_POST;

// Scan for post
$i = 0;
while (list($key, $value) = each($HTTP_POST_VARS)) {
// strip all CODE
  strip_tags($key);
  strip_tags($value);
//  escapeshellarg($value);
// Replacement scan for escapeshellarg
 $value = str_replace(array('\\', '%'), array('\\\\', '%%'), $value);

   
//Remove line feeds
  $key = str_replace("\r", "", $key);
  $key = str_replace("\n", "", $key);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldNames[$i] = preg_replace($find,"**SPAM**",$key);
  
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
  $fieldValues[$i] = preg_replace($find,"**SPAM Injection Detected**",$value);

//  print "$fieldNames[$i]=$fieldValues[$i]<br>";
  $i++;
  $in_post = true;
}

$HTTP_GET_VARS = $_GET;

// Scan for Get
while (list($key, $value) = each($HTTP_GET_VARS)) {
// strip all CODE
  strip_tags($key);
  strip_tags($value);
//  escapeshellarg($value);
// Replacement scan for escapeshellarg
// $value = str_replace(array('\\', '%'), array('\\\\', '%%'), $value);

   
//Remove line feeds
  $key = str_replace("\r", "", $key);
  $key = str_replace("\n", "", $key);
// Remove injected headers
  $find = array("/bcc\:/i","/Content\-Type\:/i","/Mime\-Type\:/i","/cc\:/i","/to\:/i");
  $fieldNames[$i] = preg_replace($find,"**SPAM**",$key);
  
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
  $fieldValues[$i] = preg_replace($find,"**SPAM Injection Detected**",$value);

//   print "$fieldNames[$i]=$fieldValues[$i]<br>";
  $i++;
  $in_get = true;
}
if ($in_get) {$format="GET";}
if ($in_post){$format="POST";}
?>
