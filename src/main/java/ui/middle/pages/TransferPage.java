package ui.middle.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement recepientNameInput = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recepientAccountNumberInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement confirmationCheckbox = $(Selectors.byId("confirmCheck"));
    private SelenideElement sendTransferButton = $x("//button[contains(text(), '\uD83D\uDE80 Send Transfer')]");

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage fillAllTransferFields(int userId, double transferAmount) {
        String name = nameOfUser.getText();
        recepientNameInput.sendKeys(name);
        recepientAccountNumberInput.sendKeys("ACC" + userId);
        amountInput.sendKeys(String.valueOf(transferAmount));
        confirmationCheckbox.click();
        return this;
    }
    public TransferPage pressTransferButton() {
        sendTransferButton
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();
        return this;
    }

    public TransferPage shouldHaveSendTransferButton() {
        sendTransferButton.shouldBe(Condition.visible);
        return this;
    }


}
