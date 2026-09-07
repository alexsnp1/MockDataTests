package api.iteration2_senior.tests;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.TransferFundsWireMockResponse;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.models.comparison.ModelAssertions;
import api.iteration2_senior.requests.steps.*;
import api.iteration2_senior.utils.RandomData;
import common.annotations.FraudCheckMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferWithFraudCheckTest extends BaseTest {
    private String authTokenUser1;
    private int user1Id1;
    private int user1Id2;
    private static final double INITIAL_DEPOSIT = 5000;
    private final double TRANSFER_AMOUNT = RandomData.getRandomTransferAmount();

    @BeforeEach
    public void setUp() {
        /// USER 1
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser1 = AuthenticationStep.getUserTokenStep(user1);
        UserCreateAccountResponse response1User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id1 = response1User1.getId();
        UserCreateAccountResponse response2User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id2 = response2User1.getId();

        //deposit to acc1 user 1
        for (int i = 0; i < 2; i++) {
            DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
        }
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void userCanSuccessfullyTransferFundsWithFraudCheck() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();
        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "BLOCKED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void blockedDecisionLeadsToBlockTransfer() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("BLOCKED")
                .message("Transfer blocked due to fraud detection")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "REVIEW_REQUIRED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void reviewRequiredDecisionLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = true,
            additionalVerificationRequired = false
    )
    public void booleanRequiresManualReviewTrueLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(true)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "VERIFICATION_REQUIRED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void decisionVerificationRequiredLeadsToAdditionalVerification() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("VERIFICATION_REQUIRED")
                .message("Additional verification required")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = true
    )
    public void booleanAdditionalVerificationRequiredTrueLeadsToAdditionalVerification() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(true)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            httpStatus = 500,
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void serverError500LeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.5)
                .requiresManualReview(true)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).matchExcept("fraudReason");
        softly.assertThat(transferResponse.getFraudReason()).contains("Unexpected error during fraud check: 500 Server Error");
    }

    @Test
    @FraudCheckMock(
            fault = FraudCheckMock.WireMockFault.CONNECTION_RESET_BY_PEER,
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void connectionErrorLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck(user1Id1, user1Id2, TRANSFER_AMOUNT, authTokenUser1);

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = TransferFundsWireMockResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(TRANSFER_AMOUNT)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(0.5)
                .fraudReason("Fraud detection service is currently unavailable")
                .requiresManualReview(true)
                .requiresVerification(false)
                .build();
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

}
