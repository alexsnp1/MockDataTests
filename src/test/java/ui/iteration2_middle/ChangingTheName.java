package ui.iteration2_middle;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.CustomerProfileStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import api.iteration2_senior.utils.RandomData;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.DashboardPage;
import ui.middle.pages.EditProfilePage;

public class ChangingTheName extends BaseUiTest {
    private static String authTokenUser;
    private static final String validName = RandomData.getRandomValidName();
    private static final String invalidName = RandomData.getRandomInvalidName();
    private DashboardPage dashboardPage = new DashboardPage();
    private EditProfilePage editProfilePage = new EditProfilePage();

    @BeforeEach
    public void prepareTestData() {
        /// CREATE USER
        AdminCreateUserRequest user1 = UserCreationStep.createUserRequest();
        authTokenUser = AuthenticationStep.getUserTokenStep(user1);
    }

    @Test
    public void userCanRenameThemselves() {
        authAsUser(authTokenUser);
        dashboardPage.open().pressProfileHeader().getPage(EditProfilePage.class).shouldHaveEditProfileHeader()
//                .changeName(validName).checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .shouldHaveEditProfileHeader();
        Selenide.refresh();
        editProfilePage.nameShouldBeVisible(validName);
        softly.assertThat(CustomerProfileStep.getCustomerProfileResponse(authTokenUser).getName()).isEqualTo(validName);
    }

    @Test
    public void userCannotRenameThemselvesUsingIncorrectName() {
        authAsUser(authTokenUser);
        dashboardPage.open().pressProfileHeader();
        String name = editProfilePage.getNameOfUser();
//        editProfilePage.shouldHaveEditProfileHeader().changeName(invalidName)
//                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage())
//                .shouldHaveEditProfileHeader();
        Selenide.refresh();
        editProfilePage.nameShouldBeVisible(name);
        softly.assertThat(CustomerProfileStep.getCustomerProfileResponse(authTokenUser).getName()).isNull();
    }
}
