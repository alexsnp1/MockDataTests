package api.iteration2_senior.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transactions {
    private int id;
    private double amount;
    private String type;
    private String timestamp;
    private String timestampAsString;
    private int relatedAccountId;
    private int amountAsDouble;
}
