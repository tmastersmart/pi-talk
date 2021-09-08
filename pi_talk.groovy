/**
Hubitat driver to connect to rasbery pi and talk

 (c) 2021 by tmastersmart winnfreenet.com all rights reserved
  permission to use on hubiat for free
 

v1.0  09/08/2021 


you need to get talk.php and talk.sh to run on your pi see this url
https://github.com/tmastersmart/pi-talk/tree/main

https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

*   
* 
*
*/
import java.text.SimpleDateFormat
metadata {
    definition (name: "PI Talk no cloud", namespace: "tmastersmart", author: "Tmaster", importUrl: "https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy") {
        capability "Notification"
        capability "Speech Synthesis"
 }

                
    preferences {
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/talk.php")
        input("code", "text", title: "Code to identify hub", description: "the hubs name")
 }              
}









def speak(message) {
    deviceNotification(message)
}

def deviceNotification(message) {

    
// what is the varable for the hubs name
     def params = [
            uri: "${url}",
        query: [
            "talk": "${message}",
            "code": "${code}",
        ]
    ]

   log.info "${device} :Sending Message: ${url}?talk=${message}"    

    httpPost(params){response ->
            if(response.status != 200) {log.error "${device} :Error ${response.status}. " }
            else {log.info "${device} :Received ok"}
    }
}

