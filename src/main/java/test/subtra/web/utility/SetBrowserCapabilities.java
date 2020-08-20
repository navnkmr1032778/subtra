package test.subtra.web.utility;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.subtra.web.constants.WebDriverConstants;

public class SetBrowserCapabilities {

	CommonUtils utils = new CommonUtils();
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean isGridRun() {
		return Boolean.valueOf(System.getProperty("grid", "false").toLowerCase(Locale.ENGLISH));
	}

	public String getBrowserToRun() {
		return System.getProperty("gridbrowser", System.getProperty("webdriver.browser", "chrome"))
				.toLowerCase(Locale.ENGLISH);
	}

	public ChromeOptions setChromeDriver() {
		ChromeOptions cap = new ChromeOptions();
		try {
			String workingDir = utils.getCurrentWorkingDirectory();
			if (workingDir == null) {
				logger.info("Working directory is Null");
				return null;
			}

			if (!isGridRun()) {
				File chromeDriver = utils
						.getBrowserExecutable((workingDir + WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE), "chrome");

				if (chromeDriver.getName().equals("tempfile")) {
					logger.warn("Unable to find executable file");
					return null;
				}
				System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
			}

			cap = new ChromeOptions();
			LoggingPreferences loggingprefs = new LoggingPreferences();
			loggingprefs.enable(LogType.BROWSER, Level.ALL);
			cap.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
			cap.addArguments("--disable-notifications");
			cap.addArguments("--disable-extensions");
			cap.addArguments("disable-infobars");
//			cap.addArguments("--incognito");

			cap.addArguments("--session-override=true");
			String windowSize = System.getProperty("windowSize", "");

			if (windowSize.matches("^\\d+,\\d+$")) {
				cap.addArguments("--window-size=" + windowSize);
			}
//			Proxy proxy = new Proxy();
//			proxy.setHttpProxy("localhost:8889");
//			proxy.setSslProxy("localhost:8889");
//			cap.setProxy(proxy);
//			cap.setCapability("proxy", proxy);
			cap.setCapability(CapabilityType.BROWSER_NAME, "chrome");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cap;
	}

	public FirefoxOptions setFirefoxDriver() {
		FirefoxOptions cap = new FirefoxOptions();
		try {

			String workingDir = utils.getCurrentWorkingDirectory();
			if (workingDir == null) {
				logger.info("Working directory is Null");
				return null;
			}

			if (!isGridRun()) {
				File geckoDriver = utils
						.getBrowserExecutable((workingDir + WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE), "gecko");

				if (geckoDriver.getName().equals("tempfile")) {
					logger.warn("Unable to find executable file");
					return null;
				}
				System.setProperty("webdriver.gecko.driver", geckoDriver.getAbsolutePath());
			}

			LoggingPreferences loggingprefs = new LoggingPreferences();
			loggingprefs.enable(LogType.BROWSER, Level.ALL);
			cap.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);

			cap.addArguments("--disable-extensions");
			cap.addArguments("disable-infobars");
			cap.addArguments("--incognito");

			cap.addArguments("--session-override=true");
			String windowSize = System.getProperty("windowSize", "");

			if (windowSize.matches("^\\d+,\\d+$")) {
				cap.addArguments("--window-size=" + windowSize);
			}
			final FirefoxProfile profile = new FirefoxProfile();
			cap.setCapability(FirefoxDriver.PROFILE, profile);
			cap.setCapability(CapabilityType.BROWSER_NAME, "firefox");
			cap.setCapability("marionette", true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return cap;
	}

	public InternetExplorerOptions setIEDriver() {
		InternetExplorerOptions cap = new InternetExplorerOptions();
		try {
			String workingDir = utils.getCurrentWorkingDirectory();
			if (workingDir == null) {
				logger.warn("Working directory is Null ");
				return null;
			}

			if (!isGridRun()) {
				File ieDriver = utils.getBrowserExecutable((workingDir + WebDriverConstants.PATH_TO_BROWSER_EXECUTABLE),
						"IE");

				if (ieDriver.getName().equals("tempfile")) {
					logger.info("Unable to find executable file");
					return null;
				}
				System.setProperty("webdriver.ie.driver", ieDriver.getAbsolutePath());
			}
			cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			cap.setCapability(CapabilityType.BROWSER_NAME, "InternetExplorer");

		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return cap;
	}

}