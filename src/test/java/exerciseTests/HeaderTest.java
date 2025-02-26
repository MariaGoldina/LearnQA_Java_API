package exerciseTests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeaderTest {
    @Test
    public void testGetHeaderMethod() {
        Response responseGetHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = responseGetHeader.getHeaders();

        Assertions.assertTrue(headers.exist(), "Response should contain headers.");
        Assertions.assertTrue(
                headers.hasHeaderWithName("x-secret-homework-header"),
                "Response doesn't have header with name 'x-secret-homework-header'.");
        Assertions.assertEquals(
                "Some secret value",
                headers.get("x-secret-homework-header").getValue(),
                "Header 'x-secret-homework-header' value should be 'Some secret value'.");
    }
}
