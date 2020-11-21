package tech.pathtoprogramming.simplebudgeting.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import tech.pathtoprogramming.simplebudgeting.document.Expense;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static tech.pathtoprogramming.simplebudgeting.Constants.*;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Expense addExpense(Expense expense) {
        return mongoTemplate.insert(expense, EXPENSES_COLLECTION);
    }

    @Override
    public List<Expense> findAllExpensesByUsername(String username) {
        return mongoTemplate.find(findByUsernameQuery(username), Expense.class);
    }

    @Override
    public Expense updateExpense(Expense expense) {
        return mongoTemplate.save(expense, EXPENSES_COLLECTION);
    }

    @Override
    public void deleteExpense(String username, String expenseId) {
        Query usernameWithExpenseIdQuery = findByExpenseIdQuery(expenseId).addCriteria(where("username").is(username));
        mongoTemplate.remove(usernameWithExpenseIdQuery, Expense.class);
    }

    @Override
    public List<Expense> getExpensesByCategoryAndMonth(String username, String category, Month month) {
        LocalDate beginningOfTheMonth = LocalDate.of(LocalDate.now().getYear(), month, 1);
        LocalDate endOfMonth = LocalDate.of(LocalDate.now().getYear(), month, month.maxLength());

        Query query = findByUsernameQuery(username)
                .addCriteria(where("category").is(category))
                .addCriteria(where("transactionDate")
                        .gte(beginningOfTheMonth)
                        .lte(endOfMonth)
                );
        return mongoTemplate.find(query, Expense.class);
    }

    @Override
    public List<Expense> getExpensesByMonth(String username, Month month) {
        LocalDate beginningOfTheMonth = LocalDate.of(LocalDate.now().getYear(), month, 1);
        LocalDate endOfMonth = LocalDate.of(LocalDate.now().getYear(), month, month.maxLength());

        Query query = findByUsernameQuery(username)
                .addCriteria(where("transactionDate")
                        .gte(beginningOfTheMonth)
                        .lte(endOfMonth)
                );
        return mongoTemplate.find(query, Expense.class);
    }
}