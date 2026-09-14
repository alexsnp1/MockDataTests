package api.iteration2_senior.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDao {
    private int id;
    private String username;
    private String password;
    private String role;
    private String name;
    private String created_at;
    private String updated_at;
}
