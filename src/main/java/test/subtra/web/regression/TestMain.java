package test.subtra.web.regression;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;

import test.subtra.APITest.WeatherAPITest;
import test.subtra.POJO.WeatherDetails;
import test.subtra.web.constants.TestConstants;
import test.subtra.web.pom.NDTVMainPage;
import test.subtra.web.pom.NDTVWeatherpage;
import test.subtra.web.utility.CommonUtils;
import test.subtra.web.utility.TakeScreenshot;
import test.subtra.web.utility.TakeScreenshotUtils;

public class TestMain extends AppTest {

	protected static Logger logger = LoggerFactory.getLogger(TestMain.class);

	public CommonUtils utils = new CommonUtils();

	public NDTVWeatherpage ndtvWeatherPage;
	public NDTVMainPage ndtvMainPage;
	public WeatherDetails weatherFromWeb;
	public WeatherDetails weatherFromAPI;
	public WeatherAPITest weatherAPITest;

	public void takeScreenShot(String fileName) {
		WebDriver driver = getDriver();
		if (driver != null) {
			TakeScreenshot ts = new TakeScreenshotUtils(false, "", "", false);
			ts.captureScreenShot(driver, fileName);
			utils.captureFullBrowserScreenShot(fileName, driver);
		} else {
			logger.info("Couldn't take screenshot.. No driver found.");
		}

	}

	public void takeScreenShot() {
		WebDriver driver = getDriver();
		if (driver != null) {
			String fileName = null;
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			fileName = fileName + stackTraceElements[2].getMethodName() + ".png";
			TakeScreenshot ts = new TakeScreenshotUtils(false, "", "", false);
			ts.captureScreenShot(driver, fileName);
			fileName = fileName.replace(".png", "");
			utils.captureFullBrowserScreenShot(fileName, driver);
		} else {
			logger.info("Couldn't take screenshot.. No driver found.");
		}

	}

	public void loadHomePage() throws Exception {
		if (hasDriver() && ndtvMainPage != null) {
			ndtvMainPage = ndtvMainPage.loadSignUpPage();
		}
		if (!hasDriver() || ndtvMainPage == null) {
			getDriver().get(TestConstants.NDTV_MAIN_PAGE_URL);
			ndtvMainPage = new NDTVMainPage(getDriver());
		}
		getDriver().manage().window().maximize();
	}

	public void afterTest() {
		stopDriver();
	}

	public void fail(String message) {
		String additionalDetails = "test failed";
		Assert.fail(message + additionalDetails);
	}

	public void fail(String message, Exception e) {
		if (e instanceof SkipException)
			throw new SkipException(e.getMessage());
		if (e instanceof UnhandledAlertException) {
			try {
				Alert a = getDriver().switchTo().alert();
				String alertMessage = a.getText();
				a.accept();
				message += " \nMessage from unhandled alert box: \n" + alertMessage;
			} catch (NoAlertPresentException ex) {
			}
		}
		fail(message + "\nMessage from Exception:\n" + ExceptionUtils.getFullStackTrace(e));
	}

	public WeatherDetails getWeatherPOJO(double degree, int humidity) {
		return new WeatherDetails(degree, humidity);
	}

	public WeatherAPITest getWeatherAPIPOJO() {
		return new WeatherAPITest();
	}

	public void logInfo(String message) {
		logger.info(message);
	}

}
