/**
PI Talk media controler
Hubitat driver to connect to rasbery pi and talk
capability Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis
===============================================================

Reads text on the pi or you can play any mp3 file on your pi through pi speakers.



v1.8  09/13/2021 Error Detection added (PI now reports 404 not found for missing mp3)
v1.7  09/11/2021 PlayTrack support added 
v1.6  09/11/2021 Music player/SpeechSynthesis added
v1.5  09/10/2021 Siren added
v1.4  09/09/2021 
v1.3  09/09/2021  
v1.2  09/09/2021 
v1.1  09/08/2021
v1.0  09/08/2021 


you need to get talk.php and talk.sh to run on your pi see this url
https://github.com/tmastersmart/pi-talk/tree/main


https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

* =================================================  
  (c) 2021 by WinnFreeNet.com all rights reserved
  permission to use on hubiat for free
* =================================================
*/
import java.text.SimpleDateFormat
metadata {
    definition (name: "PI Talk media controler", namespace: "tmastersmart", author: "WinnFreeNet.com", importUrl: "https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy") {
        capability "Notification"
	    capability "Chime"
        capability "Alarm"
//        capability "AudioNotification"
        capability "MusicPlayer"
        capability "SpeechSynthesis"
       
 }

                
    preferences {
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/talk.php")
        input("code", "text", title: "Hubs name", description: "to identify the hub in the pi log")
        input("voice", "text", title: "Voice code", description: "+m1 +m2 +m3 +m4 +m5 +m6 +m7 for male voices and +f1 +f2 +f3 +f4 for female or +croak and +whisper.")
        input("lang", "text", title: "Language", description: "-ven-us USA -ves spanish -vde german(see 'espeak --voices' on pi)")
        input("scode", "text", title: "chime # for siren", description: "Number for siren mp3 file ")

    }              
}

def siren(cmd){
  playSound(scode)
  log.info "${device} :siren"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off") 
}
def strobe(cmd){
  playSound(scode)
  log.info "${device} :strobe"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off")  
}
def both(cmd){
  playSound(scode)
  log.info "${device} :siren / strobe"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off")   
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

    
//    httpPost(params){response ->
//            if(response.status != 200) {
//            log.error "${device} :Error ${response.status}" 
//                sendEvent(name: "status", value: "Error ${response.status}")
//}
//else {
//               log.info "${device} :Received ok"
//                sendEvent(name: "status", value: "ok")
//            }
        
// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok")
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error ${e.message}")    
        log.warn "${device} Error: ${e.message}"
  
    
    
    
    }
}


// unsuported music handler error trap
def customError (st){
  sendEvent(name: "received", value: "${st}")
  log.info "${device} :${st} received Not supported"
  sendEvent(name: "status", value: "error")     
}
def    off(ok)      {customError("OFF")}
def   mute(ok)      {customError("MUTE")}
def unmute(ok)      {customError("UNMUTE")}
def previousTrack(ok){customError("previousTrack")} 
def setLevel(ok)    {customError("SetLevel")} 
def setTrack(ok)    {customError("setTrack")} 
def nextTrack(ok)   {customError("nextTrack")} 
def pause(ok)       {customError("PAUSE")} 
def stop(ok)        {customError("STOP")}




def restoreTrack(message){
  log.info "${device} :restoreTrack ${message}"  
  playSound(message)	
}


def resumeTrack(message){
  log.info "${device} :resumeTrack ${message}" 
  playSound(message)	
}

def playTrack(message){
  log.info "${device} :PlayTrack ${message}"  
  playSound(message)
}
                 
def play(ok){
    log.info "${device} :Play received Playing 1 default"
    playSound(1)
}
def playText(message){
  deviceNotification(message) 
}


// speach values: [hello, 1, name]
// need to pull out first varable
def speak(message) {
    deviceNotification(message)
}




def deviceNotification(message) {
    def params = [
        uri: "${url}",
        query: ["talk": "${message}","code": "${code}","voice": "${voice}","lang": "${lang}",]
    ]
 
   log.info "${device} :Sending Message: ${url}?talk=${message}"    
   sendEvent(name: "received", value: "${message}")
    
// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok")
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error ${e.message}")    
        log.warn "${device} Error: ${e.message}"
  
    
    
    
    
//   old way 
//    httpPost(params){response ->
//            if(response.status != 200) {
//                log.error "${device} :Error ${response.status}" 
//                sendEvent(name: "status", value: "Error ${response.status}")
//            }
//            else {
//                log.info "${device} :Received ok"
//                sendEvent(name: "status", value: "ok")
//            }
    }
}

