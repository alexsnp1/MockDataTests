package api.iteration2_senior.requests.steps;

import api.iteration2_senior.models.TransferFundsRequest;
import api.iteration2_senior.models.TransferFundsWireMockResponse;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;

public class TransferFundsWithFraudCheckSteps {
    public static TransferFundsWireMockResponse makeTransferWithFraudCheck(int accountIdSender, int accountIdReceiver, double amount, String authToken) {
        TransferFundsRequest transferFundsRequest = TransferFundsRequest.builder()
                .senderAccountId(accountIdSender).receiverAccountId(accountIdReceiver).amount(amount).build();

        return new ValidatedCrudRequester<TransferFundsWireMockResponse>(RequestSpecs.userAuthSpec(authToken),
                Endpoint.ACCOUNTS_TRANSFER_WITH_FRAUD_CHECK,
                ResponseSpecs.returnsOK())
                .post(transferFundsRequest);
    }
}
