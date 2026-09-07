package common.extensions;

import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;

public class NetworkLoggingExtension implements AfterTestExecutionCallback {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final List<String> NETWORK_LOGS = new ArrayList<>();

    @Override
    public void afterTestExecution(ExtensionContext context) {

        // Забираем последние события
        collectNetworkLogs();

        // Показываем network logs только если тест упал
        if (context.getExecutionException().isEmpty()) {
            NETWORK_LOGS.clear();
            return;
        }

        System.out.println();
        System.out.println("========== UI NETWORK LOGS ==========");

        for (String log : NETWORK_LOGS) {
            System.out.println(log);
        }

        System.out.println("=====================================");
        System.out.println();

        NETWORK_LOGS.clear();
    }

    public static void collectNetworkLogs() {

        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }

        WebDriver driver = WebDriverRunner.getWebDriver();

        for (LogEntry entry : driver.manage().logs().get(LogType.PERFORMANCE)) {

            try {

                JsonNode root =
                        OBJECT_MAPPER.readTree(entry.getMessage());

                JsonNode message = root.get("message");

                if (message == null) {
                    continue;
                }

                String methodName =
                        message.get("method").asText();

                JsonNode params =
                        message.get("params");

                if (params == null) {
                    continue;
                }

                /*
                 * ==========================
                 * REQUEST
                 * ==========================
                 */

                if ("Network.requestWillBeSent".equals(methodName)) {

                    JsonNode request =
                            params.get("request");

                    if (request == null) {
                        continue;
                    }

                    String requestId =
                            params.get("requestId").asText();

                    String httpMethod =
                            request.get("method").asText();

                    String url =
                            request.get("url").asText();

                    if (!url.contains("/api/v1/")) {
                        continue;
                    }

                    String postData = null;

                    if (request.has("postData")) {
                        postData =
                                request.get("postData").asText();
                    }

                    StringBuilder log = new StringBuilder();

                    log.append("UI REQUEST [")
                            .append(requestId)
                            .append("]: ")
                            .append(httpMethod)
                            .append(" ")
                            .append(url);

                    NETWORK_LOGS.add(log.toString());

                    if (postData != null && !postData.isBlank()) {

                        NETWORK_LOGS.add(
                                "    Body: " + postData
                        );
                    }
                }

                /*
                 * ==========================
                 * RESPONSE
                 * ==========================
                 */

                if ("Network.responseReceived".equals(methodName)) {

                    JsonNode response =
                            params.get("response");

                    if (response == null) {
                        continue;
                    }

                    String requestId =
                            params.get("requestId").asText();

                    String url =
                            response.get("url").asText();

                    if (!url.contains("/api/v1/")) {
                        continue;
                    }

                    int status =
                            response.get("status").asInt();

                    /*
                     * Сейчас сохраняем ВСЕ responses.
                     * Потом при необходимости отфильтруем.
                     */

                    NETWORK_LOGS.add(
                            "UI RESPONSE ["
                                    + requestId
                                    + "]: "
                                    + status
                                    + " "
                                    + url
                    );
                }

            } catch (Exception e) {

                NETWORK_LOGS.add(
                        "Failed to parse network log: "
                                + e.getMessage()
                );
            }
        }
    }

    public static void printNetworkLogs() {

        for (String log : NETWORK_LOGS) {
            System.out.println(log);
        }
    }
}