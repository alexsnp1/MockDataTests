package ui.iteration2_junior;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.steps.AccountCreationStep;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.DepositFundsStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import api.iteration2_senior.utils.RandomData;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferringFunds {
    private static String authTokenUser1;
    private static int user1Id1;
    private static int user1Id2;
    private static final double INITIAL_DEPOSIT = 5000;
    private static final double TRANSFER_AMOUNT = RandomData.getRandomTransferAmount();
    private static final int NON_EXISTENT_ACCOUNT_ID = RandomData.getRandomNonExistentId();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.133:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
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
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser1);
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id2));
        double account2BalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        String name = $(".user-name").getText();
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys("ACC" + user1Id2);
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(String.valueOf(TRANSFER_AMOUNT));
        $(Selectors.byId("confirmCheck")).click();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("✅ Successfully transferred $" + TRANSFER_AMOUNT + " to account ACC" + user1Id2 + "!")).isTrue();
        alert.accept();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").shouldBe(Condition.visible);
        Selenide.refresh();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id2));
        double account2BalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        assertThat(account1BalanceBeforeDeposit - TRANSFER_AMOUNT).isEqualTo(account1BalanceAfterDeposit);
        assertThat(account2BalanceBeforeDeposit + TRANSFER_AMOUNT).isEqualTo(account2BalanceAfterDeposit);
    }

    @Test
    public void userCannotTransferFundsUsingIncorrectAmountOfTransfer() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser1);
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id2));
        double account2BalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        String name = $(".user-name").getText();
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys("ACC" + user1Id2);
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(String.valueOf(RandomData.getRandomTransferAmountGreaterThan10000()));
        $(Selectors.byId("confirmCheck")).click();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("❌ Error: Transfer amount cannot exceed 10000")).isTrue();
        alert.accept();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").shouldBe(Condition.visible);
        Selenide.refresh();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id2));
        double account2BalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit);
        assertThat(account2BalanceBeforeDeposit).isEqualTo(account2BalanceAfterDeposit);

    }

    @Test
    public void userCannotTransferFundsToNonExistentAccount() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser1);
        Selenide.open("/dashboard");
        $(Selectors.byText("🔄 Make a Transfer")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        String name = $(".user-name").getText();
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys(name);
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys("ACC" + NON_EXISTENT_ACCOUNT_ID);
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(String.valueOf(TRANSFER_AMOUNT));
        $(Selectors.byId("confirmCheck")).click();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("❌ No user found with this account number.")).isTrue();
        alert.accept();
        $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]").shouldBe(Condition.visible);
        Selenide.refresh();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double account1BalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        assertThat(account1BalanceBeforeDeposit).isEqualTo(account1BalanceAfterDeposit);
    }
}
