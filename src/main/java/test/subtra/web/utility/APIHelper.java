package test.subtra.web.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.restassured.response.Response;

public class APIHelper {

	protected static Logger logger = LoggerFactory.getLogger(APIHelper.class.getName());

	APICalls apiCall = new APICalls();

	public void setHeader(String key, String value) {
		System.setProperty(key, value);
	}

	public void setHeaders(HashMap<String, String> headers) {
		for (HashMap.Entry<String, String> entry : headers.entrySet()) {
			System.setProperty(entry.getKey(), entry.getValue());
		}
	}

	public Response request(String method, String endpoint) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, String.class);
			response = (Response) function.invoke(apiCall, endpoint);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public Response request(String method, String endpoint, HashMap<String, String> param) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, String.class, HashMap.class);
			response = (Response) function.invoke(apiCall, endpoint, param);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public Response request(String method, String endpoint, String body) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, String.class, String.class);
			response = (Response) function.invoke(apiCall, body, endpoint);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public Response request(String method, String endpoint, String body, HashMap<String, String> param) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, String.class, String.class, HashMap.class);
			response = (Response) function.invoke(apiCall, body, endpoint, param);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public Response request(String method, String endpoint, Object body) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, Object.class, String.class);
			response = (Response) function.invoke(apiCall, body, endpoint);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public Response request(String method, String endpoint, File body) {
		Response response = null;
		try {
			Method function = apiCall.getClass().getMethod(method, File.class, String.class);
			response = (Response) function.invoke(apiCall, body, endpoint);
		} catch (Exception e) {
			e.getMessage();
		}
		return response;
	}

	public JsonObject getJsonObject(Response response) {
		String jsonString = response.asString();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
		return jsonObject;
	}

	public JsonObject getJsonObject(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		try {
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(sb.toString()).getAsJsonObject();
		return jsonObject;
	}

	public String getJsonString(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		try {
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

	public JsonObject getJsonObject(JsonObject object, String node) {
		JsonObject jsonObject = object.getAsJsonObject(node);
		return jsonObject;
	}

	public JsonArray getJsonArray(JsonObject object, String node) {
		JsonArray jsonArray = object.getAsJsonArray(node);
		return jsonArray;
	}

}
