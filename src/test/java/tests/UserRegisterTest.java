package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-1")
    @Story("Успешная регистрация пользователя")
    @Severity(value = SeverityLevel.BLOCKER)
    @Description("This test successfully register new user with correct user data.")
    @DisplayName("Test positive register new user")
    @Tags({@Tag("registration"), @Tag("smoke")})
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 200);
        Assertions.assertJsonHasField(responseCreateUser, "id");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-2")
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration existing user.")
    @DisplayName("Test negative register existing user")
    @Tag("registration")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseTextEquals(responseCreateUser,
                "Users with email '" + email + "' already exists");
    }

    @Test
    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-3")
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration user with uncorrect email.")
    @DisplayName("Test negative register user with uncorrect email")
    @Tag("registration")
    public void testCreateUserWithUncorrectEmail() {
        String email = DataGenerator.getRandomEmail().replace("@", "");

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "Invalid email format");
    }

    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-4")
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("This test check status code and answer for registration user without required fields.")
    @DisplayName("Test negative register user without required field")
    @ParameterizedTest(name = "{displayName} - {0}")
    @ValueSource(strings={"email", "password", "username", "firstName", "lastName"})
    @Tag("registration")
    public void testCreateUserWithoutRequiredFields(String field) {
        Allure.parameter("required field", field);

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field);

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "The following required params are missed: " + field);
    }

    @Owner(value = "Голдина М.А.")
    @TmsLink(value = "PN-5")
    @Story("Недопустимая регистрация пользователя")
    @Severity(value = SeverityLevel.NORMAL)
    @Description("This test check status code and answer for registration user with uncorrect user name.")
    @DisplayName("Test negative register user with uncorrect user name")
    @ParameterizedTest(name = "{displayName} - with {1} symbols in {0}")
    @CsvSource({"username, 1, short", "username, 251, long", "firstName, 1, short", "firstName, 251, long",
            "lastName, 1, short", "lastName, 251, long"})
    @Tag("registration")
    public void testCreateUserWithUncorrectName(String field, int stringSize, String expectedResult) {
        Allure.parameter("field", field);
        Allure.parameter("symbols quantity", stringSize);
        Allure.parameter("expected value for name", expectedResult);

        String uncorrectName = DataGenerator.getRandomString(stringSize);

        Map<String, String> userData = new HashMap<>();
        userData.put(field, uncorrectName);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUser = apiCoreRequests
                .makePostCreateRequest(RequestSettings.registrationUrl, userData);

        Assertions.assertResponseCodeEquals(responseCreateUser, 400);
        Assertions.assertResponseHtmlTextEquals(responseCreateUser,
                "The value of '" + field + "' field is too " + expectedResult);
    }
}
