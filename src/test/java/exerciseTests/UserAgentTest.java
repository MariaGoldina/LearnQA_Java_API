package exerciseTests;

public class UserAgentTest {
//    public static List<String> userAgentProvider() {
//        List<String> userAgents = Stream.of(
//                "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
//                "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
//                "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
//                "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
//        ).collect(Collectors.toList());
//        List<String> devices = Stream.of(
//                "Android", "iOS", "Unknown", "No", "iPhone"
//        ).collect(Collectors.toList());
//        List<String> browsers = Stream.of(
//                "No", "Chrome", "Unknown", "Chrome", "No"
//        ).collect(Collectors.toList());
//        List<String> platforms = Stream.of(
//                "Mobile", "Mobile", "Googlebot", "Web", "Mobile"
//        ).collect(Collectors.toList());
//        List<String> values = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            values.add(userAgents.get(i));
//            values.add(devices.get(i));
//            values.add(browsers.get(i));
//            values.add(platforms.get(i));
//        }
//        return values;
//    }
//
//    @ParameterizedTest
//    @MethodSource("userAgentProvider")
//    public void testGetHeaderMethod(List<String> values) {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("User Agent ", values.get(0));
//
//        Response responseCheckUserAgent = RestAssured
//                .given()
//                .headers(headers)
//                .get("https://playground.learnqa.ru/api/homework_header")
//                .andReturn();
//
//        Headers responseHeaders = responseCheckUserAgent.getHeaders();
////        headers.put("device ", device);
////        headers.put("browser ", browser);
////        headers.put("platform ", platform);
//    }
}
