package ui.iteration2_senior;

import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.requests.steps.*;
import api.iteration2_senior.utils.RandomData;
import api.iteration2_senior.utils.TestUtils;
import com.codeborne.selenide.Selenide;
import common.annotations.Browsers;
import common.annotations.UserSession;
import common.data.UserSessionData;
import common.data.UserSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.DashboardPage;
import ui.middle.pages.TransferPage;

import static org.assertj.core.api.Assertions.offset;

@UserSession(2)
public class TransferringFundsTests extends BaseUiTest {
    private UserSessionData user1;
    private UserSessionData user2;
    private static final double INITIAL_DEPOSIT = 5000;
    private static final double MONEY_ASSERT_DELTA = 0.03;
    private final double TRANSFER_AMOUNT = RandomData.getRandomTransferAmount();
    private final int NON_EXISTENT_ACCOUNT_ID = RandomData.getRandomNonExistentId();
    private DashboardPage dashboardPage = new DashboardPage();
    private TransferPage transferPage = new TransferPage();


    @BeforeEach
    public void prepareData(UserSessions users) {
        user1 = users.get(0);
        user2 = users.get(1);
        DepositFundsStep.depositFunds(user1.getAuthToken(), user1.getAccountId(), INITIAL_DEPOSIT);
        DepositFundsStep.depositFunds(user1.getAuthToken(), user1.getAccountId(), INITIAL_DEPOSIT);
    }


    @Test
    public void userCanTransferFunds() {
        dashboardPage.open().pressMakeATransferButton();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransferUser1 = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());
        CustomerAccountsGetResponse[] apiAccountsBeforeTransferUser2 = CustomerAccountStep.getCustomerAccountResponse(user2.getAuthToken());

        transferPage.fillAllTransferFields(user2.getAccountId(), TRANSFER_AMOUNT).pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.transferSuccessful(TRANSFER_AMOUNT, user2.getAccountId()))
                .shouldHaveSendTransferButton();
        Selenide.refresh();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransferUser1 = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());
        CustomerAccountsGetResponse[] apiAccountsAfterTransferUser2 = CustomerAccountStep.getCustomerAccountResponse(user2.getAuthToken());

        softly.assertThat(account1BalanceBeforeDeposit - TRANSFER_AMOUNT).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransferUser1, user1.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransferUser1, user1.getAccountId()).getBalance() + TRANSFER_AMOUNT, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransferUser2, user2.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransferUser2, user2.getAccountId()).getBalance() - TRANSFER_AMOUNT, offset(MONEY_ASSERT_DELTA));
    }

    @Browsers({"chrome"})
    @Test
    public void userCannotTransferFundsUsingIncorrectAmountOfTransfer() {
        dashboardPage.open().pressMakeATransferButton();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransferUser1 = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());
        CustomerAccountsGetResponse[] apiAccountsBeforeTransferUser2 = CustomerAccountStep.getCustomerAccountResponse(user2.getAuthToken());

        transferPage.fillAllTransferFields(user2.getAccountId(), RandomData.getRandomTransferAmountGreaterThan10000()).pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_CANNOT_EXCEED_10000.getMessage())
                .shouldHaveSendTransferButton();
        Selenide.refresh();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransferUser1 = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());
        CustomerAccountsGetResponse[] apiAccountsAfterTransferUser2 = CustomerAccountStep.getCustomerAccountResponse(user2.getAuthToken());

        softly.assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransferUser1, user1.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransferUser1, user1.getAccountId()).getBalance(), offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransferUser2, user2.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransferUser2, user2.getAccountId()).getBalance(), offset(MONEY_ASSERT_DELTA));
    }

    @Test
    public void userCannotTransferFundsToNonExistentAccount() {
        dashboardPage.open().pressMakeATransferButton();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransfer = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());

        transferPage.fillAllTransferFields(NON_EXISTENT_ACCOUNT_ID, TRANSFER_AMOUNT).pressTransferButton()
                .checkAlertMessageAndAccept(BankAlert.NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER.getMessage())
                .shouldHaveSendTransferButton();
        Selenide.refresh();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1.getAccountId()).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransfer = CustomerAccountStep.getCustomerAccountResponse(user1.getAuthToken());

        softly.assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1.getAccountId()).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1.getAccountId()).getBalance(), offset(MONEY_ASSERT_DELTA));
    }
}
