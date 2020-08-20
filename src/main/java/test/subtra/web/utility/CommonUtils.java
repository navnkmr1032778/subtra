package test.subtra.web.utility;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import test.subtra.web.constants.WebDriverConstants;

public class CommonUtils {

	Boolean driverFilefound = false;

	protected static Logger logger = LoggerFactory.getLogger(CommonUtils.class.getName());

	public String getCurrentWorkingDirectory() {
		String workingDir = null;
		try {
			workingDir = System.getProperty("user.dir");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workingDir;
	}

	public String getTestDataFullDirPath(String fileName) {
		String path = WebDriverConstants.PATH_TO_TEST_DATA_FILE;
		if (OSCheck.getOperatingSystemType() == OSCheck.OSType.Windows)
			path = WebDriverConstants.WINDOWS_PATH_TO_TEST_DATA_DIR;
		return (getCurrentWorkingDirectory() + path + fileName);
	}

	public File getBrowserExecutable(String path, String fileName) {
		try {
			File fileDirectory = new File(path);
			if (!fileDirectory.exists()) {
				fileDirectory.mkdirs();
			}

			File[] listOfFiles = fileDirectory.listFiles();
			if (listOfFiles != null && listOfFiles.length != 0) {
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].getName().contains(fileName)) {
						// check windows driver file "chromedriver.exe" and for Mac "chromedriver"
						// check for file extension based on OS type and to pick the correct driver file
						// even when both drivers are available
						if (OSCheck.getOperatingSystemType() == OSCheck.OSType.Windows
								&& Files.getFileExtension(listOfFiles[i].getName()).equalsIgnoreCase("exe")) {
							listOfFiles[i].setExecutable(true);
							return listOfFiles[i];
						} else if ((OSCheck.getOperatingSystemType() == OSCheck.OSType.MacOS
								|| OSCheck.getOperatingSystemType() == OSCheck.OSType.Linux)
								&& !Files.getFileExtension(listOfFiles[i].getName()).equalsIgnoreCase("exe")) {
							listOfFiles[i].setExecutable(true);
							return listOfFiles[i];
						}
					}

				}
			}
			if (!driverFilefound) {
				logger.info("No driver file found under drivers folder. Trying to download driver executable file");
				DriverUtils.getInstance().downloadFile(fileName, OSCheck.getOperatingSystemType());
				driverFilefound = true;
				return getBrowserExecutable(path, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new File("temp file");
	}

	public void captureFullBrowserScreenShot(String imageName, WebDriver webDriver) {
		try {
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
					.takeScreenshot(webDriver);
			File dir = new File(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT);
			if (!dir.exists()) {
				try {
					dir.mkdir();
					logger.info("creating directory: " + dir);
				} catch (Exception ex) {
					logger.info("Couldn't create Directory" + ExceptionUtils.getFullStackTrace(ex));
				}
			}
			ImageIO.write(screenshot.getImage(), "PNG", new File(
					WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT + imageName + System.currentTimeMillis() + ".png"));
		} catch (Exception ex) {
			logger.info("Couldn't take Screenshot" + ExceptionUtils.getFullStackTrace(ex));
		}
	}

	public void captureFullBrowserScreenShotForExtent(String imageName, WebDriver webDriver) {
		try {
			Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.simple()).takeScreenshot(webDriver);
			ImageIO.write(screenshot.getImage(), "PNG", new File(imageName));
		} catch (Exception ex) {
			logger.info("Couldn't take Screenshot" + ExceptionUtils.getFullStackTrace(ex));
		}
	}

	public void captureBrowserScreenShot(String imageName, WebDriver webDriver) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String curDate = dateFormat.format(date);
		File dir = new File(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT);
		if (!dir.exists()) {
			try {
				dir.mkdir();
				logger.info("creating directory: " + dir);
			} catch (Exception ex) {
				logger.info("Couldn't create Directory" + ExceptionUtils.getFullStackTrace(ex));
			}
		}

		try {
			Set<String> handles = webDriver.getWindowHandles();
			String currentHandle = webDriver.getWindowHandle();
			int handleCount = 0;
			for (String handle : handles) {
				handleCount++;
				webDriver.switchTo().window(handle);
				webDriver.manage().window().maximize();
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					logger.info(ExceptionUtils.getFullStackTrace(e));
				}
				screenShot(WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT + imageName + "_handle" + handleCount + "_"
						+ curDate + "_" + System.currentTimeMillis() + ".png", webDriver);
			}
			webDriver.switchTo().window(currentHandle);
		} catch (Exception ex) {
			logger.info("exception in taking Screenshot" + ExceptionUtils.getFullStackTrace(ex));
		}
	}

	public void screenShot(String fileName, WebDriver webDriver) {
		try {
			File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(fileName));
		} catch (IOException e) {
			logger.info("Error While taking Screen Shot");
			e.printStackTrace();
		}
	}

	public boolean isFileExists(String fileName) {
		return new File(fileName).exists();
	}

}
