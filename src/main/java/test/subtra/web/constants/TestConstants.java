package test.subtra.web.constants;

public class TestConstants {

	public static final String NDTV_MAIN_PAGE_URL = "https://www.ndtv.com/";
	public static final String REPORTS_DIRECTORY = "Reports";
	public static final String CITY_NAME = System.getProperty("city", "Jaipur");
	public static final int WEATHER_DEGREE_VARIANCE = Integer
			.parseInt(System.getProperty("weatherDegreeVariance", "-1"));
	public static final int WEATHER_HUMIDITY_VARIANCE = Integer
			.parseInt(System.getProperty("weatherHumidityVariance", "-1"));

}
