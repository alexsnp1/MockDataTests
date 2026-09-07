package api.iteration2_senior.tests;

import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.CustomerProfileGetResponse;
import api.iteration2_senior.models.CustomerProfileUpdateRequest;
import api.iteration2_senior.models.CustomerProfileUpdateResponse;
import api.iteration2_senior.models.comparison.ModelAssertions;
import api.iteration2_senior.requests.skeleton.requesters.CrudRequester;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.iteration2_senior.requests.steps.AuthenticationStep;
import api.iteration2_senior.requests.steps.CustomerProfileStep;
import api.iteration2_senior.requests.steps.UserCreationStep;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;
import api.iteration2_senior.utils.RandomData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ChangingTheNameApi extends BaseTest {
    private String authTokenUser;
    AdminCreateUserRequest user;
    private final String validName = RandomData.getRandomValidName();

    @BeforeEach
    public void setUp() {
        user = UserCreationStep.createUserRequest();
        authTokenUser = AuthenticationStep.getUserTokenStep(user);
    }

    @Test
    public void userCanRenameThemselves() {
        CustomerProfileUpdateRequest customerProfileUpdateRequest = CustomerProfileUpdateRequest
                .builder().name(validName).build();

        CustomerProfileUpdateResponse customerProfileUpdateResponse = new ValidatedCrudRequester<CustomerProfileUpdateResponse>(RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.CUSTOMER_PROFILE_UPDATE,
                ResponseSpecs.profileUpdatedSuccessfully())
                .put(customerProfileUpdateRequest);
//        softly.assertThat(customerProfileUpdateRequest.getName()).isEqualTo(customerProfileUpdateResponse.getCustomer().getName());
        ModelAssertions.assertThatModels(customerProfileUpdateRequest, customerProfileUpdateResponse).match();
    }

    @ParameterizedTest
    @ValueSource(strings = {"JohnSmith", "a", "", " ", "John Smith2", "John Smith?"})
    public void userCannotRenameThemselvesUsingIncorrectName(String name) {
        CustomerProfileGetResponse customerProfileGetResponseOld =
                CustomerProfileStep.getCustomerProfileResponse(authTokenUser);

        CustomerProfileUpdateRequest customerProfileUpdateRequest = CustomerProfileUpdateRequest
                .builder().name(name).build();
        new CrudRequester(RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.CUSTOMER_PROFILE_UPDATE,
                ResponseSpecs.invalidNameError())
                .put(customerProfileUpdateRequest)
                .extract()
                .asString();

        CustomerProfileGetResponse customerProfileGetResponseNew =
                CustomerProfileStep.getCustomerProfileResponse(authTokenUser);
//        softly.assertThat(customerProfileGetResponseOld.getName()).isEqualTo(customerProfileGetResponseNew.getName());
        ModelAssertions.assertThatModels(customerProfileGetResponseOld, customerProfileGetResponseNew).match();
    }

    @Test
    public void userCannotRenameThemselvesUsingNullName() {
        CustomerProfileGetResponse customerProfileGetResponseOld =
                CustomerProfileStep.getCustomerProfileResponse(authTokenUser);

        CustomerProfileUpdateRequest customerProfileUpdateRequest = CustomerProfileUpdateRequest
                .builder().name(null).build();
        new CrudRequester(RequestSpecs.userAuthSpec(authTokenUser),
                Endpoint.CUSTOMER_PROFILE_UPDATE,
                ResponseSpecs.returnsInternalServerError())
                .put(customerProfileUpdateRequest);

        CustomerProfileGetResponse customerProfileGetResponseNew =
                CustomerProfileStep.getCustomerProfileResponse(authTokenUser);
//        softly.assertThat(customerProfileGetResponseOld.getName()).isEqualTo(customerProfileGetResponseNew.getName());
        ModelAssertions.assertThatModels(customerProfileGetResponseOld, customerProfileGetResponseNew).match();
    }
}
