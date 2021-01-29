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
		command "disconnect"

		// command "setMode", [[
		// 							name: "setMode",
		// 							title: "Mode Desired",
		// 							constraints: ["Video", "Ambient", "Music", "Sleep"],
		// 							type: "ENUM"]]
		command "setSource", [[
									  name: "setSource",
									  title: "Set Source",
									  constraints: ["HDMI1", "HDMI2", "HDMI3"],
									  type: "ENUM"]]
	}
	preferences {
		input ("IP", "text", title: "Dreamscreen Ip", defaultValue: "", required: true)
		input ("logEnable", "bool", title:"Debug logging", defaultValue: false, required: true)
		input ("setMode", "enum", title: "setMode", options: ["Video", "Ambient", "Music", "Sleep"], required: true)
//		input ("setSource", "enum", title: "setSource", options: ["HDMI1", "HDMI2", "HDMI3"], required: true)
	}
}

log.debug "setMode: ${setMode}"
log.debug "setSource: ${setSource}"
log.debug "IP: ${IP}"
log.debug "logEnable: ${logEnable}"

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
			(payload = 3) 
			break
		case "Music":
			(payload = 2)
			break
		default:
			payload = 0
	}
	buildAndSendPacket(3, 1, payload)
}

def setSource(String setSource) {
		if (logEnable) {
		log.debug "setSource in method: ${setSource}"
	}
	switch (setSource) {
		case "HDMI1":
			(payload = 0)
			break
		case "HDMI2":
			(payload = 1)
			break
		case "HDMI3":
			(payload = 2)
			break
		default:
			payload = 0
	}
	buildAndSendPacket(3,32, payload)
}


def crcTable = [
		0, 7, 4, 9, 28, 27, 18, 21,
		56, 63, 54, 49, 36, 35, 42,
		45, 112, 119, 126, 121, 108,
		107, 98, 101, 72, 79, 70, 65,
		84, 83, 90, 93, 224, 231, 238,
		233, 252, 251, 242, 245, 216,
		223, 214, 209, 196, 195, 202,
		205, 144, 151, 158, 153, 140,
		139, 130, 133, 168, 175, 166,
		161, 180, 179, 186, 189, 199,
		192, 201, 206, 219, 220, 213,
		210, 255, 248, 241, 246, 227,
		228, 237, 234, 183, 176, 185,
		190, 171, 172, 165, 162, 143,
		136, 129, 134, 147, 148, 157,
		154, 39, 32, 41, 46, 59, 60,
		53, 50, 31, 24, 17, 22, 3,
		4, 13, 10, 87, 80, 89, 94,
		75, 76, 69, 66, 111, 104,
		97, 102, 115, 116, 125, 122,
		137, 142, 135, 0, 149, 146,
		155, 156, 177, 182, 191, 184,
		173, 170, 163, 164, 249, 254,
		247, 240, 229, 226, 235, 236,
		193, 198, 207, 200, 221, 218,
		211, 212, 105, 110, 103, 96,
		117, 114, 123, 124, 81, 86,
		95, 88, 77, 74, 67, 68, 25,
		30, 23, 16, 5, 2, 11, 12, 33,
		38, 47, 40, 61, 58, 51, 52, 78,
		73, 64, 71, 82, 85, 92, 91, 118,
		113, 120, 255, 106, 109, 100,
		99, 62, 57, 48, 55, 34, 37,
		44, 43, 6, 1, 8, 15, 26,
		29, 20, 19, 174, 169, 160,
		167, 178, 181, 188, 187, 150,
		145, 152, 159, 138, 141, 132,
		131, 222, 217, 208, 215, 194,
		197, 204, 203, 230, 225, 232,
		239, 250, 253, 244, 243
]


//no errors until this section with error beginning on for and range loop

def buildAndSendPacket(upperC, lowerC, int payload){
	def resp = [] //starts the response object
	resp.add(252) //#0xFC
	resp.add(payload + 5)
	resp.add(0) // group = 0 in python script, so just setting to 0 here
	resp.add(17)
	resp.add(upperC)
	resp.add(lowerC)
	log.debug "resp: ${resp}"
	def size = resp[1] + 1
	def crc = 0
	try {
	for (j in (resp[1] + 1)) {
		crc = crcTable[(resp[j] ^ crc) & 255]
	}
	}
	catch(Exception ex) {
		log.debug "Exception: ${ex}"
	}
	resp.add(crc)
	// resp = byte [resp]//forms response object into a byte to be sent out
	String respBytes = hubitat.helper.HexUtils.intArrayToHexString(resp, 1)
//	sock.sendto(resp, endpoint)  //Does this do anything currently? It appears no.
	def myhubAction = new hubitat.device.HubAction(respBytes, hubitat.device.Protocol.LAN, [type: HubAction.Type.LAN_TYPE_UDPCLIENT, destinationAddress: "${IP}:8888"] )
	// httpPost(uri: "${IP}:8888",path: "",body:"${resp}")
	sendHubCommand(myhubAction)

	if (logEnable) { log.debug "resp: ${resp}"}
}


//void httpPost(uri: "http:// ${IP}/ ${resp}")


// def calcCRC8(resp){
//     def size = resp[1] + 1
//     def crc = 0
//     for (i in (0, size){
//         crc = crcTable[(resp[i] ^ crc) & 255]})
//             //#print crc #just in for verification
//         return crc
// }



