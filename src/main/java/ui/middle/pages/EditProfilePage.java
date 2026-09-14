package ui.middle.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private SelenideElement editProfileHeader = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveChangesButton = $x("//button[contains(text(), '\uD83D\uDCBE Save Changes')]");

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage shouldHaveEditProfileHeader() {
        editProfileHeader.shouldBe(Condition.visible);
        return this;
    }

    public EditProfilePage enterNewName(String newName) {
        newNameInput.click();
        newNameInput.clear();
        newNameInput.sendKeys(newName);
        newNameInput.shouldHave(Condition.value(newName));
        return this;
    }

    public EditProfilePage pressSaveChangesButton() {
        saveChangesButton.shouldBe(Condition.interactable).click();
        return this;
    }

    public EditProfilePage nameShouldBeVisible(String name) {
        nameOfUser.shouldBe(Condition.visible).shouldHave(Condition.text(name));
        return this;
    }

    public String getNameOfUser() {
        return nameOfUser.getText();
    }


}
