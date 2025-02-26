package exerciseTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongTimeJobTest {
    @Test
    public void testLongTimeJobWithToken() {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";

        Response responseStartJob = RestAssured
                .get(url)
                .andReturn();

        String token = responseStartJob.jsonPath().get("token");
        int seconds = responseStartJob.jsonPath().get("seconds");

        if(seconds!=0) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Something went wrong while waiting end of job.");
            }
        }
        if(token!=null || !token.isEmpty()) {
            Response responseAfterJob = RestAssured
                    .given()
                    .param("token", token)
                    .when()
                    .get(url)
                    .andReturn();

            String status = responseAfterJob.jsonPath().get("status");
            String result = responseAfterJob.jsonPath().get("result");

            if (status.equals("Job is ready") && result!=null) {
                System.out.println(status);
                System.out.println("Result: " + result);
            } else {
                System.out.println("Job is NOT ready.");
            }
        }
    }
}
