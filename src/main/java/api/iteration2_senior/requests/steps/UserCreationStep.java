package api.iteration2_senior.requests.steps;

import api.iteration2_senior.generators.RandomModelGenerator;
import api.iteration2_senior.models.AdminCreateUserRequest;
import api.iteration2_senior.models.UserRole;
import api.iteration2_senior.requests.skeleton.requesters.CrudRequester;
import api.iteration2_senior.requests.skeleton.requesters.Endpoint;
import api.iteration2_senior.specs.RequestSpecs;
import api.iteration2_senior.specs.ResponseSpecs;

import java.util.UUID;

public class UserCreationStep {
    public static AdminCreateUserRequest createUserRequest() {
        AdminCreateUserRequest credentials = RandomModelGenerator.generate(AdminCreateUserRequest.class);

        AdminCreateUserRequest user = AdminCreateUserRequest.builder()
                .username(uniqueUsername())
                .password(credentials.getPassword())
                .role(UserRole.USER.toString())
                .build();
        new CrudRequester(
                RequestSpecs.adminAuthSpec(),
                Endpoint.ADMIN_USERS,
                ResponseSpecs.returnsCreated())
                .post(user);
        return user;
    }
    private static String uniqueUsername() {
        return "u" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 14);
    }
}
