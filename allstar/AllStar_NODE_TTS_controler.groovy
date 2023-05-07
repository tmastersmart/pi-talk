/**
Allstar Node Talk relay 
TTS to Allstar
Hubitat driver to connect to rasbery pi Allstar node and talk
capability 
Notification,Chime,Alarm,MusicPlayer,SpeechSynthesis,buton,Presence,Temp,Volts
=====================================================================================
Text to speach read out over your Allstar Node.

This is the driver only. Requires other part for the node


v1.0  05/07/2023  Ported from my PI talk scripts.  

-------------------------------------------------
you need to install these files on your pi 
/srv/http/supermon/talk.php <-- this driver talks to this file
/srv/http/supermon/input-scan.php <-- Safe loading of get and post

/etc/astrix/local/talk-loop.sh  <-- this runs in a loop to take action


https://github.com/tmastersmart/pi-talk/
-------------------------------------------------


* =================================================  
  (c) 2021 / 2023 by WinnFreeNet.com lagmrs.com
  all rights reserved.  Permission to use on Hubitat
  and GMRS nodes..   
* =================================================
*/

def clientVersion() {
    TheVersion="1.0"
 if (state.version != TheVersion){ 
     state.version = TheVersion
   
 }
}



import java.text.SimpleDateFormat
metadata {
    definition (name: "AllStar NODE TTS controler", namespace: "tmastersmart", author: "WinnFreeNet.com", importUrl: "") {
		    capability "Refresh"
        capability "Notification"
        capability "SpeechSynthesis"
        capability "Presence Sensor"
        command "unschedule"
        
        
 }

                
    preferences {
      input name: "infoLogging",  type: "bool", title: "Enable info logging",  description: "Recomended low level" ,defaultValue: true, required: true
	    input name: "debugLogging", type: "bool", title: "Enable debug logging", description: "MED level Debug" ,     defaultValue: false,required: true
	    input name: "traceLogging", type: "bool", title: "Enable trace logging", description: "Insane HIGH level",    defaultValue: false,required: true

        
        input("url", "text", title: "URL OF PI:", description: "http://0.0.0.0/supermon/talk.php",required: true)
        input("url2",  "text", title: "Presence URL", description: "http://0.0.0.0/",required: true)
        input("pollMinutes",  "text", title: "Polling Minutes", description: "Schedule to check",defaultValue: 40,required: true)

        input("code", "text", title: "Password", description: "must match password in talk.php file",defaultValue: "GMRS",required: true)
 
    }              
}

def initialize() {
    logging("Initialize", "warn")
}


def updated () {
    loggingUpdate()
    clientVersion()
    logging("Cron updated ${pollMinutes}min", "info") 

    state.tryPresence = 0
    schedule("0 */${pollMinutes} * ? * *", refresh)
    runIn(2, refresh)

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
        query: ["talk": "${message}","code": "${code}","dev": "${device.deviceId}",]
    ]
   logging("Sending Message: ${url}?talk=${message}", "info") 

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






// scheduled for 20 mins.
def refresh() {
	state.tryPresence = state.tryPresence + 1
    asynchttpGet("httpGetPresence", [uri: url2,timeout: 10]);
}


def httpGetPresence(response, data) {
	
	if (response == null || response.class != hubitat.scheduling.AsyncResponse) {
		return
	}
    
    def st = response.getStatus()
    code="error"
    if(st == 200){code="ok"}
    if(st == 400){code="Bad Request"}
  	if(st == 401){code="Unauthorized"}
  	if(st == 404){code="File Not Found"}
    if(st == 500){code="Server Error"}
    if(st == 503){code="Service Unavailable"}

	if (st == 200 || st == 404 ) {
		state.tryPresence = 0
        logging("Presence check [${st} ${code}] Tries:${state.tryPresence}", "info") 
        
		
		if (device.currentValue('presence') != "present") {
           sendEvent(name: "presence", value: "present")
           logging("is present", "info") 
           sendEvent(name: "status", value: "ok", descriptionText: "Im Back", displayed: true)  
 
         }
        
        if (device.currentValue('powerSource') != "mains") {
           sendEvent(name: "powerSource", value: "mains", isStateChange: true)
           logging("Power: Mains", "info")  

        }
   }// end 200
    
    else { 
        logging("Presence check [${st} ${code}] Tries:${state.tryPresence}", "warn") 
   
        if (state.tryPresence > 2){
        if (device.currentValue('presence') == "present") {
         sendEvent(name: "presence", value: "not present")
         logging("not present", "warn")    
         sendEvent(name: "status", value: "OFFLINE", descriptionText: "${st} ${code}", displayed: true)   
        } 
    }
   }// end else
}// end get
// Logging block  v4

void loggingUpdate() {
    logging("Logging Info:[${infoLogging}] Debug:[${debugLogging}] Trace:[${traceLogging}]", "infoBypass")
    // Only do this when its needed
    if (debugLogging){
        logging("Debug log:off in 3000s", "warn")
        runIn(3000,debugLogOff)
    }
    if (traceLogging){
        logging("Trace log: off in 1800s", "warn")
        runIn(1800,traceLogOff)
    }
}

void traceLogOff(){
	device.updateSetting("traceLogging",[value:"false",type:"bool"])
	log.trace "${device} : Trace Logging : Automatically Disabled"
}
void debugLogOff(){
	device.updateSetting("debugLogging",[value:"false",type:"bool"])
	log.debug "${device} : Debug Logging : Automatically Disabled"
}
private logging(String message, String level) {
  if (level == "infoBypass"){log.info  "${device} : $message"}
	if (level == "error"){     log.error "${device} : $message"}
	if (level == "warn") {     log.warn  "${device} : $message"}
	if (level == "trace" && traceLogging) {log.trace "${device} : $message"}
	if (level == "debug" && debugLogging) {log.debug "${device} : $message"}
  if (level == "info"  && infoLogging)  {log.info  "${device} : $message"}
}
