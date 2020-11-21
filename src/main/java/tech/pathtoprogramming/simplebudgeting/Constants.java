package tech.pathtoprogramming.simplebudgeting;

import org.springframework.data.mongodb.core.query.Query;

import java.time.format.DateTimeFormatter;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class Constants {
    public static final String USERS_COLLECTION = "users";
    public static final String EXPENSES_COLLECTION = "expenses";
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");


    public static Query findByUsernameQuery(String username) { return query(where("username").is(username)); }
    public static Query findByExpenseIdQuery(String expenseId) { return query(where("expenseId").is(expenseId)); }
}
