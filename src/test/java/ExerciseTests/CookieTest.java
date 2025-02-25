package ExerciseTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CookieTest {
    @Test
    public void testGetCookieMethod() {
        Response responseGetCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String,String> cookies = responseGetCookie.getCookies();

        Assertions.assertFalse(cookies.isEmpty(), "Response should contain cookies.");
        Assertions.assertTrue(
                cookies.containsKey("HomeWork"), "Response doesn't have cookie with name 'HomeWork'.");
        Assertions.assertEquals(
                "hw_value",
                cookies.get("HomeWork"),
                "Cookie 'HomeWork' value should be 'hw_value'.");
    }
}
