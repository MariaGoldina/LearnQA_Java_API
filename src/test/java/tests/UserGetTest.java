package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user data cases")
@Feature("Get user data")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-16")
    @Story("Успешное получение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test successfully get user data with user's auth cookie and token")
    @DisplayName("Test positive get user data with auth")
    @Tags({@Tag("get user data"), @Tag("smoke")})
    public void testGetUserDataDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostLoginRequest(RequestSettings.loginUrl, authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeaders(responseGetAuth, "x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequestWithUserId(RequestSettings.userUrl,"2", header, cookie);

        String[] expectedFieldNames = {"username", "firstName", "lastName", "email"};

        Assertions.assertJsonHasFields(responseUserData, expectedFieldNames);
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-17")
    @Story("Недопустимое получение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check answer for get user data without auth cookie and token.")
    @DisplayName("Test negative get user data without auth")
    @Tag("get user data")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithUserIdOnly(RequestSettings.userUrl, "2");

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-18")
    @Story("Недопустимое получение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check answer for get user data-request with auth cookie and token for other user.")
    @DisplayName("Test negative get user data with other user's auth")
    @Tag("get user data")
    public void testGetUserDataDetailsAuthAsOtherUser() {
        //CREATE USER 1
        Map<String, String> user1Data = DataGenerator.getRegistrationData();

        Response responseCreateUser1 = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, user1Data);

        Allure.step("Check preconditions: User1 is created.", step -> {
                    Assertions.assertResponseCodeEquals(responseCreateUser1, 200);
                });

        //LOGIN USER 1
        Map<String, String> authUser1Data = new HashMap<>();
        authUser1Data.put("email", user1Data.get("email"));
        authUser1Data.put("password", user1Data.get("password"));

        Response responseGetAuthUser1 = apiCoreRequests
                .makePostLoginRequest(RequestSettings.loginUrl, authUser1Data);

        Allure.step("Check preconditions: User1 is authorized.", step -> {
                    Assertions.assertResponseCodeEquals(responseGetAuthUser1, 200);
                });

        String cookie = this.getCookie(responseGetAuthUser1, "auth_sid");
        String header = this.getHeaders(responseGetAuthUser1, "x-csrf-token");

        //CREATE USER 2
        Map<String, String> user2Data = DataGenerator.getRegistrationData();

        Response responseCreateUser2 = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, user2Data);

        Allure.step("Check preconditions: User2 is created.", step -> {
                    Assertions.assertResponseCodeEquals(responseCreateUser2, 200);
                });

        String user2Id = responseCreateUser2.jsonPath().getString("id");

        //GET USER2 DATA WITH AUTH PARAMS FOR USER1
        Response responseUser2Data = apiCoreRequests
                .makeGetRequestWithUserId(RequestSettings.userUrl, user2Id, header, cookie);

        Assertions.assertJsonHasField(responseUser2Data, "username");
        Assertions.assertJsonHasNotField(responseUser2Data, "firstName");
        Assertions.assertJsonHasNotField(responseUser2Data, "lastName");
        Assertions.assertJsonHasNotField(responseUser2Data, "email");
    }
}
