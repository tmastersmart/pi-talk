/**
PI Talk media controler
Hubitat driver to connect to rasbery pi and talk
capability Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis

 (c) 2021 by WinnFreeNet.com all rights reserved
  permission to use on hubiat for free

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

*   
* 
*
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


// unsuported music handler error trap
def customError (st){
  sendEvent(name: "received", value: "${st}")
  log.info "${device} :${st} received Not supported"
  sendEvent(name: "status", value: "error")     
}
def    off(ok)      {customError("OFF")}
def   mute(ok)      {customError("MUTE")}
def unmute(ok)      {customError("UNMUTE")}
def restoreTrack(ok){customError("RestoreTrack")} 
def previousTrack(ok){customError("previousTrack")} 
def resumeTrack(ok) {customError("resumeTrack")} 
def setLevel(ok)    {customError("SetLevel")} 
def setTrack(ok)    {customError("setTrack")} 
def nextTrack(ok)   {customError("nextTrack")} 
def pause(ok)       {customError("PAUSE")} 
def stop(ok)        {customError("STOP")}
def playTrack(ok)   {customError("playTrack")}


// redirect these                    
def play(ok){
    log.info "${device} :Play received Playing 1 default"
    playSound(1)
}
def playText(message){
  playSound(message)
  log.info "${device} :PlayTest sound by filename "  
}


// speach values: [hello, 1, name]
// need to pull out first varable
def speak(message) {
    deviceNotification(message)
}




def deviceNotification(message) {
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

