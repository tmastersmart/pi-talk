/**
PI Talk media GPIO controler
Hubitat driver to connect to rasbery pi and talk
capability 
Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis,buton,Presence,Temp,Volts
=====================================================================================
Reads text on the pi or you can play any mp3 file on your pi through pi speakers.

        _        _            _            _                   _             _        
        /\ \     /\ \         /\ \         / /\                _\ \          /\_\      
       /  \ \    \ \ \        \_\ \       / /  \              /\__ \        / / /  _   
      / /\ \ \   /\ \_\       /\__ \     / / /\ \            / /_ \_\      / / /  /\_\ 
     / / /\ \_\ / /\/_/      / /_ \ \   / / /\ \ \          / / /\/_/     / / /__/ / / 
    / / /_/ / // / /        / / /\ \ \ / / /  \ \ \        / / /         / /\_____/ /  
   / / /__\/ // / /        / / /  \/_// / /___/ /\ \      / / /         / /\_______/   
  / / /_____// / /        / / /      / / /_____/ /\ \    / / / ____    / / /\ \ \      
 / / /   ___/ / /__      / / /      / /_________/\ \ \  / /_/_/ ___/\ / / /  \ \ \     
/ / /   /\__\/_/___\    /_/ /      / / /_       __\ \_\/_______/\__\// / /    \ \ \    
\/_/    \/_________/    \_\/       \_\___\     /____/_/\_______\/    \/_/      \_\_\   
                                                                                       
v3.0  05-04-2022 Added mains support flag
v2.9  03-30-2022 bug fixed speak was not working if no vol sent
v2.8  10-14-2021 After routine 5.1 you must now provide a VOL for speak
v2.7  09-26-2021 Added config for chimes
v2.6  09-25-2021 OFF forces custom alarmin off
v2.5  09-21-2021 Presence fixed Default was not getting setup
v2.4  09-19-2021 AlarmIN created 
v2.3  09-18-2021 Alarm from Pi added
v2.2  09-17-2021 Log / Display improvments,Temp Volts Model 
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

-------------------------------------------------
you need to install these files on your pi 
talk.php <-- this driver talks to this file
temp.php <-- this file post back to this driver
input-scan.php <-- Safe loading of get and post
talk.sh  <-- this runs in a loop to take action
temp-chart.sh <-- Draws a png temp chart in /images 

install-talk.sh <-- Installs extra programs on pi

https://github.com/tmastersmart/pi-talk/tree/main
-------------------------------------------------

https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy

* =================================================  
  (c) 2021 by WinnFreeNet.com all rights reserved
  permission to use on hubiat for free
* =================================================
*/

def clientVersion() {
    TheVersion="3.0"
 if (state.version != TheVersion){ 
     state.version = TheVersion
     Refresh() 
 }
}



import java.text.SimpleDateFormat
metadata {
    definition (name: "PI Talk media GPIO controler", namespace: "tmastersmart", author: "WinnFreeNet.com", importUrl: "https://raw.githubusercontent.com/tmastersmart/pi-talk/main/pi_talk.groovy") {
		capability "Refresh"
        capability "Notification"
	    capability "Chime"
        capability "Alarm"
        capability "MusicPlayer"
        capability "SpeechSynthesis"
        capability "PushableButton"
        capability "Presence Sensor"
        capability "Temperature Measurement"
        capability "Power Source"

        command "setModel", ["string"]
        command "setTemperature", ["Number"]
        command "setVolts", ["Number"]
        command "setMemory", ["string"]
        command "setAlarmIN", ["string"]
        command "unschedule"
        
        
        attribute "Temperature", "string"
        attribute "volts", "string"
        attribute "alarmin","string"
 }

                
    preferences {
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/talk.php",required: true)
        input("url2",  "text", title: "Presence URL", description: "http://0.0.0.0/",required: true)
        input("pollMinutes",  "text", title: "Polling Minutes", description: "Schedule to check",defaultValue: 10,required: true)

        input("code", "text", title: "Hubs name", description: "to identify the hub in the pi log",required: true)
        input("voice", "text", title: "Voice code", description: "+m1 +m2 +m3 +m4 +m5 +m6 +m7 for male voices and +f1 +f2 +f3 +f4 for female or +croak and +whisper.")
        input("lang", "text", title: "Language", description: "-ven-us USA -ves spanish -vde german(see 'espeak --voices' on pi)")

        input("chime1", "text", title: "Chime 1", description: "The name of the mp3 or wav file to play [dont enter ext .mp3]",defaultValue: "1",required: true)
        input("chime2", "text", title: "Chime 2", description: "The name of the mp3 or wav file to play [dont enter ext .mp3]",defaultValue: "2",required: true)
        input("chime3", "text", title: "Chime 3", description: "The name of the mp3 or wav file to play [dont enter ext .mp3]",defaultValue: "3",required: true)
        input("chime4", "text", title: "Chime 4", description: "The name of the mp3 or wav file to play [dont enter ext .mp3]",defaultValue: "4",required: true)       
        
        input("scode", "text", title: "siren", description: "Name of the Siren file to play [dont enter.mp3]",required: true)


        input("gpio1", "text", title: "1st GPIO", description: "Button 1 on Button 2 OFF. number of GPIO (0 disable)",defaultValue: "0",required: true)
        input("gpio2", "text", title: "2nd GPIO", description: "Button 3 on Button 4 OFF. number of GPIO (0 disable)",defaultValue: "0",required: true)
        input("gpio3", "text", title: "3rd GPIO", description: "Button 5 Push on momentary (0 disable)",defaultValue: "0",required: true)

    }              
}

def setAlarmIN(st) {
    log.info "${device.displayName} PI sent Alarm ${st}"    
    sendEvent(name: "alarmin", value: st, descriptionText: "Custom alarm from PI")    
   
}


def setModel(version) {
    log.info "${device.displayName} Model is ${version}"    
    updateDataValue("model", version)
}
def setMemory(version) {
    log.info "${device.displayName} Memory is ${version}"    
    updateDataValue("Memory", version)
}
def setVolts(coreVolts) {
//    log.info "${device.displayName} PI Core Volts ${coreVolts}v"
    sendEvent(name: "volts", value: coreVolts, descriptionText: "Core Volts", unit: "v")    
   
}

def setTemperature(temp) {
//    log.info "${device.displayName} PI Core Temp is ${temp}c"
    sendEvent(name: "temperature", value: temp, descriptionText: "Core Temp", unit: "c")
    
// This resets the pressence flags
   if (device.currentValue('presence') != "present") {
	   sendEvent(name: "presence", value: "present") 
	   log.info "${device}: presence set by temp report. Im Back. "
       state.tryPresence = 0
  }
   
}

def push(button) {
    
// cleanup temp var
    state.remove("gpioState")        
    state.gpioSwitch = ""

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
        sendEvent(name: "status", value: "Error", descriptionText: "Button${button} not supported", displayed: true)    
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
            "dev": "${device.deviceId}",
        ]
    ]
	
   log.info "${device} :Button ${button} Pushed GPIO ${state.gpio} State:${state.gpioState}"    
   
// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok", descriptionText: "${state.gpioSwitch}", displayed: true) 
               sendEvent(name: "pushed", value: "${button}", isStateChange: true, type: "digital")
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error", descriptionText: "${e.message}", displayed: true)        
        log.warn "${device} Error: ${e.message}"
    // we dont send a event for switch if error 
    }
}

def off(cmd){
// This does nothing but notify the hub we are off and log
// We only play sounds no OFF is needed but hub will send it
  sendEvent(name: "siren", value: "off", descriptionText: "We were already off", displayed: true)
  sendEvent(name: "strobe", value: "off", descriptionText: "We were already off", displayed: true)
  log.info "${device} :Ignoring OFF command" 
  sendEvent(name: "status", value: "ok", descriptionText: "im ok", displayed: true)
  sendEvent(name: "alarmin", value: "OFF", descriptionText: "forcing off")   
}

def siren(cmd){
  playMP3(scode)
  log.info "${device} :siren"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off") 
}
def strobe(cmd){
  playMP3(scode)
  log.info "${device} :strobe"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off")  
}
def both(cmd){
  playMP3(scode)
  log.info "${device} :siren / strobe"
  sendEvent(name: "siren", value: "on")  
  sendEvent(name: "siren", value: "off")
  sendEvent(name: "strobe", value: "off")   
}


// PlaySound is the chime - numbers
def playSound(soundnumber){
   log.info "${device} :Chime  ${soundnumber}" 
   sendEvent(name: "chime", value: "${soundnumber}") 
    if (soundnumber== 1){ soundnumber = chime1}
    if (soundnumber== 2){ soundnumber = chime2}
    if (soundnumber== 3){ soundnumber = chime3}
    if (soundnumber== 4){ soundnumber = chime4}
// any other # will get passed as a filename 5.mp3
    playMP3(soundnumber)
}


// Custom MP3 routine
def playMP3(soundname){
    def params = [
            uri: "${url}",
        query: ["play": "${soundname}","code": "${code}","dev": "${device.deviceId}",]
    ]
	
   log.info "${device} :Playing File: ${url}?play=${soundname}"    
   sendEvent(name: "received", value: "${soundname}")

// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok", descriptionText: "Playing : ${soundname}", displayed: true)
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error", descriptionText: "${e.message}", displayed: true)       
        log.warn "${device} Error: ${e.message}"
 
    }
}


// unsuported music handler error trap
// Hub needs these to be trapped or error thrown.
def customError (st){
  sendEvent(name: "received", value: "${st}") 
  log.warn "${device} :${st} Not supported yet"
  sendEvent(name: "status", value: "error", descriptionText: "Unknown cmd", displayed: true) 

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
  playMP3(message)	
}


def resumeTrack(message){
  log.info "${device} :resumeTrack ${message}" 
  playMP3(message)	
}

def playTrack(message){
  log.info "${device} :PlayTrack ${message}"  
  playMP3(message)
}
                 
def play(ok){
    log.info "${device} :Play Chime 1"
    playSound(1)
}
def playText(message){
  deviceNotification(message) 
}

// After v5.1 routine you must provide a vol level
// Its ignored but must be provided or driver crashes
  
def speak(message, volume=-1, voice="") {    
    log.info "${device} :Speak() msg:${message} VOL:${volume} "
    deviceNotification(message)
}




def deviceNotification(message) {
    
    def params = [
        uri: "${url}",
        query: ["talk": "${message}","code": "${code}","voice": "${voice}","lang": "${lang}","dev": "${device.deviceId}",]
    ]
 
   log.info "${device} :Sending Message: ${url}?talk=${message}"    
   sendEvent(name: "received", value: "${message}")
    
// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok", descriptionText: "Speaking : ${message}", displayed: true)
            }
            else {log.info "${device} : Received Status ${resp.status}"}
        }
    } catch (Exception e) {
        sendEvent(name: "status", value: "Error", descriptionText: "${e.message}", displayed: true)    
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



// scheduled for 20 mins.
def refresh() {
	state.tryPresence = state.tryPresence + 1
    state.remove("gpioSwitch")
    state.remove("gpioState")
    sendEvent(name: "pushed", value: "")
    asynchttpGet("httpGetPresence", [uri: url2,timeout: 10]);
}


def httpGetPresence(response, data) {
	
	if (response == null || response.class != hubitat.scheduling.AsyncResponse) {
		return
	}
    
    def st = response.getStatus()
  
	if (st == 200) {
		state.tryPresence = 0
        log.info "${device}: Presence check status =${st} (OK)"
		
		if (device.currentValue('presence') != "present") {
           sendEvent(name: "presence", value: "present")
           log.info "${device}: is present" 
           sendEvent(name: "status", value: "ok", descriptionText: "Im Back", displayed: true)  
 
         }
        
        if (device.currentValue('powerSource') != "mains") {
           sendEvent(name: "powerSource", value: "mains", isStateChange: true)
           log.info "${device}: Power: Mains" 
        }
   }// end 200
    
    else { 
        log.info "${device}: Presence check status =${st} Failed:${state.tryPresence} Times."    
    
        if (state.tryPresence > 2){
        if (device.currentValue('presence') == "present") {
         sendEvent(name: "presence", value: "not present")
         log.warn "${device}: not present"    
         sendEvent(name: "powerSource", value: "battery", isStateChange: true)
         log.info "${device}: Power: OFF"    
         sendEvent(name: "status", value: "error", descriptionText: "OFFLINE", displayed: true)   
        } 
    }
   }// end else
}// end get

