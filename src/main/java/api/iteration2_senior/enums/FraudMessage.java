package api.iteration2_senior.enums;

public enum FraudMessage {
    TRANSFER_APPROVED_AND_PROCESSED_IMMEDIATELY("Transfer approved and processed immediately"),
    TRANSFER_BLOCKED_DUE_TO_FRAUD_DETECTION("Transfer blocked due to fraud detection"),
    TRANSFER_REQUIRES_MANUAL_REVIEW("Transfer requires manual review"),
    ADDITIONAL_VERIFICATION_REQUIRED("Additional verification required");

    private final String value;

    FraudMessage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
