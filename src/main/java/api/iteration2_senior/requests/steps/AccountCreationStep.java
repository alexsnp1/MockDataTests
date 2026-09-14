package api.iteration2_senior.requests.steps;

import api.iteration2_senior.models.UserCreateAccountResponse;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;

public class AccountCreationStep {
    public static UserCreateAccountResponse userCreateAccount(String authTokenUser) {
        return new ValidatedCrudRequester<UserCreateAccountResponse>(
                RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.ACCOUNTS,
                ResponseSpecs.returnsCreated())
                .post();
    }
}
