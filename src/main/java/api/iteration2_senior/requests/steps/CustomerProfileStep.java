package api.iteration2_senior.requests.steps;

import api.iteration2_senior.models.CustomerProfileGetResponse;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;

public class CustomerProfileStep {
    public static CustomerProfileGetResponse getCustomerProfileResponse(String authTokenUser) {
        return new ValidatedCrudRequester<CustomerProfileGetResponse>(
                RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.CUSTOMER_PROFILE_GET,
                ResponseSpecs.returnsOK())
                .get();
    }
}
