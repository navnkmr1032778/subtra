package test.subtra.APITest;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.jayway.restassured.response.Response;

import test.subtra.web.constants.APIConstants;

public class WeatherAPITest extends WeatherAPIMainTest {

	public WeatherAPITest() {
		super();
	}

	public Response sendRequest(String cityName) {
		String apiURL = APIConstants.API_URL;
		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("q", cityName);
		parameter.put("APPID", APIConstants.CLIENT_TOKEN);
		Response response = helper.request(APIConstants.GET, apiURL, parameter);
		return response;
	}

	public double getDegreeFromResponse(Response response) {
		JsonObject jObj = helper.getJsonObject(response);
		String cityDegree = jObj.get("main").getAsJsonObject().get("temp").getAsString();
		return Double.parseDouble(cityDegree);
	}

	public int getHumidityFromResponse(Response response) {
		JsonObject jObj = helper.getJsonObject(response);
		String cityHumidity = jObj.get("main").getAsJsonObject().get("humidity").getAsString();
		return Integer.parseInt(cityHumidity);
	}
}
