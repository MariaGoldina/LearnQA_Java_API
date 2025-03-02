package tests;

import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private final String registrationUrl = "https://playground.learnqa.ru/api/user/";

    @Test
    @Description("This test successfully register new user with correct user data.")
    @DisplayName("Test positive register new user")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasField(responseCreateUser, "id");
    }

    @Test
    @Description("This test check status code and answer for registration existing user.")
    @DisplayName("Test negative register existing user")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test check status code and answer for registration user with uncorrect email.")
    @DisplayName("Test negative register user with uncorrect email")
    public void testCreateUserWithUncorrectEmail() {
        String email = DataGenerator.getRandomEmail().replace("@", "");

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "Invalid email format");
    }

    @Description("This test check status code and answer for registration user without required fields.")
    @DisplayName("Test negative register user without required fields")
    @ParameterizedTest
    @ValueSource(strings={"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutRequiredFields(String field) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "The following required params are missed: " + field);
    }

    @Description("This test check status code and answer for registration user with uncorrect user name.")
    @DisplayName("Test negative register user with uncorrect user name")
    @ParameterizedTest
    @CsvSource({"username, 1, short", "username, 251, long", "firstName, 1, short", "firstName, 251, long",
            "lastName, 1, short", "lastName, 251, long"})
    public void testCreateUserWithUncorrectName(String field, int stringSize, String expectedResult) {
        String uncorrectName = DataGenerator.getRandomString(stringSize);

        Map<String, String> userData = new HashMap<>();
        userData.put(field, uncorrectName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostRequest(registrationUrl, userData);
        responseCreateUser.prettyPrint();

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "The value of '" + field + "' field is too " + expectedResult);
    }
}
