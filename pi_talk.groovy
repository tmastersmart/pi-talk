/**
Hubitat driver to connect to rasbery pi and talk

 (c) 2021 by tmastersmart winnfreenet.com all rights reserved
  permission to use on hubiat for free

v1.3  09/09/2021  
v1.2  09/09/2021  Remove music/added status
v1.1  09/08/2021
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
	capability "Chime"
//       capability "Speech Synthesis"
//        capability "AudioNotification"
//        capability "MusicPlayer"
//        capability "SpeechSynthesis"
       
 }

                
    preferences {
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/talk.php")
        input("code", "text", title: "Hubs name", description: "to identify the hub in the pi log")
        input("voice", "text", title: "Voice code", description: "+m1 +m2 +m3 +m4 +m5 +m6 +m7 for male voices and +f1 +f2 +f3 +f4 for female or +croak and +whisper.")
        input("lang", "text", title: "Language", description: "-ven-us USA -ves spanish -vde german(see 'espeak --voices' on pi)")

    }              
}






def playSound(soundnumber){
// codes are set at the hub    
     def params = [
            uri: "${url}",
        query: [
            "play": "${soundnumber}",
            "code": "${code}",
        ]
    ]

	
   log.info "${device} :Sending Chime: ${url}?play=${soundnumber}"    
   sendEvent(name: "received", value: "${soundnumber}")
    httpPost(params){response ->
            if(response.status != 200) {
                log.error "${device} :Error ${response.status}" 
                sendEvent(name: "status", value: "Error ${response.status}")
            }
            else {
                log.info "${device} :Received ok"
                sendEvent(name: "status", value: "ok")
            }
        
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
            "voice": "${voice}",
            "lang": "${lang}",
        ]
    ]

   log.info "${device} :Sending Message: ${url}?talk=${message}"    
   sendEvent(name: "received", value: "${message}")
    httpPost(params){response ->
            if(response.status != 200) {
                log.error "${device} :Error ${response.status}" 
                sendEvent(name: "status", value: "Error ${response.status}")
            }
            else {
                log.info "${device} :Received ok"
                sendEvent(name: "status", value: "ok")
            }
        
    }
}

