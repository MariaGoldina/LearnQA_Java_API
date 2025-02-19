import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectTest {
    @Test
    public void testLongRedirectLocation() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;
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
            }
            statusCode = response.getStatusCode();
        }

        System.out.println(url);
    }
}
