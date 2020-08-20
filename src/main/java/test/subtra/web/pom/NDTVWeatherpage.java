package test.subtra.web.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NDTVWeatherpage extends AppPage {

	@FindBy(xpath = "//div[@id = 'more']//div[@id='messages']")
	private WebElement pinCity;

	@FindBy(xpath = "//div[@id='map_canvas']")
	private WebElement weatherMapCanvas;

	public NDTVWeatherpage(WebDriver driver) {
		super(driver);
		waitForPageLoadComplete();
	}

	public NDTVWeatherpage(WebDriver driver, String url) {
		super(driver);
		this.driver.get(url);
		validate();
	}

	public void validate() {
		logger.info("TITLE::" + getTitle());
	}

	public void pinCityForWeather(String city) {
		WebElement cityElement = pinCity.findElement(By.xpath(".//input[@id='" + city + "']"));
		selectCheckbox(cityElement);
	}

	public void clickCityOnMap(String city) {
		WebElement cityWeather = weatherMapCanvas
				.findElement(By.xpath(".//div[contains(@title, '" + city + "') ][contains(@class, 'outerContainer')]"));
		click(cityWeather);
	}

	public double getCityTempratureFromPopUp(String city) {
		WebElement tempratureDegreeLabel = weatherMapCanvas.findElement(By.xpath("//span[contains(text(), '" + city
				+ "')]//ancestor::div[contains(@class,'popup-content')]//b[contains(text(), 'Degrees')]"));

		String degree = tempratureDegreeLabel.getText();
		return convertToDouble(degree);
	}

	public int getCityHumidityFromPopUp(String city) {
		WebElement tempratureDegreeLabel = weatherMapCanvas.findElement(By.xpath("//span[contains(text(), '" + city
				+ "')]//ancestor::div[contains(@class,'popup-content')]//b[contains(text(), 'Humidity')]"));

		String humidity = tempratureDegreeLabel.getText();
		return convertToInt(humidity);
	}

	public double convertToDouble(String original) {
		String value = original;
		value.trim();
		String[] split = value.split("[^\\d]+");
		value = split[1];
		return Double.parseDouble(value);
	}

	public int convertToInt(String original) {
		String value = original;
		value.trim();
		String[] split = value.split("[^\\d]+");
		value = split[1];
		return Integer.parseInt(value);
	}
}
