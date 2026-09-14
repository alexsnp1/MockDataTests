package api.iteration2_senior.utils;

import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ProfileRequestWaiter {

    private static final String PROFILE_PATH =
            "/api/v1/customer/profile";

    private static final int EXPECTED_REQUESTS = 2;

    private final WebSocketClient webSocketClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Set<String> profileRequests =
            ConcurrentHashMap.newKeySet();

    private final CountDownLatch completedRequests =
            new CountDownLatch(EXPECTED_REQUESTS);

    private volatile String requestError;

    public ProfileRequestWaiter() {
        WebDriver driver = WebDriverRunner.getWebDriver();

        String cdpUrl = ((HasCapabilities) driver)
                .getCapabilities()
                .getCapability("se:cdp")
                .toString();

        try {
            URI uri = URI.create(
                    cdpUrl.endsWith("/")
                            ? cdpUrl + "page"
                            : cdpUrl + "/page"
            );

            System.out.println("FULL CDP URL: " + uri);

            webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    //Connection established
                }

                @Override
                public void onMessage(String message) {
                    handleCdpMessage(message);
                }

                @Override
                public void onClose(
                        int code,
                        String reason,
                        boolean remote
                ) {
                    //Connection closed
                }

                @Override
                public void onError(Exception ex) {
                    requestError =
                            "CDP WebSocket error: "
                                    + ex.getMessage();
                }
            };

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to create CDP WebSocket client",
                    e
            );
        }
    }

    public void start() {
        try {
            if (!webSocketClient
                    .connectBlocking(10, TimeUnit.SECONDS)) {

                throw new AssertionError(
                        "Could not connect to Selenoid CDP WebSocket"
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while connecting to CDP",
                    e
            );
        }

        webSocketClient.send(
                """
                        {
                            "id": 1,
                            "method": "Network.enable"
                        }
                        """
        );

        System.out.println(
                "CDP Network monitoring started"
        );
    }

    public void waitForTwoRequests(Duration timeout) {

        try {
            boolean completed =
                    completedRequests.await(
                            timeout.toMillis(),
                            TimeUnit.MILLISECONDS
                    );

            if (requestError != null) {
                throw new AssertionError(requestError);
            }

            if (!completed) {
                int completedCount =
                        EXPECTED_REQUESTS
                                - (int) completedRequests.getCount();

                throw new AssertionError(
                        "Expected "
                                + EXPECTED_REQUESTS
                                + " completed GET requests to "
                                + PROFILE_PATH
                                + ", but only "
                                + completedCount
                                + " were completed within "
                                + timeout.getSeconds()
                                + " seconds"
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while waiting for profile requests",
                    e
            );
        } finally {
            webSocketClient.close();
        }
    }

    private void handleCdpMessage(String message) {
        try {
            JsonNode root =
                    objectMapper.readTree(message);

            String method =
                    root.path("method").asText();

            if ("Network.requestWillBeSent".equals(method)) {

                JsonNode params =
                        root.path("params");

                JsonNode request =
                        params.path("request");

                String requestId =
                        params.path("requestId").asText();

                String requestMethod =
                        request.path("method").asText();

                String url =
                        request.path("url").asText();

                if ("GET".equalsIgnoreCase(requestMethod)
                        && isProfileRequest(url)) {
                    profileRequests.add(requestId);
                }
            }

            if ("Network.loadingFinished".equals(method)) {

                String requestId =
                        root.path("params")
                                .path("requestId")
                                .asText();

                if (profileRequests.remove(requestId)) {

                    completedRequests.countDown();
                }
            }

            if ("Network.loadingFailed".equals(method)) {

                String requestId =
                        root.path("params")
                                .path("requestId")
                                .asText();

                if (profileRequests.remove(requestId)) {

                    String errorText =
                            root.path("params")
                                    .path("errorText")
                                    .asText();

                    requestError =
                            "GET "
                                    + PROFILE_PATH
                                    + " failed: "
                                    + errorText;

                    completedRequests.countDown();
                }
            }

        } catch (Exception e) {

            requestError =
                    "Failed to parse CDP message: "
                            + e.getMessage();
        }
    }

    private boolean isProfileRequest(String url) {

        if (url == null) {
            return false;
        }

        try {
            return PROFILE_PATH.equals(
                    URI.create(url).getPath()
            );
        } catch (Exception e) {
            return false;
        }
    }
}