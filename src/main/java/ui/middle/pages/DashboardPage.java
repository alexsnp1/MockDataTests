package ui.middle.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class DashboardPage extends BasePage<DashboardPage> {
    private SelenideElement depositMoneyButton = $(Selectors.byText("💰 Deposit Money"));
    private SelenideElement makeATransferButton = $(Selectors.byText("🔄 Make a Transfer"));
    private SelenideElement profileHeaderButton = $(".profile-header");
    private SelenideElement UserDashboardText = $(Selectors.byText("User Dashboard"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public DashboardPage pressDepositMoneyButton() {
        depositMoneyButton.click();
        return this;
    }

    public DashboardPage pressMakeATransferButton() {
        makeATransferButton.click();
        return this;
    }

    public DashboardPage pressProfileHeader() {
        profileHeaderButton.click();
        return this;
    }

    public DashboardPage checkUserDashboardTextIsVisible() {
        UserDashboardText.shouldBe(Condition.visible);
        return this;
    }

}
