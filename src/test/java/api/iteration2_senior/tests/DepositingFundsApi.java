package api.iteration2_senior.tests;
import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.models.DepositFundsRequest;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.skeleton.requesters.CrudRequester;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.steps.*;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;
import api.iteration2_senior.utils.RandomData;
import api.iteration2_senior.utils.TestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.offset;

public class DepositingFundsApi extends BaseTest {
    private String authTokenUser1;
    private String authTokenUser2;
    private int user1Id1;
    private int user2Id1;
    private static final double MONEY_ASSERT_DELTA = 0.01;
    private final double DEPOSIT_AMOUNT = RandomData.getRandomDepositAmount();
    private final int NON_EXISTENT_ACCOUNT_ID = RandomData.getRandomNonExistentId();

    @BeforeEach
    public void setUp() {
        ///USER 1
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser1 = AuthenticationStep.getUserTokenStep(user1);
        UserCreateAccountResponse response1User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id1 = response1User1.getId();
        ///USER 2
        AdminCreateUserRequest user2 = UserCreationStep.createUserRequest();
        authTokenUser2 = AuthenticationStep.getUserTokenStep(user2);
        UserCreateAccountResponse response1User2 = AccountCreationStep.userCreateAccount(authTokenUser2);
        user2Id1 = response1User2.getId();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 4999.99, 5000})
    public void userCanDepositFunds(double balance) {
        CustomerAccountsGetResponse[] accountsOld = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, balance);
        CustomerAccountsGetResponse[] accountsNew = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);
        softly.assertThat(TestUtils.findAccountById(accountsOld, user1Id1).getBalance() + balance)
                .isEqualTo(TestUtils.findAccountById(accountsNew, user1Id1).getBalance());
    }

    @ParameterizedTest
    @CsvSource({
            "-0.01, 'Invalid account or amount'",
            "0, 'Invalid account or amount'",
            "5000.01, 'Deposit amount exceeds the 5000 limit'",
    })
    public void userCannotDepositIncorrectAmountOfFunds(double balance, String error) {
        CustomerAccountsGetResponse[] accountsOld = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);
        DepositFundsRequest depositFundsRequest = DepositFundsRequest.builder()
                .accountId(user1Id1).amount(balance).build();
        new CrudRequester(RequestSpecs.userAuthSpec(authTokenUser1),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.returnsBadRequest())
                .post(depositFundsRequest)
                .body(Matchers.equalTo(error));

        CustomerAccountsGetResponse[] accountsNew = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);
        softly.assertThat(TestUtils.findAccountById(accountsOld, user1Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(accountsNew, user1Id1).getBalance(), offset(MONEY_ASSERT_DELTA));
    }

    @Test
    public void userCannotDepositFundsToUnfamiliarAccount() {
        CustomerAccountsGetResponse[] accountsOld = CustomerAccountStep.getCustomerAccountResponse(authTokenUser2);
        DepositFundsRequest depositFundsRequest = DepositFundsRequest.builder()
                .accountId(user2Id1).amount(DEPOSIT_AMOUNT).build();

        new CrudRequester(RequestSpecs.userAuthSpec(authTokenUser1),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.unauthorizedAccountAccess())
                .post(depositFundsRequest);

        CustomerAccountsGetResponse[] accountsNew = CustomerAccountStep.getCustomerAccountResponse(authTokenUser2);
        softly.assertThat(TestUtils.findAccountById(accountsOld, user2Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(accountsNew, user2Id1).getBalance(), offset(MONEY_ASSERT_DELTA));
    }

    @Test
    public void userCannotDepositFundsToNonExistentAccount() {
        DepositFundsRequest depositFundsRequest = DepositFundsRequest.builder()
                .accountId(NON_EXISTENT_ACCOUNT_ID).amount(DEPOSIT_AMOUNT).build();
        new CrudRequester(RequestSpecs.userAuthSpec(authTokenUser1),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.unauthorizedAccountAccess())
                .post(depositFundsRequest);
    }
}
