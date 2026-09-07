package api.iteration2_senior.dao;

import api.iteration2_senior.database.Condition;
import api.iteration2_senior.database.DBRequest;


public class DataBaseSteps {
    public enum Table {
        CUSTOMERS("customers"),
        ACCOUNTS("accounts");

        Table(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return name;
        }

    }
        public static UserDao getUserByUsername(String username) {
            return DBRequest.builder()
                    .requestType(DBRequest.RequestType.SELECT)
                    .table(Table.CUSTOMERS.getName())
                    .where(Condition.equalTo("username", username))
                    .extractAs(UserDao.class);
                }
    public static AccountDao getAccountByAccountNumber(String accountNumber) {
        return DBRequest.builder().
                requestType(DBRequest.RequestType.SELECT)
                .table(Table.ACCOUNTS.getName())
                .where(Condition.equalTo("account_number", accountNumber))
                .extractAs(AccountDao.class);
                }
    }
//    public static UserDao getUserByUsername(String username) {
//       System.out.println(("Get user from database by username: " + username));
//            return DBRequest.builder()
//                    .requestType(DBRequest.RequestType.SELECT)
//                    .table(Table.CUSTOMERS.getName())
//                    .where(Condition.)
//                };
//    }
//    public static UserDao getAccountByAccountNumber(String accountNumber) {
//        return System.out.println(("Get account from database by account number: " + accountNumber,
//                () -> {
//                    return DBRequest.builder().
//                });
//    }

