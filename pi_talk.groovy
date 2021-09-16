/**
PI Talk media GPIO controler
Hubitat driver to connect to rasbery pi and talk
capability Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis,buton,Presence
===============================================================

Reads text on the pi or you can play any mp3 file on your pi through pi speakers.

v2.1  09-16-2021 Button code changed
v2.0  09-15-2021 Presence Sensor added. Switch removed due to OFF conflice with Strobe
v1.9  09/13/2021 Switch and button support added via GPIO (better logs)
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
    definition (name: "PI Talk media switch controler", namespace: "tmastersmart", author: "WinnFreeNet.com", importUrl: "https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy") {
		capability "Refresh"
        capability "Notification"
	    capability "Chime"
        capability "Alarm"
        capability "MusicPlayer"
        capability "SpeechSynthesis"
        capability "PushableButton"
        capability "Presence Sensor"
 }

                
    preferences {
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/talk.php",required: true)
        input("code", "text", title: "Hubs name", description: "to identify the hub in the pi log",required: true)
        input("voice", "text", title: "Voice code", description: "+m1 +m2 +m3 +m4 +m5 +m6 +m7 for male voices and +f1 +f2 +f3 +f4 for female or +croak and +whisper.")
        input("lang", "text", title: "Language", description: "-ven-us USA -ves spanish -vde german(see 'espeak --voices' on pi)")
        input("scode", "text", title: "chime # for siren", description: "Number for siren mp3 file ")

        input("gpio1", "text", title: "1st GPIO", description: "Button 1 on Button 2 OFF. number of GPIO (0 disable)",defaultValue: "0")
        input("gpio2", "text", title: "2nd GPIO", description: "Button 3 on Button 4 OFF. number of GPIO (0 disable)",defaultValue: "0")
        input("gpio3", "text", title: "3rd GPIO", description: "Button 5 Press on wait then Off (0 disable)",defaultValue: "0")

        input("url2",  "text", title: "Presence URL", description: "http://0.0.0.0",required: true)
        input("pollMinutes",  "text", title: "Polling Minutes", description: "Schedule to check",defaultValue: 10,required: true)


    }              
}


def push(button) {
    state.gpioSwitch = ""
    state.gpioState=0
// button matrix    
    if (button == 1 ){
        state.gpio = gpio1 //Button 1 on Button 2 On
        state.gpioState=1
        state.gpioSwitch = "1st GPIO ON"
        customPush(button)
    }
    if (button == 2 ){
        state.gpio = gpio1 //Button 2 on Button 2 OFF
        state.gpioState=0
        state.gpioSwitch = "1st GPIO OFF"
        customPush(button)
    }
    if (button == 3 ){
        state.gpio = gpio2 //Button 3 on Button 4 On
        state.gpioState=1
        state.gpioSwitch = "2nd GPIO On"
        customPush(button)
    }
    if (button == 4 ){
        state.gpio = gpio2 //Button 3 on Button 4 OFF
        state.gpioState=0
        state.gpioSwitch = "2nd GPIO OFF"
        customPush(button)
    }
    if (button == 5 ){
        state.gpio = gpio3 //Button 5 Press on wait then Off
        state.gpioState=4 
        state.gpioSwitch = "3rd GPIO Pressed"
        customPush(button)
    }

    if (state.gpioSwitch == ""){
        sendEvent(name: "status", value: "Error")    
        log.warn "${device} Error Button${button} not supported"
    }
       
}

 def customPush(button){   
       def params = [
            uri: "${url}",
        query: [
            "gpio": "${state.gpio}",
            "switch": "${state.gpioState}",
            "button": "${button}", // we really dont need this. Just for logging
            "code": "${code}",
        ]
    ]
	
   log.info "${device} :Button ${button} Pushed GPIO ${state.gpio} State:${state.gpioState}"    
   
// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok ${state.gpioSwitch}")
               sendEvent(name: "pushed", value: "${button}", isStateChange: true, type: "digital")
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error ${e.message}")    
        log.warn "${device} Error: ${e.message}"
    // we dont send a event for switch if error 
    }
}

def off(cmd){
// This does nothing but notify the hub we are off
// Siren only plays sound then turns off but hub will
// send off anyway
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off") 
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
    def params = [
            uri: "${url}",
        query: [
            "play": "${soundnumber}",
            "code": "${code}",
        ]
    ]
	
   log.info "${device} :Playing File: ${url}?play=${soundnumber}"    
   sendEvent(name: "received", value: "${soundnumber}")

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
  log.error "${device} :${st} received Not supported"
  sendEvent(name: "status", value: "error")     
}

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
    }
}


def updated () {
	log.debug "${device}: Cron updated ${pollMinutes}min"
    state.tryPresence = 0
    schedule("0 */${pollMinutes} * ? * *", refresh)
    runIn(2, refresh)
//    sendEvent(name: "presence", value: "present")   
//    log.info "${device}: Present AT DEFAULT" 
}




def refresh() {
    log.info "${device}: refresh"
	state.tryPresence = state.tryPresence + 1
    
    if (state.tryPresence > 3){
        if (device.currentValue('presence') == "present") {
         sendEvent(name: "presence", value: "not present")   
         log.info "${device}: is OFFLINE"   
        } 
    }
    
    asynchttpGet("httpGetPresence", [uri: url2,timeout: 10]);
}


def httpGetPresence(response, data) {
	
	if (response == null || response.class != hubitat.scheduling.AsyncResponse) {
		return
	}
    
    def st = response.getStatus()
    
//    log.debug "${device}: Presence check status =${st}"

	if (st == 200) {
		state.tryPresence = 0
		
		if (device.currentValue('presence') == "not present") {
           sendEvent(name: "presence", value: "present")   
           log.info "${device}: is Present"   
         } 
//        else {
//        log.warn "${device}: Presence failed ${state.tryPresence} Tries"
//        }
   }
}

