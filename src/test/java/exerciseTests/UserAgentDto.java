package exerciseTests;

public class UserAgentDto {
    private String userAgent;
    private String expectedDevice;
    private String expectedBrowser;
    private String expectedPlatform;

    public UserAgentDto(String userAgent, String expectedDevice, String expectedBrowser, String expectedPlatform) {
        this.userAgent=userAgent;
        this.expectedDevice=expectedDevice;
        this.expectedBrowser=expectedBrowser;
        this.expectedPlatform=expectedPlatform;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getExpectedDevice() {
        return expectedDevice;
    }

    public String getExpectedBrowser() {
        return expectedBrowser;
    }

    public String getExpectedPlatform() {
        return expectedPlatform;
    }
}
