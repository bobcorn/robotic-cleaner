//
//  AppDelegate.swift
//  iss2018
//
//  Created by Marco Boschi on 15/06/2018.
//  Copyright © 2018 Marco Boschi. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
	
	weak var mainController: ViewController!

	func applicationDidFinishLaunching(_ aNotification: Notification) {
		// Insert code here to initialize your application
	}

	func applicationWillTerminate(_ aNotification: Notification) {
		// Insert code here to tear down your application
	}

	func applicationShouldTerminateAfterLastWindowClosed(_ sender: NSApplication) -> Bool {
		mainController.disconnect()
		return true
	}
	
}

