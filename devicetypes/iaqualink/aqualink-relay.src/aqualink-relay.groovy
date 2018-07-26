/**
 *  Pool Relay Device Handler
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
 */
metadata {
	definition (name: "Aqualink Relay", namespace: "iAqualink", author: "Chris Kooken") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale:2) {
  
    
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				//attributeState "on", label:'${name}', action:"switch.off", icon:"st.Outdoor.outdoor16", backgroundColor:"#00a0dc", nextState:"waiting"
				//attributeState "off", label:'${name}', action:"switch.on", icon:"st.Outdoor.outdoor16", backgroundColor:"#ffffff", nextState:"waiting"
				//attributeState "waiting", label:'${name}', action:"switch.on", icon:"st.Outdoor.outdoor16", backgroundColor:"#15EE10", nextState:"waiting"
				//attributeState "commsError", label: 'Comms Error', action:"switch.on", icon:"st.Outdoor.outdoor16", backgroundColor:"#e86d13", nextState:"waiting"
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://www.dropbox.com/s/wtiwzgfeu4rvzf5/pump.png?dl=1", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"https://www.dropbox.com/s/wtiwzgfeu4rvzf5/pump.png?dl=1", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'Turning On', action:"switch.on", icon:"https://www.dropbox.com/s/wtiwzgfeu4rvzf5/pump.png?dl=1", backgroundColor:"#15EE10", nextState:"turningOn"
                attributeState "turningOff", label:'Turning Off', action:"switch.on", icon:"https://www.dropbox.com/s/wtiwzgfeu4rvzf5/pump.png?dl=1", backgroundColor:"#15EE10", nextState:"turningOff"
				attributeState "commsError", label: 'Comms Error', action:"switch.on", icon:"https://www.dropbox.com/s/wtiwzgfeu4rvzf5/pump.png?dl=1", backgroundColor:"#e86d13", nextState:"waiting"
			}    
                
			tileAttribute ("deviceError", key: "SECONDARY_CONTROL") {
				attributeState "deviceError", label: '${currentValue}'
			}
			
		}
        

      	valueTile("temperature", "device.temperature", width: 2, height: 2) {
        		state "val", label:'${currentValue}', defaultState: true
   		}
        
		standardTile("refresh", "capability.refresh", width: 2, height: 2,  decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
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

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	refresh();
}

void uninstalled() {	
	def alias = device.label
	parent.removeChildDevice(alias, device.deviceNetworkId)
}

def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command

    def deviceId = getDataValue("deviceId");
	def allDevices = parent.getDeviceStates();
    def currentDeviceState = allDevices[deviceId];
    
    if (currentDeviceState.toInteger() == 0){
    	sendEvent(name: "switch", value: "off")
    }else{
    	sendEvent(name: "switch", value: "on")
    }
    
    def poolTemp= allDevices["pool_temp"]
    def spaTemp = allDevices["spa_temp"]
    
 
    
    
    if (poolTemp != null && poolTemp != ""){
    	sendEvent(name: "temperature", value: poolTemp+"℉")
        log.debug("pool temperature:" + poolTemp)
    }
    
    if (spaTemp != null && spaTemp != ""){
    	sendEvent(name: "temperature", value: spaTemp+"℉")
    	log.debug("spa temperature:" + spaTemp)
    }
}

def on() {
	def deviceId = getDataValue("deviceId");
	log.debug "Executing 'on'" + deviceId
	def message = parent.execCommand(deviceId, 1)
    sendEvent(name: "deviceError", value: message)
    refresh();
    
}

def off() {
    def deviceId = getDataValue("deviceId");
	log.debug "Executing 'off'" + deviceId
	def message = parent.execCommand(deviceId, 0)
     sendEvent(name: "deviceError", value: message)
	refresh();
}