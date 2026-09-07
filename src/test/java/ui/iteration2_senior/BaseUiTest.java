package ui.iteration2_senior;

import api.iteration2_senior.tests.BaseTest;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import api.iteration2_senior.configs.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = true;
    }

    @AfterEach
    public void closeUiDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.closeWebDriver();
        }
    }
}