package test.subtra.web.utility;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import net.lightbody.bmp.proxy.ProxyServer;
import test.subtra.web.constants.WebDriverConstants;
import test.subtra.web.customexceptions.MyCoreExceptions;

public class BaseDriverHelper {

	CommonUtils commonUtils = new CommonUtils();
	WebDriver driver = null;
	WebDriver secondaryDriver = null;
	ProxyServer proxyServer = null;
	ProxyServer proxy = null;

	Logger logger = getLogger(this.getClass());

	public void startServer() throws InterruptedException {
		if (!isGridRun()) {
			logger.info("starting server");
			if (proxyServer != null)
				return;
			// port number equals to zero starts the server in dynamic port
			proxyServer = new ProxyServer(0);
			try {
//				Map<String, String> options = new HashMap<String, String>();
//				options.put("httpProxy", "127.0.0.1" + ":" + "3000");
//				options.put("sslProxy", "127.0.0.1" + ":" + "3000");
//				proxyServer.setOptions(options);
//				proxyServer.remapHost("0.0.0.0", "127.0.0.1");
//				proxyServer.setPort(3001);				
//				proxyServer..remapHost("0.0.0.0", "127.0.0.1");
				proxyServer.start();
			} catch (Exception e) {
				String error = "Error while starting server.. " + e.getStackTrace();
				logger.error(error);
			}
		}
	}

	public void startDriver() throws Exception {
		logger.info("start driver");
		if (driver != null)
			return;
		String browserName = getBrowserToRun();

		Capabilities cap = createDriverCapabilities(browserName);

		if (cap == null)
			throw new MyCoreExceptions("Capabilities return as Null");
		if (isGridRun()) {
			driver = setRemoteWebDriver(cap);
		} else {
			driver = setWebDriver(cap);
		}
	}

	public WebDriver setWebDriver(Capabilities cap) throws Exception {
		if (WebDriverConfig.usingProxyServer())
			createProxy(cap);
		driver = startBrowser(cap);
		return driver;
	}

	private void setAdditionalCapabilities(Capabilities cap, String capabilityType, Object capabilityValue) {

		switch (cap.getClass().getSimpleName()) {
		case "ChromeOptions":
			ChromeOptions chromeOptions = (ChromeOptions) cap;
			chromeOptions.setCapability(capabilityType, capabilityValue);
			break;
		case "FirefoxOptions":
			FirefoxOptions firefoxOptions = (FirefoxOptions) cap;
			firefoxOptions.setCapability(capabilityType, capabilityValue);
			break;
		case "InternetExplorerOptions":
			InternetExplorerOptions internetExplorerOptions = (InternetExplorerOptions) cap;
			internetExplorerOptions.setCapability(capabilityType, capabilityValue);
			break;
		}

	}

	@SuppressWarnings("unused")
	private String getBrowserName(String driverTypeStr) throws MyCoreExceptions {
		String browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;
		try {
			switch (WebDriverConstants.DriverTypes.valueOf(driverTypeStr.toUpperCase())) {
			case PRIMARY:
				browserName = System.getProperty("webdriver.browser", WebDriverConstants.DEFAULT_BROWSER_NAME);
				break;
			default:
				browserName = WebDriverConstants.DEFAULT_BROWSER_NAME;
			}
			browserName = browserName.toLowerCase();
			browserName = WebDriverConstants.DRIVER_METHOD.containsKey(browserName) ? browserName
					: WebDriverConstants.DEFAULT_BROWSER_NAME;
		} catch (Exception e) {
			throw new MyCoreExceptions("Exception while assiging browserName");
		}
		return browserName;
	}

	private Capabilities createDriverCapabilities(String browserName) {
		Capabilities cap = null;
		try {
			SetBrowserCapabilities setBrowserCapabilities = new SetBrowserCapabilities();
			Method setCapabilities = setBrowserCapabilities.getClass()
					.getMethod(WebDriverConstants.DRIVER_METHOD.get(browserName), null);
			cap = (Capabilities) setCapabilities.invoke(setBrowserCapabilities);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cap;
	}

	@SuppressWarnings("deprecation")
	private WebDriver startBrowser(Capabilities cap) {
		WebDriver driver = null;
		try {
			switch (WebDriverConstants.BrowserNames.valueOf(cap.getBrowserName().replace(" ", "_").toUpperCase())) {
			case CHROME:
				driver = new ChromeDriver(cap);
				break;
			case INTERNET_EXPLORER:
				driver = new InternetExplorerDriver(cap);
				break;
			case FIREFOX:
				driver = new FirefoxDriver(cap);
				break;

			default:
				throw new IllegalArgumentException("Invalid Argument for browser name : " + cap.getBrowserName());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return driver;
	}

	private WebDriver setRemoteWebDriver(Capabilities cap) throws Exception {
		RemoteWebDriver rd = new RemoteWebDriver(new URL("http://" + getGridServerWithPort() + "/wd/hub"), cap);
		rd.setFileDetector(new LocalFileDetector());
		return rd;
	}

	private Proxy createProxyObject() {
		Proxy proxy = null;
		try {
			logger.info("-------------------------------proxy server - " + proxyServer.getPort());
			proxy = proxyServer.seleniumProxy();

			// proxy.setSslProxy("trustAllSSLCertificates");
//			proxy.setSslProxy("0.0.0.0:" + proxyServer.getPort());
//			proxy.setHttpProxy("0.0.0.0:" + proxyServer.getPort());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return proxy;
//		Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxyServer);
//		try {
//			String hostIp = Inet4Address.getLocalHost().getHostAddress();
//			seleniumProxy.setHttpProxy(hostIp + ":" + proxyServer.getPort());
//			seleniumProxy.setSslProxy(hostIp + ":" + proxyServer.getPort());
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		return seleniumProxy;
	}

	private void createProxy(Capabilities cap) {
		try {
			Proxy proxy = createProxyObject();
			logger.info(proxy.getHttpProxy() + "/n" + proxy.getSslProxy());
			if (proxy != null) {
				setAdditionalCapabilities(cap, CapabilityType.PROXY, (Object) proxy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		try {
			if (proxyServer != null)
				proxyServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopDriver() {
		if (getDriver() != null) {
			getDriver().quit();
			setDriver(null);
		}
	}

	public void stopPrimaryDriver() {
		if (getDriver() != null) {
			getDriver().quit();
			setDriver(null);
		}
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	public void setDriver(Object obj) {
		this.driver = (WebDriver) obj;
	}

	@SuppressWarnings("unused")
	private void printCapabilities(Capabilities capabilities) {
		Map<String, ?> map = capabilities.asMap();
		for (Entry<String, ?> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			logger.info("\t\tkey is " + key + "\t\tvalue is " + value);
		}
	}

	public Logger getLogger(Class<?> className) {
		Logger logger = null;
		try {
			logger = LoggerFactory.getLogger(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logger;
	}

	public String getPrimaryWinhandle() throws MyCoreExceptions {
		try {
			if (this.driver == null)
				throw new MyCoreExceptions("Unable to get the winhandle as the driver is set as null");
			return this.driver.getWindowHandle();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyCoreExceptions("Exception occured... " + e.getStackTrace());

		}
	}

	public WebDriver getDriverfromResult(ITestResult testResult) {
		Object currentClass = testResult.getInstance();
		return ((AppDriver) currentClass).getDriver();
	}

	public void ExtractChromeJSLogs(WebDriver driver) {
		logger.info("JS Errors");
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries)
			logger.info(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
	}

	public boolean isGridRun() {
		try {
			return Boolean.valueOf(System.getProperty("grid", "false").toLowerCase(Locale.ENGLISH));
		} catch (NullPointerException exp) {
			return false;
		}
	}

	public String getBrowserToRun() {
		return System.getProperty("gridbrowser", System.getProperty("webdriver.browser", "chrome"))
				.toLowerCase(Locale.ENGLISH);
	}

	public String getGridServerWithPort() {
		return System.getProperty("gridserver") + ":" + System.getProperty("gridport");
	}

	public String getGridPlatform() {
		String platform = System.getProperty("gridplatform");
		if (platform == null) {
			switch (OSCheck.getOperatingSystemType()) {
			case MacOS:
				platform = "MAC";
				break;
			case Linux:
				platform = "LINUX";
				break;
			case Other:
				platform = "ANY";
				break;
			case Windows:
				platform = "WINDOWS";
				break;
			default:
				platform = "ANY";
				break;
			}
		} else if (platform.equals("windows")) {
			platform = "WINDOWS";
		} else if (platform.equals("mac")) {
			platform = "MAC";
		} else if (platform.equals("android")) {
			platform = "ANDROID";
		}
		return platform;
	}
}