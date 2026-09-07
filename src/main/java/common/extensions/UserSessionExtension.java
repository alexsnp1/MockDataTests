package common.extensions;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.steps.AccountCreationStep;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import common.annotations.UserSession;
import common.data.UserSessionData;
import common.data.UserSessions;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.List;

import static ui.middle.pages.BasePage.authAsUser;

public class UserSessionExtension implements BeforeEachCallback, ParameterResolver {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(UserSessionExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserSession annotation = getUserSessionAnnotation(context);
        if (annotation == null) {
            return;
        }

        int userCount = annotation.value();
        List<UserSessionData> users = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            AdminCreateUserRequest user = UserCreationStep.createUserRequest();
            String authToken = AuthenticationStep.getUserTokenStep(user);
            UserCreateAccountResponse account = AccountCreationStep.userCreateAccount(authToken);
            UserSessionData session = new UserSessionData(user, authToken, account.getId());
            users.add(session);
        }
        context.getStore(NAMESPACE).put("users", users);

        int authUserIndex = annotation.authUser();
        if (authUserIndex < 0 || authUserIndex >= users.size()) {
            throw new ExtensionConfigurationException(
                    "authUser must be between 0 and " + (users.size() - 1)
            );
        }
        authAsUser(users.get(authUserIndex).getAuthToken());
    }
    private UserSession getUserSessionAnnotation(ExtensionContext context) {

        UserSession annotation = context.getTestMethod()
                .map(method -> method.getAnnotation(UserSession.class))
                .orElse(null);

        if (annotation == null) {
            annotation = context.getTestClass()
                    .map(clazz -> clazz.getAnnotation(UserSession.class))
                    .orElse(null);
        }
        return annotation;
    }
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(UserSessionData.class) || parameterContext.getParameter().getType().equals(UserSessions.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext, ExtensionContext extensionContext) {
        List<UserSessionData> users = extensionContext.getStore(NAMESPACE)
                .get("users", List.class);

        if (parameterContext.getParameter().getType().equals(UserSessionData.class)) {
            return users.get(0);
        }

        if (parameterContext.getParameter().getType().equals(UserSessions.class)) {
            return new UserSessions(users);
        }

        throw new ParameterResolutionException(
                "Unsupported parameter type: " + parameterContext.getParameter().getType()
        );
    }
}
