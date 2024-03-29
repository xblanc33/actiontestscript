package com.ats.executor.drivers.engines.browsers;

import org.openqa.selenium.chrome.ChromeOptions;

import com.ats.driver.ApplicationProperties;
import com.ats.executor.ActionStatus;
import com.ats.executor.channels.Channel;
import com.ats.executor.drivers.DriverManager;
import com.ats.executor.drivers.DriverProcess;
import com.ats.executor.drivers.desktop.DesktopDriver;
import com.ats.executor.drivers.engines.WebDriverEngine;
import com.ats.tools.Utils;

public class ChromiumDriverEngine extends WebDriverEngine {

	public ChromiumDriverEngine(Channel channel, ActionStatus status, String name, DriverProcess driverProcess, DesktopDriver windowsDriver, ApplicationProperties props) {
		super(channel, name, driverProcess, windowsDriver, props, 100);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-default-browser-check");
		options.addArguments("--test-type");
		options.addArguments("--allow-file-access-from-files");
		options.addArguments("--allow-running-insecure-content");
		options.addArguments("--allow-file-access-from-files");
		options.addArguments("--allow-cross-origin-auth-prompt");
		options.addArguments("--allow-file-access");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-web-security");
		options.addArguments("--user-data-dir=" + Utils.createDriverFolder(DriverManager.CHROMIUM_BROWSER));
		
		if(lang != null) {
			options.addArguments("--lang=" + lang);
		}

		if(applicationPath != null) {
			options.setBinary(applicationPath);
		}

		launchDriver(status, options);
	}
}