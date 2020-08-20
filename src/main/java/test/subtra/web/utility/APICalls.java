package test.subtra.web.utility;

import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;

import com.jayway.restassured.response.Response;

public class APICalls {
	Utils utils = new Utils();

	public Response getRequest(String endpoint) {
		return given().headers(utils.getHeaders()).when().get(endpoint).thenReturn();
	}

	public Response getRequest(String endpoint, HashMap<String, String> param) {
		return given().parameters(param).headers(utils.getHeaders()).when().get(endpoint).thenReturn();
	}

	public Response postRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().post(endpoint).then().extract().response();
	}

	public Response putRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().put(endpoint).then().extract().response();
	}

	public Response putRequest(Object body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().put(endpoint).then().extract().response();
	}

	public Response deleteRequest(String endpoint) {
		return given().headers(utils.getHeaders()).when().delete(endpoint).then().extract().response();
	}

	public Response deleteRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().delete(endpoint).then().extract().response();
	}

	public Response patchRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().patch(endpoint).then().extract().response();
	}

	public Response postFileRequest(String fileName, String endpoint) {
		return given().headers(utils.getHeaders()).multiPart(new File(fileName)).log().all().when().post(endpoint)
				.then().log().all().extract().response();
	}

	public Response postFileRequest(String fileName, String endpoint, HashMap<String, String> map) {
		return given().headers(utils.getHeaders()).multiPart(new File(fileName)).formParameters(map).log().all().when()
				.post(endpoint).then().log().all().extract().response();
	}

}