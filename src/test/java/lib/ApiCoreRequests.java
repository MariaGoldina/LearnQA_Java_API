package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-auth request with token and cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-auth request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-auth request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-user data request with user_id, token and cookie")
    public Response makeGetRequestWithUserId(String url, String userId, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get(url + userId)
                .andReturn();
    }

    @Step("Make a GET-user data request with user_id only")
    public Response makeGetRequestWithUserIdOnly(String url, String userId) {
        return given()
                .filter(new AllureRestAssured())
                .get(url + userId)
                .andReturn();
    }

    @Step("Make a POST-login request")
    public Response makePostLoginRequest(String url, Map<String, String> authdata) {
        return given()
                .filter(new AllureRestAssured())
                .body(authdata)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-create user request")
    public Response makePostCreateRequest(String url, Map<String, String> data) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-edit user data request with user_id only")
    public Response makePutEditRequestWithUserIdOnly(String url, String userId, Map<String, String> data) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .put(url + userId)
                .andReturn();
    }

    @Step("Make a PUT-edit user data request with user_id and token only")
    public Response makePutEditRequestWithTokenOnly(String url, String userId, Map<String, String> data, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .body(data)
                .put(url + userId)
                .andReturn();
    }

    @Step("Make a PUT-edit user data request with user_id and cookie only")
    public Response makePutEditRequestWithCookieOnly(String url, String userId, Map<String, String> data, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .body(data)
                .put(url + userId)
                .andReturn();
    }

    @Step("Make a PUT-edit user data request with user_id, token and cookie")
    public Response makePutEditRequest(String url, String userId, Map<String, String> data, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(data)
                .put(url + userId)
                .andReturn();
    }
}
