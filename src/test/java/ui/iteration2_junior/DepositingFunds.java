package ui.iteration2_junior;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.steps.AccountCreationStep;
import api.iteration2_senior.requests.steps.AuthenticationStep;
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

public class DepositingFunds {
    private static String authTokenUser1;
    private static int user1Id1;
    private static final double DEPOSIT_AMOUNT = RandomData.getRandomDepositAmount();

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
    }

    @Test
    public void userCanDepositFunds() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser1);
        Selenide.open("/dashboard");
        $(Selectors.byText("💰 Deposit Money")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double accountBalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(String.valueOf(DEPOSIT_AMOUNT));
        $x("//button[contains(text(), 'Deposit')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("✅ Successfully deposited $" + DEPOSIT_AMOUNT + " to account ACC" + user1Id1 + "!")).isTrue();
        alert.accept();
        $(Selectors.byText("User Dashboard")).shouldBe(Condition.visible);

        //проверка, что при повторном нажатии на кнопку “Deposit Money” и раскрытии дропдауна для выбора аккаунта, Balance аккаунта, выбранного в шаге 3, увеличился на сумму, введенную в шаге 4
        $(Selectors.byText("💰 Deposit Money")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double accountBalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        assertThat(accountBalanceBeforeDeposit + DEPOSIT_AMOUNT).isEqualTo(accountBalanceAfterDeposit);
    }

    @Test
    public void userCannotDepositFundsUsingIncorrectAmountOfFunds() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser1);
        Selenide.open("/dashboard");
        $(Selectors.byText("💰 Deposit Money")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double accountBalanceBeforeDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(String.valueOf(RandomData.getRandomDepositAmountGreaterThan5000()));
        $x("//button[contains(text(), 'Deposit')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("❌ Please deposit less or equal to 5000$.")).isTrue();
        alert.accept();
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).shouldBe(Condition.visible);

        //проверка, что при повторном раскрытии дропдауна для выбора аккаунта, Balance аккаунта, выбранного в шаге 3, не изменился
        $(Selectors.byText("💰 Deposit Money")).click();
        $(".form-control.account-selector").selectOptionByValue(String.valueOf(user1Id1));
        double accountBalanceAfterDeposit = Double.parseDouble(
                $(".account-selector")
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
        assertThat(accountBalanceBeforeDeposit).isEqualTo(accountBalanceAfterDeposit);
    }
}
