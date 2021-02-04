/********
 /********DreamScreen Control
 /***************************
 /*
 */

def driverVer() {
	return "1.3.9.1"
}
import groovy.json.JsonOutput
metadata {
	definition (name: "Dreamscreen Control Test2",
			namespace: "Brock Ondayko",
			author: "Brock Ondayko") {
		capability "Telnet"
		command "on"
		command "off"

		command "setMode", [[
									name: "setMode",
									title: "Mode Desired",
									constraints: ["Video", "Ambient", "Music", "Sleep"],
									type: "ENUM"]]
		command "setSource", [[
									  name: "setSource",
									  title: "Set Source",
									  constraints: ["HDMI1", "HDMI2", "HDMI3"],
									  type: "ENUM"]]
   //     command "setScene", [[
	//								  name: "setScene",
	//								  title: "Set Scene",
	//								  constraints: ["random", "fireside", "twinkle", "ocean", "pride", "july4", "holiday", "pop", "enchforest"],
	//								  type: "ENUM"]]
	}
	preferences {
		input ("IP", "text", title: "Dreamscreen Ip", defaultValue: "", required: true)
		input ("logEnable", "bool", title:"Debug logging", defaultValue: false, required: true)
        input ("HDR", "bool", title:"HDR", defaultValue: false, required: true)
        input ("audioJack", "bool", title: "Music audio jack", defaultValue: false, required: true)
        input ("musicHdmi", "bool", title: "Music HDMI", defaultValue: false, required: true)
	}
}

log.debug "setMode: ${setMode}"
log.debug "setSource: ${setSource}"
log.debug "IP: ${IP}"
log.debug "logEnable: ${logEnable}"

def on() {
	buildAndSendPacket(1)
}

def off() {
    buildAndSendPacket(0)   
}

def musicHdmi() {
    if (musicHdmi) {
       byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x21, 0x00, 0x5f]
       String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       def myHubAction = new hubitat.device.HubAction(stringBytes,
                         hubitat.device.Protocol.LAN, 
                         [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                          destinationAddress: "${IP}:8888",
                         encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       log.debug "string: ${stringBytes}"
	   sendHubCommand(myHubAction) 
    }
}

def audioJack() {
    if (audioJack) {
       byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x21, 0x01, 0x58]
       String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       def myHubAction = new hubitat.device.HubAction(stringBytes,
                         hubitat.device.Protocol.LAN, 
                         [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                          destinationAddress: "${IP}:8888",
                         encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       log.debug "string: ${stringBytes}"
	   sendHubCommand(myHubAction) 
          
    }
}

def HDR() {
    if (HDR) {
       byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x60, 0x01, 0x16]
       String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       def myHubAction = new hubitat.device.HubAction(stringBytes,
                         hubitat.device.Protocol.LAN, 
                         [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                          destinationAddress: "${IP}:8888",
                         encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       log.debug "string: ${stringBytes}"
	   sendHubCommand(myHubAction)
    } else {
       byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x60, 0x00, 0x11]
       String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       def myHubAction = new hubitat.device.HubAction(stringBytes,
                         hubitat.device.Protocol.LAN, 
                         [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                          destinationAddress: "${IP}:8888",
                         encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       log.debug "string: ${stringBytes}"
	   sendHubCommand(myHubAction)       
    }
}

//Set the mode of the DreamScreen
def setMode(String setMode) {
	if (logEnable) {
		log.debug "setMode: ${setMode}"
	}
	def payload
	switch (setMode.toString()) {
		case "Sleep":
			(payload = 0)
			break
		case "Video":
			(payload = 1)
			break
		case "Ambient":
			(payload = 2) 
			break
		case "Music":
			(payload = 3)
			break
		default:
			payload = 0
	}
	buildAndSendPacket(payload)
}

//Set the source of the DreamScreen
def setSource(String setSource) {
		if (logEnable) {
		log.debug "setSource in method: ${setSource}"
	}
	switch (setSource) {
		case "HDMI1":
			(payload = 4)
			break
		case "HDMI2":
			(payload = 5)
			break
		case "HDMI3":
			(payload = 6)
			break
		default:
			payload = 4
	}
	buildAndSendPacket(payload)
}

def setScene(String setScene) {
    if (logEnable) {
        log.debug "setScene in method: ${setScene}"
    }
    switch (setScene) {
        case "random":
            payload = 7
            break
        case "fireside":
            payload = 8
            break
        case "twinkle":
            payload = 9
            break
        case "ocean":
            payload = 10
            break
        case "pride":
            payload = 11
            break
        case "july4":
            payload = 12
            break
        case "holiday":
            payload = 13
            break
        case "pop":
            payload = 14
            break
        case "enchforest":
            payload = 15
            break
        default:
            payload = 7
    }
    buildAndSendPacket(payload)
}

def buildAndSendPacket(int payload){
	switch(payload) {
		case 0: //Off
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x01, 0x00, 0x0d ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                               destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 1: //Video
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x01, 0x01, 0x0a ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 2: //Ambient
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x01, 0x03, 0x04 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 3: //Music
        	byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x01, 0x02, 0x03 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 4: //HDMI1
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x20, 0x00, 0xb6 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 5: //HDMI2
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x20, 0x01, 0xb1 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
		case 6: //HDMI3
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x11, 0x03, 0x20, 0x02, 0xb8 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
        case 7: //Random  Colors scene
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x00, 0x0d ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
         case 8: //Fireside
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x01,0x0a ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
         case 9: //Twinkle
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x02,0x03 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
         case 10: //Ocean
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x03, 0x04 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
         case 11: //Pride
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x04, 0x11 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
			break
        case 12: //July4
            byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x05, 0x16 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)
            break
        case 13: //holiday
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x06, 0x1f ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)            
            break
        case 14: //pop
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x07, 0x18 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)            
            break
        case 15: //enchForest
			byte[] rawBytes = [0xfc, 0x06, 0x00, 0x41, 0x03, 0x0d, 0x08, 0x35 ]
    		String stringBytes = hubitat.helper.HexUtils.byteArrayToHexString(rawBytes)
       		def myHubAction = new hubitat.device.HubAction(stringBytes,
                           	  hubitat.device.Protocol.LAN, 
                           	  [type: hubitat.device.HubAction.Type.LAN_TYPE_UDPCLIENT, 
                              destinationAddress: "${IP}:8888",
                              encoding: hubitat.device.HubAction.Encoding.HEX_STRING])
       		log.debug "string: ${stringBytes}"
			sendHubCommand(myHubAction)            
            break   
	}
}


//void httpPost(uri: "http:// ${IP}/ ${resp}")

// def calcCRC8(resp){
// 		def crcTable = [
// 		0, 7, 4, 9, 28, 27, 18, 21,
// 		56, 63, 54, 49, 36, 35, 42,
// 		45, 112, 119, 126, 121, 108,
// 		107, 98, 101, 72, 79, 70, 65,
// 		84, 83, 90, 93, 224, 231, 238,
// 		233, 252, 251, 242, 245, 216,
// 		223, 214, 209, 196, 195, 202,
// 		205, 144, 151, 158, 153, 140,
// 		139, 130, 133, 168, 175, 166,
// 		161, 180, 179, 186, 189, 199,
// 		192, 201, 206, 219, 220, 213,
// 		210, 255, 248, 241, 246, 227,
// 		228, 237, 234, 183, 176, 185,
// 		190, 171, 172, 165, 162, 143,
// 		136, 129, 134, 147, 148, 157,
// 		154, 39, 32, 41, 46, 59, 60,
// 		53, 50, 31, 24, 17, 22, 3,
// 		4, 13, 10, 87, 80, 89, 94,
// 		75, 76, 69, 66, 111, 104,
// 		97, 102, 115, 116, 125, 122,
// 		137, 142, 135, 0, 149, 146,
// 		155, 156, 177, 182, 191, 184,
// 		173, 170, 163, 164, 249, 254,
// 		247, 240, 229, 226, 235, 236,
// 		193, 198, 207, 200, 221, 218,
// 		211, 212, 105, 110, 103, 96,
// 		117, 114, 123, 124, 81, 86,
// 		95, 88, 77, 74, 67, 68, 25,
// 		30, 23, 16, 5, 2, 11, 12, 33,
// 		38, 47, 40, 61, 58, 51, 52, 78,
// 		73, 64, 71, 82, 85, 92, 91, 118,
// 		113, 120, 255, 106, 109, 100,
// 		99, 62, 57, 48, 55, 34, 37,
// 		44, 43, 6, 1, 8, 15, 26,
// 		29, 20, 19, 174, 169, 160,
// 		167, 178, 181, 188, 187, 150,
// 		145, 152, 159, 138, 141, 132,
// 		131, 222, 217, 208, 215, 194,
// 		197, 204, 203, 230, 225, 232,
// 		239, 250, 253, 244, 243
// ] // crcTable is grabbed from a PDF from DreamScreen that describes UDP process
//     def size = resp[1] + 1
//     def crc = 0
// 	log.debug "crcTable: ${crcTable}"
// 	try {
//     	for (int i : size) {
//         	crc = crcTable[(resp[i] ^ crc) & 255]
//             //#print crc #just in for verification
// 		}
// 	} catch (Exception ex) {
// 		log.debug "Error in calcCRC8: ${ex}"
// 	}
// 	log.debug "crc: ${crc}"
//     return crc
// }



