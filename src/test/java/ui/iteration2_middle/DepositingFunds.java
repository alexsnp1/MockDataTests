package ui.iteration2_middle;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.steps.AccountCreationStep;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.CustomerAccountStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import api.iteration2_senior.utils.RandomData;
import api.iteration2_senior.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.DashboardPage;
import ui.middle.pages.DepositPage;

import static org.assertj.core.api.Assertions.offset;

public class DepositingFunds extends BaseUiTest {
    private static String authTokenUser1;
    private static int user1Id1;
    private static final double DEPOSIT_AMOUNT = RandomData.getRandomDepositAmount();
    private static final double MONEY_ASSERT_DELTA = 0.01;
    private DashboardPage dashboardPage = new DashboardPage();
    private DepositPage depositPage = new DepositPage();

    @BeforeEach
    public void prepareTestData() {
        /// CREATE USER
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser1 = AuthenticationStep.getUserTokenStep(user1);
        UserCreateAccountResponse response1User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id1 = response1User1.getId();
    }

    @Test
    public void userCanDepositFunds() {
        authAsUser(authTokenUser1);
        dashboardPage.open().pressDepositMoneyButton();
        double accountBalanceBeforeDeposit = depositPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceBeforeDeposit = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

//        depositPage.deposit(DEPOSIT_AMOUNT)
////                .checkAlertMessageAndAccept(BankAlert.depositSuccessful(DEPOSIT_AMOUNT, user1Id1))
//                .getPage(DashboardPage.class).checkUserDashboardTextIsVisible()
//                .pressDepositMoneyButton();
        double accountBalanceAfterDeposit = depositPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceAfterDeposit = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        softly.assertThat(accountBalanceBeforeDeposit + DEPOSIT_AMOUNT).isEqualTo(accountBalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountBalanceBeforeDeposit, user1Id1).getBalance() + DEPOSIT_AMOUNT)
                .isEqualTo(TestUtils.findAccountById(apiAccountBalanceAfterDeposit, user1Id1).getBalance());
    }

    @Test
    public void userCannotDepositFundsUsingIncorrectAmountOfFunds() {
        authAsUser(authTokenUser1);
        dashboardPage.open().pressDepositMoneyButton();
        double accountBalanceBeforeDeposit = depositPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceBeforeDeposit = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

//        depositPage.deposit(RandomData.getRandomDepositAmountGreaterThan5000())
////                .checkAlertMessageAndAccept(BankAlert.PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000$.getMessage())
//                .shouldHaveDepositMoneyHeader().getPage(DashboardPage.class).pressDepositMoneyButton();
        double accountBalanceAfterDeposit = depositPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceAfterDeposit = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        softly.assertThat(accountBalanceBeforeDeposit).isEqualTo(accountBalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountBalanceBeforeDeposit, user1Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountBalanceAfterDeposit, user1Id1).getBalance(), offset(MONEY_ASSERT_DELTA));
    }
}
