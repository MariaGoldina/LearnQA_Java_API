package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import lib.RequestSettings;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostLoginRequest(RequestSettings.loginUrl, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeaders(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-6")
    @Story("Успешная авторизация пользователя")
    @Severity(value = SeverityLevel.BLOCKER)
    @Description("This test successfully authorize user by email and password.")
    @DisplayName("Test positive auth user")
    @Tags({@Tag("authorization"), @Tag("smoke")})
    public void testAuthUser() {
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(RequestSettings.authUrl, this.header, this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-7")
    @Story("Недопустимая авторизация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test checks authorization status without auth token or cookie.")
    @DisplayName("Test negative auth user")
    @ParameterizedTest(name = "{displayName} - with only {0}")
    @ValueSource(strings = {"cookie", "headers"})
    @Tag("authorization")
    public void testNegativeAuthUser(String condition) {
        Allure.parameter("auth param", condition);

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(RequestSettings.authUrl, this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(RequestSettings.authUrl, this.header);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }
    }
}
