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

@Epic("Delete user data cases")
@Feature("Delete user data")
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-13")
    @Story("Недопустимое удаление пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for delete user 2 with role admin")
    @DisplayName("Test negative delete amin user")
    @Tags({@Tag("delete user"), @Tag("smoke")})
    public void testDeleteUserDataForAdminUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostLoginRequest(RequestSettings.loginUrl, authData);

        Allure.step("Check preconditions: User is authorized.", step -> {
            Assertions.assertResponseCodeEquals(responseGetAuth, 200);
        });

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteUserRequest(RequestSettings.userUrl,
                        "2",
                        this.getHeaders(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error",
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-14")
    @Story("Успешное удаление пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test successfully delete user with user's auth cookie and token")
    @DisplayName("Test positive delete user with user's auth params")
    @Tags({@Tag("delete user"), @Tag("smoke")})
    public void testDeleteUserDataDetailsAuthAsSameUser() {
        //CREATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Allure.step("Check preconditions: User is created.", step -> {
            Assertions.assertResponseCodeEquals(responseCreateUser, 200);
        });

        String userId = responseCreateUser.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostLoginRequest(RequestSettings.loginUrl, authData);

        Allure.step("Check preconditions: User is authorized.", step -> {
            Assertions.assertResponseCodeEquals(responseGetAuth, 200);
        });

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String header = this.getHeaders(responseGetAuth, "x-csrf-token");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteUserRequest(RequestSettings.userUrl, userId, header, cookie);

        Allure.step("Check status code 200 for delete request.", step -> {
            Assertions.assertResponseCodeEquals(responseDeleteUser, 200);
        });

        //GET
        Response responseGetUserData = apiCoreRequests
                .makeGetRequestWithUserId(RequestSettings.userUrl, userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseGetUserData, 404);
        Assertions.assertResponseHtmlTextEquals(responseGetUserData,
                "User not found");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-15")
    @Issue(value = "DU-1")
    @Story("Недопустимое удаление пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for delete user with auth cookie and token for other user.")
    @DisplayName("Test negative delete user with other user's auth")
    @Tag("delete user")
    public void testDeleteUserDetailsAuthAsOtherUser() {
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

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteUserRequest(RequestSettings.userUrl, user2Id, header, cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error",
                "This user can only delete their own account.");
    }
}
