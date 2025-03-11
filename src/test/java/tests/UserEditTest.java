package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit user data cases")
@Feature("Edit user data")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-8")
    @Story("Успешное изменение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test successfully edit user data with user's auth cookie and token")
    @DisplayName("Test positive edit user data with auth")
    @Tags({@Tag("edit user"), @Tag("smoke")})
    public void testEditJustCreatedUser() {
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

        //PUT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutEditRequest(RequestSettings.userUrl, userId, editData, header, cookie);

        Allure.step("Check status code 200 for edit request.", step -> {
            Assertions.assertResponseCodeEquals(responseEditUser, 200);
        });

        //GET
        Response responseGetUserData = apiCoreRequests
                .makeGetRequestWithUserId(RequestSettings.userUrl, userId, header, cookie);

        Assertions.assertJsonByName(responseGetUserData, "firstName", newName);
    }

    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-9")
    @Story("Недопустимое изменение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for edit user data request without required auth params")
    @DisplayName("Test negative edit user data without auth")
    @ParameterizedTest(name = "{displayName} - with {0}")
    @ValueSource(strings = {"no_auth", "token_only", "cookie_only"})
    @Tag("edit user")
    public void testEditUserDataWithoutAuth(String condition) {
        Allure.parameter("auth params", condition);

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

        //PUT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        if (condition.equals("no_auth")) {
            Response responseEditUser = apiCoreRequests.makePutEditRequestWithUserIdOnly(
                    RequestSettings.userUrl, userId, editData);
            Assertions.assertResponseCodeEquals(responseEditUser, 400);
            Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
        } else if (condition.equals("token_only")) {
            Response responseEditUser = apiCoreRequests.makePutEditRequestWithTokenOnly(
                    RequestSettings.userUrl, userId, editData, this.getHeaders(responseGetAuth, "x-csrf-token"));
            Assertions.assertResponseCodeEquals(responseEditUser, 400);
            Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
        } else if (condition.equals("cookie_only")) {
            Response responseEditUser = apiCoreRequests.makePutEditRequestWithCookieOnly(
                    RequestSettings.userUrl, userId, editData, this.getCookie(responseGetAuth, "auth_sid"));
            Assertions.assertResponseCodeEquals(responseEditUser, 400);
            Assertions.assertJsonByName(responseEditUser, "error", "Auth token not supplied");
        } else {
            throw new IllegalArgumentException("Condition value is known " + condition);
        }
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-10")
    @Issue(value = "EU-1")
    @Story("Недопустимое изменение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for edit user data-request with auth cookie and token for other user.")
    @DisplayName("Test negative edit user data with other user's auth")
    @Tag("edit user")
    public void testEditUserDataDetailsAuthAsOtherUser() {
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

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser2 = apiCoreRequests
                .makePutEditRequest(RequestSettings.userUrl, user2Id, editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser2, 400);
        Assertions.assertJsonByName(responseEditUser2, "error", "This user can only edit their own data.");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-11")
    @Story("Недопустимое изменение данных пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for change user's email to uncorrect email.")
    @DisplayName("Test negative edit user data with uncorrect email")
    @Tag("edit user")
    public void testEditUserDataWithUncorrectEmail() {
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

        //PUT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", userData.get("email").replace("@", ""));

        Response responseEditUser = apiCoreRequests
                .makePutEditRequest(RequestSettings.userUrl, userId, editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error", "Invalid email format");
    }

    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-12")
    @Story("Недопустимое изменение данных пользователя")
    @Severity(value = SeverityLevel.NORMAL)
    @Description("This test check status code and answer for edit user data with uncorrect name with 1 symbol.")
    @DisplayName("Test negative edit user data with uncorrect name")
    @ParameterizedTest(name = "{displayName} in {0}")
    @ValueSource(strings = {"username", "firstName", "lastName"})
    @Tag("edit user")
    public void testEditUserDataWithUncorrectName(String field) {
        Allure.parameter("testing field", field);

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

        //PUT
        Map<String, String> editData = new HashMap<>();
        editData.put(field, DataGenerator.getRandomString(1));

        Response responseEditUser = apiCoreRequests
                .makePutEditRequest(RequestSettings.userUrl, userId, editData, header, cookie);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(
                responseEditUser, "error", "The value for field `" + field + "` is too short");
    }
}
