package common.annotations;

import api.iteration2_senior.enums.FraudDecision;
import api.iteration2_senior.enums.FraudReason;
import api.iteration2_senior.enums.FraudStatus;
import api.iteration2_senior.enums.WireMockFault;
import common.extensions.FraudCheckWireMockExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ExtendWith(FraudCheckWireMockExtension.class)
public @interface FraudCheckMock {
    /**
     * The fraud check status to return
     */
    FraudStatus status() default FraudStatus.SUCCESS;

    /**
     * The fraud check decision
     */
    FraudDecision decision() default FraudDecision.APPROVED;

    /**
     * The risk score (0.0 to 1.0)
     */
    double riskScore() default 0.2;

    /**
     * The reason for the fraud check result
     */
    FraudReason reason() default FraudReason.LOW_RISK_TRANSACTION;

    /**
     * Whether manual review is required
     */
    boolean requiresManualReview() default false;

    /**
     * Whether additional verification is required
     */
    boolean additionalVerificationRequired() default false;


    int httpStatus() default 200;

    int delayMs() default 0;

    WireMockFault fault() default WireMockFault.NONE;
    /**
     * The WireMock port to use
     */
    int port() default 8081;

    /**
     * The endpoint path to mock
     */
    String endpoint() default "/fraud-check";
}
