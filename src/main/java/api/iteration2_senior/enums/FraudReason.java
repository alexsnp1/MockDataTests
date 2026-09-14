package api.iteration2_senior.enums;

public enum FraudReason {
    LOW_RISK_TRANSACTION("Low risk transaction"),
    FRAUD_DETECTION_SERVICE_IS_CURRENTLY_UNAVAILABLE("Fraud detection service is currently unavailable"),
    UNEXPECTED_ERROR_DURING_FRAUD_CHECK_500_SERVER_ERROR("Unexpected error during fraud check: 500 Server Error");
    private final String value;

    FraudReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
