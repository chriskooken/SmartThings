/**
 *  Hot Tub Controller
 *
 *  Copyright 2018 Chris Kooken
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  https://community.smartthings.com/t/where-are-the-tile-icons/40086/17
 * heater: 1 is heating, 3 is on and set
 */
metadata {
	definition (name: "Hot Tub Controller", namespace: "iAqualink", author: "Chris Kooken") {

        capability "Polling"
		capability "Refresh"
        command "spaModeOn"
        command "spaModeOff"
        command "setPointUp"
        command "setPointDown"
        command "lightOn"
        command "lightOff"
        command "jetsOn"
        command "jetsOff"
        command "heaterOn"
        command "heaterOff"
        command "heaterHeating"
        
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale:2) {  
    
    def tempColors = [
            		[value: 31, color: "#153591"],
            		[value: 44, color: "#1e9cbb"],
            		[value: 59, color: "#90d2a7"],
            		[value: 74, color: "#44b621"],
            		[value: 84, color: "#f1d801"],
            		[value: 95, color: "#d04e00"],
            	
        		]
    
     	standardTile("Spa Mode", "spaMode", width: 6, height: 1, canChangeIcon: true) {
    		state "spaModeOff", label: 'Spa Mode', action: "spaModeOn", icon: "https://www.dropbox.com/s/u8v9tlqwterv104/hottub.png?dl=1", backgroundColor: "#ffffff"
    		state "spaModeOn", label: 'Spa Mode', action: "spaModeOff", icon: "https://www.dropbox.com/s/u8v9tlqwterv104/hottub.png?dl=1", backgroundColor: "#00a0dc"
		}
         
        standardTile("Spa Temperature", "spaTemp", width: 4, height: 4, decoration: "flat") {
        		state "spaModeOn", label:'  ${currentValue}',icon:"st.Health & Wellness.health2",  backgroundColor: "#d04e00", defaultState: true
                state "spaModeOff", label:'Spa Off'
    	}
    	
        valueTile("Outside Temperature", "outsideTemp", width: 2, height: 2) {
    		state("val", label:' \r\n ${currentValue}\r\n     ', unit:"dF", icon:"st.Weather.weather11",
        		backgroundColors: tempColors
    		)
		}
        
        standardTile("heater", "heater", width: 2, height: 2, inactiveLabel: true,  decoration: "flat") {
			state "heaterHeating", label:"", icon:"st.thermostat.heating"
            state "heaterOn", label:"", icon:"st.thermostat.heat"
            state "heaterOff", label:"", icon:"st.thermostat.heating-cooling-off"
            
           
		}        
        
        standardTile("tempDown", "device.tempDown", width: 2, height: 2,  decoration: "flat") {
			state "default", label:"", icon:"st.thermostat.thermostat-down", action:"setPointDown"
		}

		valueTile("setpoint", "setpoint", width: 2, height: 2,  decoration: "flat") {
			state "val", label:'Target \r\n${currentValue}'
		}
        
        standardTile("tempUp", "device.tempUp", width: 2, height: 2,  decoration: "flat") {
			state "default", label:"", icon:"st.thermostat.thermostat-up", action:"setPointUp"
		}
       	
        standardTile("Jets", "jets", width: 2, height: 2) {
    		state "jetsOff", label: 'Jets', action: "jetsOn", icon: "st.Appliances.appliances2", backgroundColor: "#ffffff"
    		state "jetsOn", label: 'Jets', action: "jetsOff", icon: "st.Appliances.appliances2", backgroundColor: "#00a0dc"
		}
        
        standardTile("Light", "light", width: 2, height: 2) {
    		state "lightOff", label: 'Light', action: "lightOn", icon: "st.illuminance.illuminance.light", backgroundColor: "#ffffff"
    		state "lightOn", label: 'Light', action: "lightOff", icon: "st.illuminance.illuminance.light", backgroundColor: "#00a0dc"
		}
        
        standardTile("refresh", "capability.refresh", width: 2, height: 2,  decoration: "flat") {
			state "default", action:"refresh", icon:"st.secondary.refresh"
		}
		// TODO: define your main and details tiles here
	}
}

def installed() {
	update();
}

def updated() {
	runIn(2, update)
}

def update() {	
	unschedule()	
	runEvery1Minute(refresh)	
	runIn(2, refresh)
}

def jetsOff(){
	log.debug("jets off")
    def message = parent.execCommand("aux_4", 0)
}

def jetsOn(){
	def message = parent.execCommand("aux_4", 1)
}

def lightOn(){
	log.debug("light on")
    def message = parent.execCommand("aux_5", 1)
    //sendEvent(name: "deviceError", value: message)
    refresh();
}

def lightOff(){
	log.debug("light off")
    def message = parent.execCommand("aux_5", 0)
    //sendEvent(name: "deviceError", value: message)
    refresh();
}

def spaModeOn(){
	log.debug("spa mode on");
    def message = parent.execCommand("spa_pump", 1)
     message = parent.execCommand("spa_heater", 1)
    refresh();
    //sendEvent(name: "spaTemp", value: "97°")    
	//sendEvent(name: "status", value: "spaModeOn", isStateChange: true)
    //sendEvent(name: "spaTemp", value: "spaModeOn", isStateChange: true)
	//sendEvent(name: "spaTemp", value: "95°")  
}

def spaModeOff(){
	log.debug("spa mode off");
    def message = parent.execCommand("spa_pump", 0)
     message = parent.execCommand("spa_heater", 0)
    refresh();
    //sendEvent(name: "status", value: "spaModeOff", isStateChange: true)
    //sendEvent(name: "spaTemp", value: "spaModeOff", isStateChange: true)
}

def poll() {
	log.debug "Executing 'poll'"
	refresh();
}

def setPointUp(){	
	def newSetPoint = (state.setpoint.toInteger()+1);
    log.debug("Increasing setpoint to: " + newSetPoint)
	setSetPoint(newSetPoint)
}

def setPointDown(){	
	def newSetPoint = (state.setpoint.toInteger()-1);
    log.debug("Decreasing setpoint to: " + newSetPoint)
	setSetPoint(newSetPoint)
}

def setSetPoint(newTemp){
	if (newTemp > 104)
    	return
    
    state.setpoint = newTemp;    
    sendEvent(name: "setpoint", value: newTemp)
    
    unschedule(); 
    runIn(1,setTempHandler)
   
}

def setTempHandler(){
	log.debug("setting temp to: " + state.setpoint);
	parent.setSpaTemp(state.setpoint);
    refresh()
}

def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command
    
	def allDevices = parent.getDeviceStates();
    def spaTemp = allDevices["spa_temp"];
    def poolTemp = allDevices["pool_temp"];

    def spaSetPoint = allDevices["spa_set_point"];
    def spaPump = allDevices["spa_pump"];
    def outsideTemp = allDevices["air_temp"]
    def spaHeater = allDevices["spa_heater"]
    def light = allDevices["aux_5"]
    def jets = allDevices["aux_4"]
    
    if (poolTemp != null && poolTemp != ""){
    	sendEvent(name: "spaTemp", value: poolTemp + "°")         
    }
    
    if (spaTemp != null && spaTemp != ""){
    	sendEvent(name: "spaTemp", value: spaTemp +"°")  
    } 
    
    if (jets.toInteger() == 0){
    	sendEvent(name: "jets", value: "jetsOff")
    }else{
    	sendEvent(name: "jets", value: "jetsOn")
    }
    
    if (light.toInteger() == 0){
    	sendEvent(name: "light", value: "lightOff")
    }else{
    	sendEvent(name: "light", value: "lightOn")
    }
    
    if (spaHeater.toInteger() == 0){
    	sendEvent(name: "heater", value: "heaterOff")
    } else if (spaHeater.toInteger() == 3){
    	sendEvent(name: "heater", value: "heaterOn")
    } else {
    	sendEvent(name: "heater", value: "heaterHeating")
    }
    
    if (spaPump.toInteger() == 0){
    	log.debug("here")
    	sendEvent(name: "spaMode", value: "spaModeOff")
        sendEvent(name: "spaTemp", value: "spaModeOff", isStateChange: true)
        
    }else{
    	sendEvent(name: "spaMode", value: "spaModeOn")
    }
    
  
    
    log.debug "setpoint" + spaSetPoint;
    state.setpoint = spaSetPoint;
    sendEvent(name: "setpoint", value: spaSetPoint)
    sendEvent(name: "outsideTemp", value: outsideTemp)
    //sendEvent(name: "status", value: "spaModeOn", isStateChange: true)
    //sendEvent(name: "spaTemp", value: "42", isStateChange: true)
    
    
    }