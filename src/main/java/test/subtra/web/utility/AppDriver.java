package test.subtra.web.utility;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.opencsv.CSVReader;

import test.subtra.web.constants.TestConstants;
import test.subtra.web.customexceptions.MyCoreExceptions;

public class AppDriver extends TestListenerAdapter {

	protected static Logger logger = LoggerFactory.getLogger(AppDriver.class.getName());

	private final static String SKIP_EXCEPTION_MESSAGE = "Expected skip.";
	protected ExtentReports extent;
	public ExtentTest test;

	BaseDriverHelper baseDriverHelper = new BaseDriverHelper();
	CommonUtils utils = new CommonUtils();
	Set<String> skippedMethods = new HashSet<String>();

	public ExtentSparkReporter spark;
	public String reportDestinationPath;

	private long startTime;
	private long stopTime;

	public long getStopTime() {
		return this.stopTime;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public WebDriver getDriver() {
		try {
			logger.info("Checking driver..");
			if (baseDriverHelper.getDriver() == null) {
				// baseDriverHelper.startServer();
				baseDriverHelper.startDriver();
			} else {
				logger.info("Driver already running..");
			}
		} catch (Exception e) {
			logger.info("Checking driver exception..");
			e.printStackTrace();
		}
		return baseDriverHelper.getDriver();
	}

	public boolean hasDriver() {
		return baseDriverHelper.getDriver() == null ? false : true;
	}

	public String getPrimaryWinhandle() throws MyCoreExceptions {
		return baseDriverHelper.getPrimaryWinhandle();
	}

	public Logger getLogger() {
		return logger;
	}

	public Logger getLogger(Class<?> className) {
		Logger newLogger = baseDriverHelper.getLogger(className);
		if (newLogger != null)
			return newLogger;
		else {
			logger.warn("Logger initialization with class name provided failed. Returning default logger");
			return logger;
		}
	}

	public String getBrowserName() {
		return getDriver() != null ? ((RemoteWebDriver) getDriver()).getCapabilities().getBrowserName() : null;
	}

	@SuppressWarnings("resource")
	@DataProvider(name = "GenericDataProvider")
	public Object[][] genericDataProvider(Method methodName) throws IOException {
		logger.info("Method Name :" + methodName.getName());
		Reader reader = new FileReader("./resources/testdata/" + methodName.getName() + ".csv");
		List<String[]> scenarioData = new CSVReader(reader).readAll();
		Object[][] data = new Object[scenarioData.size() - 1][1];
		Iterator<String[]> it = scenarioData.iterator();
		String[] header = it.next();
		int CSV_cnt = 0;
		while (it.hasNext()) {
			HashMap<String, String> hashItem = new HashMap<String, String>();
			String[] line = it.next();
			for (int i = 0; i < line.length; i++)
				hashItem.put(header[i], line[i]);
			data[CSV_cnt][0] = hashItem;
			CSV_cnt++;
		}
		return data;
	}

	@Override
	public void onStart(ITestContext context) {
		logger.info("Executing the Test in XML: " + context.getName());
		reportDestinationPath = TestConstants.REPORTS_DIRECTORY + "/" + System.currentTimeMillis() + "/";

		spark = new ExtentSparkReporter(reportDestinationPath);
		spark.config().setTheme(Theme.DARK);

		extent = new ExtentReports();
		extent.attachReporter(spark);
	}

	@Override
	public void onFinish(ITestContext context) {
		logger.info("Execution of the Test completed in XML: " + context.getName());
		stopTime = System.currentTimeMillis();
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		test = extent.createTest(testName);

		ITestNGMethod testMethod = result.getMethod();

		String testMName = result.getMethod().getMethodName() + " - " + result.getTestClass().getName();

		logger.info("Starting the test : " + testMName);
		logger.info("Groups Depends on : " + testMethod.getGroupsDependedUpon() + "\n Methods Depends on : "
				+ testMethod.getMethodsDependedUpon());

	}

	@Override
	public void onTestFailure(ITestResult testResult) {
		try {
			String testName = testResult.getMethod().getMethodName();
			logger.info("Test : " + testResult.getName() + "' FAILED");
			if (!(testResult.getThrowable() instanceof NoSuchWindowException
					|| testResult.getThrowable() instanceof NoSuchFrameException)) {
				String imageName = processResults(testResult, true);
				test.fail("Test : " + testName + "' FAILED. " + testResult.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(imageName).build());
				extent.flush();
			}
			logger.info("Exception Throwed on TestFailure : " + testResult.getThrowable().getMessage());

		} catch (MyCoreExceptions | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSuccess(ITestResult testResult) {
		try {
			String testName = testResult.getName();
			processResults(testResult, false);
			test.pass("Test : " + testName + "' PASSED.");
			extent.flush();
			logger.info("Test : " + testResult.getMethod().getMethodName() + " - " + testResult.getTestClass().getName()
					+ "' PASSED");
		} catch (MyCoreExceptions e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestSkipped(ITestResult testResult) {
		try {
			logger.info("Test : " + testResult.getName() + "' SKIPPED");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String processResults(ITestResult testResult, boolean takeScreenShot) throws MyCoreExceptions {
		Map<String, WebDriver> drivers = getDriverfromResult(testResult);
		String imageName = null;
		for (String driverType : drivers.keySet()) {
			long threadId = Thread.currentThread().getId();
			if (takeScreenShot) {
				utils.captureBrowserScreenShot(testResult.getName(), drivers.get(driverType));
				imageName = "FullSS_" + testResult.getName() + "_thread" + threadId + ".png";
				utils.captureFullBrowserScreenShotForExtent(reportDestinationPath + "/" + imageName,
						drivers.get(driverType));
			}

		}
		return imageName;

	}

	public Map<String, WebDriver> getDriverfromResult(ITestResult testResult) {
		Map<String, WebDriver> driverList = new HashMap<String, WebDriver>();
		AppDriver appDriver = getAppDriver(testResult);
		if (appDriver != null) {
			if (appDriver.hasDriver())
				driverList.put("primary", appDriver.getDriver());
		}
		return driverList;
	}

	protected AppDriver getAppDriver(ITestResult testResult) {
		Object currentClass = testResult.getInstance();
		if (currentClass instanceof AppDriver)
			return ((AppDriver) currentClass);
		else
			return null;
	}

	public void skipTest(String message) {
		throw new SkipException(SKIP_EXCEPTION_MESSAGE + message);
	}

	public void skipTest() {
		skipTest(" Note: No additional skip message was provided.\n");
	}

	protected boolean isExpectedSkip(ITestResult testResult) {
		Throwable thr = testResult.getThrowable();
		boolean flag = false;
		if (thr.getMessage().startsWith(SKIP_EXCEPTION_MESSAGE)) {
			flag = true;
		} else {
			for (String methodDependentUpon : testResult.getMethod().getMethodsDependedUpon()) {
				if (skippedMethods.contains(methodDependentUpon)) {
					flag = true;
					break;
				}
			}
		}
		if (flag) {
			String className = testResult.getMethod().getConstructorOrMethod().getMethod().getDeclaringClass()
					.getName();
			skippedMethods.add(className + "." + testResult.getMethod().getMethodName());
		}
		return flag;
	}

	@AfterClass
	public void afterMethod() {
		logger.info("Stopping BaseDrivers");
		baseDriverHelper.stopDriver();
		baseDriverHelper.stopServer();
	}

	public void stopDriver() {
		logger.info("Stopping driver..");
		baseDriverHelper.stopDriver();
	}

	protected void stopPrimaryDriver() {
		baseDriverHelper.stopPrimaryDriver();
	}

	public void setDriver(WebDriver driver) {
		baseDriverHelper.setDriver(driver);
	}
}