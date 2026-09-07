package common.data;

import java.util.List;

public class UserSessions {
    private final List<UserSessionData> users;

    public UserSessions(List<UserSessionData> users) {
        this.users = users;
    }

    public UserSessionData get(int index) {
        return users.get(index);
    }
}
