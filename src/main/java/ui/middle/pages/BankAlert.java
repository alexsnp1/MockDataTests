package ui.middle.pages;


import lombok.Getter;

@Getter
public enum BankAlert {
    PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000$("❌ Please deposit less or equal to 5000$."),
    TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER("❌ No user found with this account number."),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY("Name must contain two words with letters only"),
    PLEASE_ENTER_A_VALID_NAME("❌ Please enter a valid name.");


    private final String message;

    BankAlert(String message) {
        this.message = message;
    }

    public static String depositSuccessful(double amount, int accountId) {
        return "✅ Successfully deposited $" + amount
                + " to account ACC" + accountId + "!";
    }

    public static String transferSuccessful(double amount, int accountId) {
        return "✅ Successfully transferred $" + amount
                + " to account ACC" + accountId + "!";
    }
}
