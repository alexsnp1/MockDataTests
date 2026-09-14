package ui.middle.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $x("//button[contains(text(), 'Deposit')]");
    private SelenideElement depositMoneyHeader = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage setDepositAmount(double depositAmount) {
        amountInput.sendKeys(String.valueOf(depositAmount));
        return this;
    }
    public DepositPage depositButtonClick() {
        depositButton
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();
        return this;
    }


    public DepositPage shouldHaveDepositMoneyHeader() {
        depositMoneyHeader.shouldBe(Condition.visible);
        return this;
    }
}
