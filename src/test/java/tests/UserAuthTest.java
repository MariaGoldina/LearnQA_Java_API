package tests;

import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String authUrl = "https://playground.learnqa.ru/api/user/auth";
    private final String loginUrl = "https://playground.learnqa.ru/api/user/login";

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(loginUrl, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeaders(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password.")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(authUrl, this.header, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("This test checks authorization status without auth token or cookie.")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {
        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(authUrl, this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(authUrl, this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }
    }
}
