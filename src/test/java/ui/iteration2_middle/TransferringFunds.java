package ui.iteration2_middle;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.steps.*;
import api.iteration2_senior.utils.RandomData;
import api.iteration2_senior.utils.TestUtils;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.DashboardPage;
import ui.middle.pages.TransferPage;

import static org.assertj.core.api.Assertions.offset;

public class TransferringFunds extends BaseUiTest {
    private static String authTokenUser1;
    private static int user1Id1;
    private static int user1Id2;
    private static final double INITIAL_DEPOSIT = 5000;
    private static final double MONEY_ASSERT_DELTA = 0.03;
    private static final double TRANSFER_AMOUNT = RandomData.getRandomTransferAmount();
    private static final int NON_EXISTENT_ACCOUNT_ID = RandomData.getRandomNonExistentId();
    private DashboardPage dashboardPage = new DashboardPage();
    private TransferPage transferPage = new TransferPage();


    @BeforeEach
    public void prepareTestData() {
        /// CREATE USER
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser1 = AuthenticationStep.getUserTokenStep(user1);
        UserCreateAccountResponse response1User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id1 = response1User1.getId();
        UserCreateAccountResponse response2User1 = AccountCreationStep.userCreateAccount(authTokenUser1);
        user1Id2 = response2User1.getId();
        ///DEPOSIT TO ACC1 USER
        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
        DepositFundsStep.depositFunds(authTokenUser1, user1Id1, INITIAL_DEPOSIT);
    }

    @Test
    public void userCanTransferFunds() {
        authAsUser(authTokenUser1);
        dashboardPage.open().pressMakeATransferButton();
        double account2BalanceBeforeDeposit = transferPage.selectAccount(user1Id2).getSelectedAccountBalance();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

//        transferPage.makeTransfer(user1Id2, TRANSFER_AMOUNT)
////                .checkAlertMessageAndAccept(BankAlert.transferSuccessful(TRANSFER_AMOUNT, user1Id2))
//                .shouldHaveSendTransferButton();
//        Selenide.refresh();
        double account2BalanceAfterDeposit = transferPage.selectAccount(user1Id2).getSelectedAccountBalance();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        softly.assertThat(account1BalanceBeforeDeposit - TRANSFER_AMOUNT).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(account2BalanceBeforeDeposit + TRANSFER_AMOUNT).isEqualTo(account2BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1Id1).getBalance() + TRANSFER_AMOUNT, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1Id2).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1Id2).getBalance() - TRANSFER_AMOUNT, offset(MONEY_ASSERT_DELTA));
    }

    @Test
    public void userCannotTransferFundsUsingIncorrectAmountOfTransfer() {
        authAsUser(authTokenUser1);
        dashboardPage.open().pressMakeATransferButton();
        double account2BalanceBeforeDeposit = transferPage.selectAccount(user1Id2).getSelectedAccountBalance();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

//        transferPage.makeTransfer(user1Id2, RandomData.getRandomTransferAmountGreaterThan10000())
////                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AMOUNT_CANNOT_EXCEED_10000.getMessage())
//                .shouldHaveSendTransferButton();
//        Selenide.refresh();
        double account2BalanceAfterDeposit = transferPage.selectAccount(user1Id2).getSelectedAccountBalance();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        softly.assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(account2BalanceBeforeDeposit).isEqualTo(account2BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1Id1).getBalance(), offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1Id2).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1Id2).getBalance(), offset(MONEY_ASSERT_DELTA));
    }

    @Test
    public void userCannotTransferFundsToNonExistentAccount() {
        authAsUser(authTokenUser1);
        dashboardPage.open().pressMakeATransferButton();
        double account1BalanceBeforeDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsBeforeTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

//        transferPage.makeTransfer(NON_EXISTENT_ACCOUNT_ID, TRANSFER_AMOUNT)
////                .checkAlertMessageAndAccept(BankAlert.NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER.getMessage())
//                .shouldHaveSendTransferButton();
//        Selenide.refresh();
        double account1BalanceAfterDeposit = transferPage.selectAccount(user1Id1).getSelectedAccountBalance();
        CustomerAccountsGetResponse[] apiAccountsAfterTransfer = CustomerAccountStep.getCustomerAccountResponse(authTokenUser1);

        softly.assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit, offset(MONEY_ASSERT_DELTA));
        softly.assertThat(TestUtils.findAccountById(apiAccountsBeforeTransfer, user1Id1).getBalance())
                .isEqualTo(TestUtils.findAccountById(apiAccountsAfterTransfer, user1Id1).getBalance(), offset(MONEY_ASSERT_DELTA));
    }
}
