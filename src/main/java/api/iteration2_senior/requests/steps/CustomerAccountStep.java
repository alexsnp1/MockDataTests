package api.iteration2_senior.requests.steps;

import api.iteration2_senior.models.CustomerAccountsGetResponse;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;

public class CustomerAccountStep {
    public static CustomerAccountsGetResponse[] getCustomerAccountResponse(String authTokenUser) {
        return new ValidatedCrudRequester<CustomerAccountsGetResponse[]>(
                RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.returnsOK())
                .get();
    }
}
