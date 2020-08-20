package test.subtra.web.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;

public class Utils {
	public Cookie getDetailedCookieUtil(Response response, String cookieName) {
		return response.getDetailedCookie(cookieName);
	}

	// Get response as jsonString
	public String getJsonString(Response res) {
		String jsonString = res.getBody().asString().replaceAll("\\[|\\]", "");
		return jsonString;
	}

	public Headers getHeaders() {

		List<Header> listOfHeaders = new ArrayList<>();

		String clientToken = System.getProperty("clienttoken", "false");
		if (clientToken != null && !clientToken.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("APPID", clientToken));
		return new Headers(listOfHeaders);
	}

	public void jsonString(String myJSONString) {
		try {
			JSONObject object = new JSONObject(myJSONString);
			String[] keys = JSONObject.getNames(object);

			for (String key : keys) {
				System.out.println(key);
				Object value = object.get(key);
				System.out.println(value.toString());
				// Determine type of value and do something with it...
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
