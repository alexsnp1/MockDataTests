package api.iteration2_senior.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferFundsWireMockResponse extends BaseModel{
    private String status;
    private String decision;
    private String message;
    private double amount;
    private int senderAccountId;
    private int receiverAccountId;
    private double fraudRiskScore;
    private String fraudReason;
    private boolean requiresManualReview;
    private boolean requiresVerification;
    private int transactionId;
}
