package test.subtra.web.regression;

import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;

import test.subtra.POJO.WeatherDetails;
import test.subtra.POJO.WeatherVarianceComparator;
import test.subtra.web.constants.TestConstants;

public class SubtraWeatherTest extends TestMain {

	protected Logger logger = this.getLogger();

	@Test(priority = 1, groups = { "Regression" })
	public void verifyNDTVWeatherTest() throws IOException {
		try {
			// Opening NDTV Weather Page And Fetching City's Weather data
			loadHomePage();
			ndtvWeatherPage = ndtvMainPage.openWeatherPage();
			ndtvWeatherPage.pinCityForWeather(TestConstants.CITY_NAME);
			ndtvWeatherPage.clickCityOnMap(TestConstants.CITY_NAME);
			double degree = ndtvWeatherPage.getCityTempratureFromPopUp(TestConstants.CITY_NAME);
			int humidity = ndtvWeatherPage.getCityHumidityFromPopUp(TestConstants.CITY_NAME);

			logger.info("Degree from NDTV Weather Page::" + degree);
			logger.info("Humididty from NDTV Weather Page::" + humidity);
			weatherFromWeb = getWeatherPOJO(degree, humidity);

			// Fetching City's Weather data from OpenWeather API
			weatherAPITest = getWeatherAPIPOJO();
			Response apiResponse = weatherAPITest.sendRequest(TestConstants.CITY_NAME);
			weatherAPITest.statusChecker.checkStatusIs200_OK(apiResponse);
			double degreeApi = WeatherDetails.kelvinToDegree(weatherAPITest.getDegreeFromResponse(apiResponse));
			int humidityApi = weatherAPITest.getHumidityFromResponse(apiResponse);

			logger.info("Degree from Open Weather API::" + degreeApi);
			logger.info("Humididty from Open Weather API::" + humidityApi);
			weatherFromAPI = getWeatherPOJO(degreeApi, humidityApi);

			// Comparing WeatherObjects between NDTV Weather And OpenWeather API
			WeatherVarianceComparator weatherComparator = new WeatherVarianceComparator(
					TestConstants.WEATHER_DEGREE_VARIANCE, TestConstants.WEATHER_HUMIDITY_VARIANCE);

			// Degree compare
			if (!weatherComparator.compareVarianceDegree(weatherFromWeb, weatherFromAPI)) {
				AssertJUnit.fail("Out Of Degree Variance::" + "Degree from NDTV Weather Page - " + degree
						+ "::Degree from Open Weather API - " + degreeApi);
			}

			// Humidity compare
			if (!weatherComparator.compareVarianceHumidity(weatherFromWeb, weatherFromAPI)) {
				AssertJUnit.fail("Out Of Humidity Variance::" + "Humididty from NDTV Weather Page::" + humidity
						+ "Humididty from Open Weather API::" + humidityApi);
			}

		} catch (Exception e) {
			AssertJUnit.fail("Exception happened::" + ExceptionUtils.getFullStackTrace(e));
		}
	}

}
