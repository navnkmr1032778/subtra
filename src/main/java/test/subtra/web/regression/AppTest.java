package test.subtra.web.regression;

import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import test.subtra.web.utility.AppDriver;

@Listeners(AppDriver.class)
public class AppTest extends AppDriver {

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite(ITestContext ctx) {
		logger.info("XML FileName : " + ctx.getCurrentXmlTest().getSuite().getFileName());
		logger.info("Executing the Suite : " + ctx.getSuite().getName() + " Started Time : " + getStartTime());
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite(ITestContext ctx) {
		logger.info("XML FileName : " + ctx.getCurrentXmlTest().getSuite().getFileName());
		logger.info("Execution completed for the Suite : " + ctx.getSuite().getName() + " Started Time : "
				+ getStartTime());
	}
}
