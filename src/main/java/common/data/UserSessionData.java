package common.data;

import api.iteration2_senior.models.AdminCreateUserRequest;
import lombok.Getter;
@Getter
public class UserSessionData {
    private final AdminCreateUserRequest user;
    private final String authToken;
    private final int accountId;

    public UserSessionData(AdminCreateUserRequest user, String authToken, int accountId) {
        this.user = user;
        this.authToken = authToken;
        this.accountId = accountId;
    }


}
