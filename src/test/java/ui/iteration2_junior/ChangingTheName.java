package ui.iteration2_junior;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import api.iteration2_senior.utils.RandomData;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ChangingTheName {
    private static String authTokenUser;
    private static final String validName = RandomData.getRandomValidName();
    private static final String invalidName = RandomData.getRandomInvalidName();

    @BeforeEach
    public void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.1.148:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";


        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
        /// CREATE USER
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser = AuthenticationStep.getUserTokenStep(user1);
    }

    @Test
    public void userCanRenameThemselves() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser);
        Selenide.open("/dashboard");
        $(".profile-header").click();
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(validName);
        $x("//button[contains(text(), '\uD83D\uDCBE Save Changes')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("✅ Name updated successfully!")).isTrue();
        alert.accept();
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        Selenide.refresh();
        $(".user-name")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text(validName));
    }

    @Test
    public void userCannotRenameThemselvesUsingIncorrectName() {
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authTokenUser);
        Selenide.open("/dashboard");
        $(".profile-header").click();
        String name = $(".user-name").getText();
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(invalidName);
        $x("//button[contains(text(), '\uD83D\uDCBE Save Changes')]").click();
        Alert alert = switchTo().alert();
        assertThat(alert.getText().contains("Name must contain two words with letters only")).isTrue();
        alert.accept();
        $(Selectors.byText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        Selenide.refresh();
        $(".user-name")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text(name));
    }
}
