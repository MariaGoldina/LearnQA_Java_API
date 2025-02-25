package ExerciseTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectTest {
    @Test
    public void testLongRedirectLocation() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;
        int count = 0;
        while (statusCode!=200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            if(response.getStatusCode()!=200) {
                url = response.getHeader("Location");
                count+=1;
            }
            statusCode = response.getStatusCode();
        }

        System.out.println("Url итогового редиректа: " + url);
        System.out.println("Количество редиректов: " + count);
    }
}
