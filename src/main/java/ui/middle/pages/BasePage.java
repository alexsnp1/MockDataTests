package ui.middle.pages;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.AlertNotFoundError;
import org.openqa.selenium.Alert;

import java.time.Duration;
import java.util.Arrays;

import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage<T extends BasePage> {
    private SelenideElement accountDropDown = $("select.account-selector");
    protected SelenideElement nameOfUser = $(".user-name");
    private static final int MAX_ALERT_ATTEMPTS = 3;
    private static final int SLEEP_TIME = 5000;
    private static final Duration ALERT_WAIT_PER_ATTEMPT =
            Duration.ofSeconds(30);

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public <T extends BasePage> T getPage(Class<T> pageClass) {
        return Selenide.page(pageClass);
    }

    public T checkAlertMessageAndAccept(String... expectedMessages) {
        for (int attempt = 1; attempt < MAX_ALERT_ATTEMPTS; attempt++) {
            try {
                Alert alert = switchTo().alert(ALERT_WAIT_PER_ATTEMPT);
                String actualMessage = alert.getText();
                if (!Arrays.asList(expectedMessages).contains(actualMessage)) {
                    alert.accept();

                    throw new AssertionError(
                            "Unexpected alert message. Expected one of: "
                                    + Arrays.toString(expectedMessages)
                                    + ", but was: "
                                    + actualMessage
                    );
                }
                alert.accept();
                return (T) this;
            } catch (AlertNotFoundError ignored) {
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }
        throw new AssertionError("Alert was not found");
    }

    public T selectAccount(int userId) {
        String accountValue = String.valueOf(userId);

        accountDropDown
                .$(Selectors.byAttribute("value", accountValue))
                .shouldBe(Condition.exist, Duration.ofSeconds(30));
        accountDropDown.selectOptionByValue(accountValue);

        return (T) this;
    }

    public double getSelectedAccountBalance() {
        return Double.parseDouble(
                accountDropDown
                        .getSelectedOption()
                        .getText()
                        .replaceAll(".*\\$([0-9.]+).*", "$1"));
    }

    public static void authAsUser(String authTokenUser) {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser);
    }
}
