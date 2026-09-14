package api.iteration2_senior.requests.steps;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.UserLoginRequest;
import api.iteration2_senior.requests.skeleton.requesters.CrudRequester;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;
import api.iteration2_senior.utils.Headers;

public class AuthenticationStep {
    public static String getUserTokenStep(AdminCreateUserRequest user) {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

        return new CrudRequester(RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.returnsOK())
                .post(userLoginRequest)
                .extract()
                .header(Headers.AUTHORIZATION);
    }
}
