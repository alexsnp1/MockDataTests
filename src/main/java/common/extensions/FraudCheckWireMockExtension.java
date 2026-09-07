package common.extensions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import common.annotations.FraudCheckMock;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FraudCheckWireMockExtension implements BeforeEachCallback, AfterEachCallback {
    private WireMockServer wireMockServer;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Find the FraudCheckMock annotation on the test method or class
        FraudCheckMock mockConfig = context.getTestMethod()
                .map(method -> method.getAnnotation(FraudCheckMock.class))
                .orElseGet(() -> context.getTestClass()
                        .map(clazz -> clazz.getAnnotation(FraudCheckMock.class))
                        .orElse(null));

        if (mockConfig != null) {
            setupWireMock(mockConfig);
        }
    }

    private void setupWireMock(FraudCheckMock config) {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(config.port()));
        wireMockServer.start();
        WireMock.configureFor("localhost", config.port());

        // Create the response body based on annotation parameters
        String responseBody = String.format("{\n" +
                        "  \"status\": \"%s\",\n" +
                        "  \"decision\": \"%s\",\n" +
                        "  \"riskScore\": %.1f,\n" +
                        "  \"reason\": \"%s\",\n" +
                        "  \"requiresManualReview\": %s,\n" +
                        "  \"additionalVerificationRequired\": %s\n" +
                        "}",
                config.status(),
                config.decision(),
                config.riskScore(),
                config.reason(),
                config.requiresManualReview(),
                config.additionalVerificationRequired());

        // Mock the fraud detection service endpoint
//        stubFor(post(urlPathMatching(config.endpoint()))
//                .willReturn(aResponse()
//                        .withStatus(config.httpStatus())
//                        .withFixedDelay(config.delayMs())
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseBody)));
        ResponseDefinitionBuilder response = aResponse()
                .withStatus(config.httpStatus())
                .withFixedDelay(config.delayMs())
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody);

        if (config.fault() != FraudCheckMock.WireMockFault.NONE) {
            response.withFault(Fault.valueOf(config.fault().name()));
        }

        stubFor(post(urlPathMatching(config.endpoint()))
                .willReturn(response));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        if (wireMockServer != null) {
            return "http://host.docker.internal:" + wireMockServer.port();
        }
        return null;
    }
}
