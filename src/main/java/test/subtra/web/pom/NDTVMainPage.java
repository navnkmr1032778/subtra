package test.subtra.web.pom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import test.subtra.web.constants.TestConstants;

public class NDTVMainPage extends AppPage {

	@FindBy(xpath = "//a[@id='h_sub_menu']")
	private WebElement subMenuOpenLink;

	@FindBy(xpath = "//div[@id='subnav']//descendant::a[contains(text(), 'WEATHER')]")
	private WebElement weatherSubMenuLink;

	public NDTVMainPage(WebDriver driver) {
		super(driver);
		waitForPageLoadComplete();
	}

	public NDTVMainPage(WebDriver driver, String url) {
		super(driver);
		this.driver.get(url);
		validate();
	}

	public void validate() {
		logger.info("TITLE::" + getTitle());
	}

	public NDTVMainPage loadSignUpPage() {
		gotoURL(TestConstants.NDTV_MAIN_PAGE_URL);
		waitForPageLoadComplete();
		logger.info("NDTV Main Page Load Completed.");
		return new NDTVMainPage(driver);
	}

	public NDTVWeatherpage openWeatherPage() {
		openSubMenu();
		click(weatherSubMenuLink);
		return new NDTVWeatherpage(driver);
	}

	public void openSubMenu() {
		click(subMenuOpenLink);
	}

}
