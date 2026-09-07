package ui.iteration2_senior;

import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.requests.steps.CustomerAccountStep;
import api.iteration2_senior.utils.RandomData;
import api.iteration2_senior.utils.TestUtils;
import common.annotations.Browsers;
import common.annotations.UserSession;
import common.data.UserSessionData;
import common.data.UserSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.DashboardPage;
import ui.middle.pages.DepositPage;

import static org.assertj.core.api.Assertions.offset;

@Browsers({"chrome"})
@UserSession()
public class DepositingFundsTests extends BaseUiTest {
    private UserSessionData user;
    private final double DEPOSIT_AMOUNT = RandomData.getRandomDepositAmount();
    private static final double MONEY_ASSERT_DELTA = 0.01;
    private DashboardPage dashboardPage = new DashboardPage();
    private DepositPage depositPage = new DepositPage();

    @BeforeEach
    public void prepareTestData(UserSessions users) {
        user = users.get(0);
    }

    @Test
    public void userCanDepositFunds() {
        dashboardPage.open().pressDepositMoneyButton();
        double accountBalanceBeforeDeposit = depositPage.selectAccount(user.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceBeforeDeposit = CustomerAccountStep.getCustomerAccountResponse(user.getAuthToken());

        depositPage.setDepositAmount(DEPOSIT_AMOUNT).depositButtonClick()
                .checkAlertMessageAndAccept(BankAlert.depositSuccessful(DEPOSIT_AMOUNT, user.getAccountId()))
                .getPage(DashboardPage.class).checkUserDashboardTextIsVisible()
                .pressDepositMoneyButton();
        double accountBalanceAfterDeposit = depositPage.selectAccount(user.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceAfterDeposit = CustomerAccountStep.getCustomerAccountResponse(user.getAuthToken());

        softly.assertThat(accountBalanceBeforeDeposit + DEPOSIT_AMOUNT).isEqualTo(accountBalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountBalanceBeforeDeposit, user.getAccountId()).getBalance() + DEPOSIT_AMOUNT)
                .isEqualTo(TestUtils.findAccountById(apiAccountBalanceAfterDeposit, user.getAccountId()).getBalance());
    }

    @Test
    public void userCannotDepositFundsUsingIncorrectAmountOfFunds() {
        dashboardPage.open().pressDepositMoneyButton();
        double accountBalanceBeforeDeposit = depositPage.selectAccount(user.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceBeforeDeposit = CustomerAccountStep.getCustomerAccountResponse(user.getAuthToken());

        depositPage.setDepositAmount(RandomData.getRandomDepositAmountGreaterThan5000())
                .depositButtonClick()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000$.getMessage())
                .shouldHaveDepositMoneyHeader().getPage(DashboardPage.class).pressDepositMoneyButton();
        double accountBalanceAfterDeposit = depositPage.selectAccount(user.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountBalanceAfterDeposit = CustomerAccountStep.getCustomerAccountResponse(user.getAuthToken());

        softly.assertThat(accountBalanceBeforeDeposit).isEqualTo(accountBalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountBalanceBeforeDeposit, user.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountBalanceAfterDeposit, user.getAccountId()).getBalance(), offset(MONEY_ASSERT_DELTA));
    }
}
