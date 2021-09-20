/**
PI Talk media GPIO controler
Hubitat driver to connect to rasbery pi and talk
capability 
Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis,buton,Presence,Temp,Volts
=====================================================================================

Reads text on the pi or you can play any mp3 file on your pi through pi speakers.

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

        command "setModel", ["string"]
        command "setTemperature", ["Number"]
        command "setVolts", ["Number"]
        command "setMemory", ["string"]
        command "setAlarmIN", ["string"]

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

        input("scode", "text", title: "siren", description: "Chime Number or name of file for siren [dont enter.mp3]")


        input("gpio1", "text", title: "1st GPIO", description: "Button 1 on Button 2 OFF. number of GPIO (0 disable)",defaultValue: "0")
        input("gpio2", "text", title: "2nd GPIO", description: "Button 3 on Button 4 OFF. number of GPIO (0 disable)",defaultValue: "0")
        input("gpio3", "text", title: "3rd GPIO", description: "Button 5 Push on momentary (0 disable)",defaultValue: "0")

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
    
// This Overides any not present
    state.tryPresence = 0
  		   if (device.currentValue('presence') == "not present") {
           sendEvent(name: "presence", value: "present") 
           sendEvent(name: "status", value: "ok", descriptionText: "Temp set Pressence", displayed: true)  
           log.info "${device}: is Present Set by Temp"   
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
        query: ["play": "${soundnumber}","code": "${code}","dev": "${device.deviceId}",]
    ]
	
   log.info "${device} :Playing File: ${url}?play=${soundnumber}"    
   sendEvent(name: "received", value: "${soundnumber}")

// improved post    
        try {
        httpPost(params) { resp ->
            if (resp.success) {
               log.info "${device} :Received at pi ok"
               sendEvent(name: "status", value: "ok", descriptionText: "Playing : ${soundnumber}", displayed: true)
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




def refresh() {
    
    log.info "${device}: refresh "
	state.tryPresence = state.tryPresence + 1
// cleanup temp var

    state.remove("gpioSwitch")
    state.remove("gpioState")
    sendEvent(name: "pushed", value: "")
// 

    if (state.tryPresence > 1){
       if (state.tryPresence < 4){ 
        if (device.currentValue('presence') == "present") {
        log.info "${device}: Failed presence ${state.tryPresence} Times. Give up on 4th"   
    }
  }
 }
    if (state.tryPresence > 3){
        if (device.currentValue('presence') == "present") {
         sendEvent(name: "presence", value: "not present")
         sendEvent(name: "status", value: "error", descriptionText: "Ill Be Back Later", displayed: true)   
         log.warn "${device}: is OFFLINE  Tried ${state.tryPresence} Times"   
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
           sendEvent(name: "status", value: "ok", descriptionText: "Im Back", displayed: true)  
           log.info "${device}: is Present and Online"   
         } 

   }
}

