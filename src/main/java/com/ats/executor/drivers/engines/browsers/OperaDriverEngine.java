/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ats.executor.drivers.engines.browsers;

import org.openqa.selenium.opera.OperaOptions;

import com.ats.driver.ApplicationProperties;
import com.ats.executor.ActionStatus;
import com.ats.executor.channels.Channel;
import com.ats.executor.drivers.DriverManager;
import com.ats.executor.drivers.DriverProcess;
import com.ats.executor.drivers.desktop.DesktopDriver;
import com.ats.executor.drivers.engines.WebDriverEngine;
import com.ats.tools.Utils;

public class OperaDriverEngine extends WebDriverEngine {

	private final static int DEFAULT_WAIT = 130;
	
	public OperaDriverEngine(Channel channel, ActionStatus status, DriverProcess driverProcess, DesktopDriver windowsDriver, ApplicationProperties props) {
		super(channel, DriverManager.OPERA_BROWSER, driverProcess, windowsDriver, props, DEFAULT_WAIT);
				
		initElementX = 20.0;
		
		OperaOptions options = new OperaOptions();
		options.setCapability("opera.log.level", "SEVERE");
		options.addArguments("test-type");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-notifications");
		options.addArguments("--no-default-browser-check");
		options.addArguments("--allow-file-access-from-files");
		options.addArguments("--disable-web-security");
		options.addArguments("--allow-running-insecure-content");
		options.addArguments("--user-data-dir=" + Utils.createDriverFolder(DriverManager.OPERA_BROWSER));
				
		if(lang != null) {
			options.addArguments("--lang=" + lang);
		}
		
		if(applicationPath != null) {
			options.setBinary(applicationPath);
		}
		
		launchDriver(status, options);
	}
}