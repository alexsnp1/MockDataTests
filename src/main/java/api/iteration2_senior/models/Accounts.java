package api.iteration2_senior.models;

import api.iteration2_senior.models.Transactions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Accounts {
    private int id;
    private String accountNumber;
    private double balance;
    private List<Transactions> transactions;
}
