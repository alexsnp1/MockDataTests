package api.iteration2_senior.requests.skeleton.requesters;

import api.iteration2_senior.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USERS(
            "/admin/users",
            AdminCreateUserRequest.class,
            AdminCreateUserResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            CustomerAccountsGetResponse[].class
    ),
    CUSTOMER_PROFILE_GET(
            "/customer/profile",
            BaseModel.class,
            CustomerProfileGetResponse.class
    ),
    CUSTOMER_PROFILE_UPDATE(
            "/customer/profile",
            CustomerProfileUpdateRequest.class,
            CustomerProfileUpdateResponse.class
    ),
    ACCOUNTS_DEPOSIT(
            "/accounts/deposit",
            DepositFundsRequest.class,
            DepositFundsResponse.class
    ),
    ACCOUNTS_TRANSFER(
            "/accounts/transfer",
            TransferFundsRequest.class,
            TransferFundsResponse.class
    ),

    ACCOUNTS_TRANSFER_WITH_FRAUD_CHECK(
            "/accounts/transfer-with-fraud-check",
            TransferFundsRequest.class,
            TransferFundsWireMockResponse.class
    ),
    FRAUD_CHECK_STATUS(
            "/accounts/fraud-check/{transactionId}",
            BaseModel.class,
            TransferFundsResponse.class
            //FraudCheckResponse.class
    ),
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            UserCreateAccountResponse.class
    ),

    LOGIN(
            "/auth/login",
            UserLoginRequest.class,
            UserLoginResponse.class
    );
    private final String url;
    private final Class<?> requestModel;
    private final Class<?> responseModel;
}
