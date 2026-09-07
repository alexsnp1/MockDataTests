package api.iteration2_senior.specs;

import api.iteration2_senior.configs.Config;
import api.iteration2_senior.utils.Headers;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {
    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unAuthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminAuthSpec() {
        return defaultRequestBuilder()
                .addHeader(Headers.AUTHORIZATION, Config.getProperty("admin.auth"))
                .build();
    }

    public static RequestSpecification userAuthSpec(String token) {
        return defaultRequestBuilder()
                .addHeader(Headers.AUTHORIZATION, token)
                .build();
    }

}
