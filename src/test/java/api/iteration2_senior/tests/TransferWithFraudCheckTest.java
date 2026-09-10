package api.iteration2_senior.tests;

import api.iteration2_senior.enums.*;
import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.TransferFundsWireMockResponse;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.models.comparison.ModelAssertions;
import api.iteration2_senior.requests.steps.*;
import api.iteration2_senior.utils.RandomData;
import common.annotations.FraudCheckMock;
import common.utils.Repeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferWithFraudCheckTest extends BaseTest {
    private String authTokenUser1;
    private int user1Id1;
    private int user1Id2;

    private static final double INITIAL_DEPOSIT = 5000;
    private static final int INITIAL_DEPOSIT_COUNT = 2;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final double LOW_RISK_SCORE = 0.2;
    private static final double HIGH_RISK_SCORE = 0.5;
    private static final String FRAUD_REASON_FIELD = "fraudReason";

    private final double transferAmount = RandomData.getRandomTransferAmount();

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
        Repeat.repeat(INITIAL_DEPOSIT_COUNT, () -> DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT));
    }

    private TransferFundsWireMockResponse buildExpectedResponse(
            String status,
            String message,
            double riskScore,
            String fraudReason,
            boolean requiresManualReview,
            boolean requiresVerification
    ) {
        return TransferFundsWireMockResponse.builder()
                .status(status)
                .message(message)
                .amount(transferAmount)
                .senderAccountId(user1Id1)
                .receiverAccountId(user1Id2)
                .fraudRiskScore(riskScore)
                .fraudReason(fraudReason)
                .requiresManualReview(requiresManualReview)
                .requiresVerification(requiresVerification)
                .build();
    }

    private TransferFundsWireMockResponse makeTransfer() {
        return TransferFundsWithFraudCheckSteps.makeTransferWithFraudCheck
                (user1Id1, user1Id2, transferAmount, authTokenUser1);
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.APPROVED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void userCanSuccessfullyTransferFundsWithFraudCheck() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.APPROVED.name(),
                FraudMessage.TRANSFER_APPROVED_AND_PROCESSED_IMMEDIATELY.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                false,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.BLOCKED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void blockedDecisionLeadsToBlockTransfer() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.BLOCKED.name(),
                FraudMessage.TRANSFER_BLOCKED_DUE_TO_FRAUD_DETECTION.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                false,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.REVIEW_REQUIRED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void reviewRequiredDecisionLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.MANUAL_REVIEW_REQUIRED.name(),
                FraudMessage.TRANSFER_REQUIRES_MANUAL_REVIEW.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                false,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.APPROVED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = true,
            additionalVerificationRequired = false
    )
    public void booleanRequiresManualReviewTrueLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.MANUAL_REVIEW_REQUIRED.name(),
                FraudMessage.TRANSFER_REQUIRES_MANUAL_REVIEW.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                true,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.VERIFICATION_REQUIRED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void decisionVerificationRequiredLeadsToAdditionalVerification() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.VERIFICATION_REQUIRED.name(),
                FraudMessage.ADDITIONAL_VERIFICATION_REQUIRED.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                false,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.APPROVED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = true
    )
    public void booleanAdditionalVerificationRequiredTrueLeadsToAdditionalVerification() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.APPROVED.name(),
                FraudMessage.TRANSFER_APPROVED_AND_PROCESSED_IMMEDIATELY.getValue(),
                LOW_RISK_SCORE,
                FraudReason.LOW_RISK_TRANSACTION.getValue(),
                false,
                true);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            httpStatus = INTERNAL_SERVER_ERROR,
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.APPROVED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void serverError500LeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.MANUAL_REVIEW_REQUIRED.name(),
                FraudMessage.TRANSFER_REQUIRES_MANUAL_REVIEW.getValue(),
                HIGH_RISK_SCORE,
                null,
                true,
                false);

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).matchExcept(FRAUD_REASON_FIELD);
        softly.assertThat(transferResponse.getFraudReason()).contains(FraudReason.UNEXPECTED_ERROR_DURING_FRAUD_CHECK_500_SERVER_ERROR.getValue());
    }

    @Test
    @FraudCheckMock(
            fault = WireMockFault.CONNECTION_RESET_BY_PEER,
            status = FraudStatus.SUCCESS,
            decision = FraudDecision.APPROVED,
            riskScore = LOW_RISK_SCORE,
            reason = FraudReason.LOW_RISK_TRANSACTION,
            requiresManualReview = false,
            additionalVerificationRequired = false
    )
    public void connectionErrorLeadsToManualReview() {
        TransferFundsWireMockResponse transferResponse
                = makeTransfer();

        softly.assertThat(transferResponse).isNotNull();

        TransferFundsWireMockResponse expectedResponse = buildExpectedResponse(
                FraudStatus.MANUAL_REVIEW_REQUIRED.name(),
                FraudMessage.TRANSFER_REQUIRES_MANUAL_REVIEW.getValue(),
                HIGH_RISK_SCORE,
                FraudReason.FRAUD_DETECTION_SERVICE_IS_CURRENTLY_UNAVAILABLE.getValue(),
                true,
                false);
        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}
