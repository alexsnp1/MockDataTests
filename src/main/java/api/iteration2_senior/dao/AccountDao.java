package api.iteration2_senior.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDao {
    private int id;
    private String account_number;
    private double balance;
    private int customer_id;
    private String created_at;
    private String updated_at;
}
