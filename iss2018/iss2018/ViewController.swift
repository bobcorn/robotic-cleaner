//
//  ViewController.swift
//  iss2018
//
//  Created by Marco Boschi on 15/06/2018.
//  Copyright © 2018 Marco Boschi. All rights reserved.
//

import Cocoa
import MBLibrary
import CocoaMQTT

class ViewController: NSViewController {
	
	@IBOutlet weak var temperatureField: NSTextField!
	@IBOutlet weak var timeField: NSTextField!
	@IBOutlet weak var colorPicker: NSColorWell!
	
	@IBOutlet weak var lamp: NSView!
	@IBOutlet weak var offLabel: NSTextField!
	@IBOutlet weak var toggleButton: NSButton!
	
	private var isOn = true
	private let temperatureFormatter = NumberFormatter()
	
	private var mqtt: CocoaMQTT!
	private let mqttTopic = "unibo/qasys"
	private let eventName = "sensorEvent"
	private let eventPayloadName = "sensorEvent"
	
	private var firstDropped = false
	private var msgQaId = 1

	override func viewDidLoad() {
		super.viewDidLoad()
		
		(NSApplication.shared.delegate as? AppDelegate)?.mainController = self

		temperatureField.resignFirstResponder()
		timeField.resignFirstResponder()
		
		temperatureFormatter.alwaysShowsDecimalSeparator = true
		temperatureFormatter.localizesFormat = false
		temperatureFormatter.hasThousandSeparators = false
		temperatureFormatter.decimalSeparator = "."
		temperatureFormatter.minimumFractionDigits = 1
		
		lamp.wantsLayer = true
		lamp.layer?.borderColor = NSColor.darkGray.cgColor
		lamp.layer?.borderWidth = 1
		lamp.layer?.masksToBounds = true
		toggleLamp(self)
		
		initMqtt()
	}
	
	private func initMqtt() {
		let clientID = "CocoaMQTT-iss2018-" + String(ProcessInfo().processIdentifier)
		mqtt = CocoaMQTT(clientID: clientID, host: "127.0.0.1", port: 1883)
		mqtt.username = ""
		mqtt.password = ""
		mqtt.willMessage = CocoaMQTTWill(topic: "unibo/clienterrors", message: "error")
		mqtt.keepAlive = 480
		mqtt.delegate = self
		mqtt.connect()
	}
	
	override func viewDidLayout() {
		super.viewDidLayout()
		
		lamp.layer?.cornerRadius = lamp.frame.width / 2
	}

	@IBAction func sendTemperature(_ sender: AnyObject) {
		guard let temp = temperatureFormatter.number(from: temperatureField.stringValue), let tempStr = temperatureFormatter.string(from: temp) else {
			temperatureField.shake()
			temperatureField.becomeFirstResponder()
			return
		}
		
		print("New temperature: \(tempStr)")
		temperatureField.resignFirstResponder()
		sendUpdate(sensor: "thermometer", payload: tempStr)
	}

	@IBAction func sendTime(_ sender: AnyObject) {
		let time = timeField.stringValue.trimmingCharacters(in: .whitespacesAndNewlines)
		let regex = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$"
		let parts = time.split(separator: ":").compactMap({ Int($0) })
		guard time.range(of: regex, options: .regularExpression) != nil, parts.count == 3, !parts[1...2].contains(where: { $0 < 0 || $0 > 59 }), parts[0] >= 0 && parts[0] <= 23 else {
			timeField.shake()
			timeField.becomeFirstResponder()
			return
		}
		
		let rawTime = parts[2] + 60*parts[1] + 3600*parts[0]
		print("New time: \(rawTime)")
		timeField.resignFirstResponder()
		sendUpdate(sensor: "clock", payload: "\(rawTime)")
	}
	
	@IBAction func updateColor(_ sender: AnyObject) {
		guard isOn else {
			return
		}
		
		lamp.layer?.backgroundColor = colorPicker.color.cgColor
	}
	
	@IBAction func toggleLamp(_ sender: AnyObject) {
		isOn = !isOn
		if isOn {
			setOn()
		} else {
			setOff()
		}
	}
	
	private func setOn() {
		toggleButton.title = "Off"
		lamp.layer?.backgroundColor = colorPicker.color.cgColor
		offLabel.isHidden = true
	}
	
	private func setOff() {
		toggleButton.title = "On"
		lamp.layer?.backgroundColor = NSColor.clear.cgColor
		offLabel.isHidden = false
	}

}

extension ViewController: CocoaMQTTDelegate {
	
	func disconnect() {
		mqtt.disconnect()
	}
	
	private func sendUpdate(sensor: String, payload: String) {
		let msg = "msg(\(eventName),event,hw_mock,null,\(eventPayloadName)(\(sensor), \(payload)),\(msgQaId))"
		mqtt.publish(mqttTopic, withString: msg)
		msgQaId += 1
	}
	
	func mqtt(_ mqtt: CocoaMQTT, didConnectAck ack: CocoaMQTTConnAck) {
		print("Connected to MQTT server")
		self.mqtt.subscribe(mqttTopic)
	}
	
	func mqtt(_ mqtt: CocoaMQTT, didPublishMessage message: CocoaMQTTMessage, id: UInt16) {}
	
	func mqtt(_ mqtt: CocoaMQTT, didPublishAck id: UInt16) {}
	
	func mqtt(_ mqtt: CocoaMQTT, didReceiveMessage message: CocoaMQTTMessage, id: UInt16) {
		guard firstDropped else {
			firstDropped = true
			return
		}
		
		guard message.topic == mqttTopic, let rawPayload = message.string, rawPayload.hasPrefix("msg("), rawPayload.hasSuffix(")") else {
			return
		}
		
		let parts = rawPayload[4...][..<(-1)].split(separator: ",")
		guard parts.count >= 6, parts[0] == "ctrlEvent" else {
			return
		}
		let payload = parts[4...(parts.count - 2)].joined(separator: ",")
		guard payload.hasPrefix("ctrlEvent("), payload.hasSuffix(")") else {
			return
		}
		let cmdInfo = payload[10...][..<(-1)].split(separator: ",").map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
		guard cmdInfo.count == 2 else {
			return
		}
		
		print("Received command targetting '\(cmdInfo[0])' with payload '\(cmdInfo[1])'")
		switch cmdInfo[0] {
		case "hueLamp":
			switch cmdInfo[1] {
			case "off":
				setOff()
			case "on":
				setOn()
			default:
				print("Unknown payload '\(cmdInfo[1])' for target '\(cmdInfo[0])'")
			}
		default:
			print("Unknown target '\(cmdInfo[0])', payload '\(cmdInfo[1])'")
		}
	}
	
	func mqtt(_ mqtt: CocoaMQTT, didSubscribeTopic topic: String) {
		print("Subscribed to '\(topic)'")
	}
	
	func mqtt(_ mqtt: CocoaMQTT, didUnsubscribeTopic topic: String) {}
	
	func mqttDidPing(_ mqtt: CocoaMQTT) {}
	
	func mqttDidReceivePong(_ mqtt: CocoaMQTT) {}
	
	func mqttDidDisconnect(_ mqtt: CocoaMQTT, withError err: Error?) {
		print("Disconnected from MQTT server. Error: \(err?.localizedDescription ?? "none")")
	}
	
}
