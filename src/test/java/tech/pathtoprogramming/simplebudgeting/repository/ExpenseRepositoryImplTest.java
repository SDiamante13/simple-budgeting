package tech.pathtoprogramming.simplebudgeting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.pathtoprogramming.simplebudgeting.document.Expense;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.MAY;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.pathtoprogramming.simplebudgeting.Constants.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ExpenseRepositoryImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private ExpenseRepository expenseRepository;

    private static final String USERNAME = "bob123";
    private static final String NEW_USERNAME = "sam55";

    private final LocalDate todaysDate = LocalDate.of(2020, 5, 24);

    private Expense expense1, expense2, expense3, expense4, expense5;

    @BeforeEach
    void setUp() {
        expenseRepository = new ExpenseRepositoryImpl(mongoTemplate);

        resetData();

        initializeData();
    }

    @Test
    void addExpense_savesNewExpenseToTheDatabase() {
        expenseRepository.addExpense(createExpense(NEW_USERNAME, LocalDate.of(2020, 5, 23), 1200.00, "SHOPPING", "Workout Equipment"));

        List<Expense> expenses = getExpensesForUser(NEW_USERNAME);

        assertThat(expenses.size()).isEqualTo(2);
    }

    @Test
    void findAllExpensesByUsername_returnsExpensesBelongingToTheGivenUsername() {
        List<Expense> actualExpenses = expenseRepository.findAllExpensesByUsername(USERNAME);

        assertThat(actualExpenses.size()).isEqualTo(4);
    }

    @Test
    void updateExpense_retrievesCurrentExpense_andUpdatesItInTheDatabase() {
        Expense updatedExpense4 = expense4.toBuilder().transactionDate(LocalDate.of(2020, 4, 11)).build();

        Expense updatedExpense = expenseRepository.updateExpense(updatedExpense4);

        assertThat(updatedExpense.getTransactionDate()).isEqualTo(LocalDate.of(2020, 4, 11));
    }

    @Test
    void deleteExpense_removesOneExpenseFromGivenUser() {
        expenseRepository.deleteExpense(USERNAME, expense3.getExpenseId());

        List<Expense> expenses = getExpensesForUser(USERNAME);

        assertThat(expenses.size()).isEqualTo(3);
        assertThat(expenses.get(2).getCategory()).isEqualTo("UTILITIES");
    }

    @Test
    void getExpensesByCategoryAndMonth() {
        List<Expense> actualExpenses = expenseRepository.getExpensesByCategoryAndMonth(USERNAME, "UTILITIES", MAY);

        assertThat(actualExpenses.size()).isEqualTo(1);
        assertThat(actualExpenses.get(0).getDescription()).isEqualTo("Water Bill");
    }

    private List<Expense> getExpensesForUser(String username) {
        return mongoTemplate.find(findByUsernameQuery(username), Expense.class);
    }

    private Expense createExpense(String username, LocalDate transactionDate, double amount, String category, String description) {
        return Expense.builder()
                .username(username)
                .amount(amount)
                .transactionDate(transactionDate)
                .category(category)
                .description(description)
                .build();
    }

    private void resetData() {
        mongoTemplate.dropCollection(USERS_COLLECTION);
        mongoTemplate.dropCollection(EXPENSES_COLLECTION);
    }

    private void initializeData() {
        expense1 = mongoTemplate.insert(createExpense(USERNAME, todaysDate, 33.99, "SHOPPING", "Table"), EXPENSES_COLLECTION);
        expense2 = mongoTemplate.insert(createExpense(USERNAME, todaysDate, 122.76, "UTILITIES", "Water Bill"), EXPENSES_COLLECTION);
        expense3 = mongoTemplate.insert(createExpense(USERNAME, LocalDate.of(2020, 5, 23), 112.99, "ENTERTAINMENT", "Bowling"), EXPENSES_COLLECTION);
        expense4 = mongoTemplate.insert(createExpense(NEW_USERNAME, LocalDate.of(2020, 5, 1), 120.00, "GROCERIES", ""), EXPENSES_COLLECTION);
        expense5 = mongoTemplate.insert(createExpense(USERNAME, LocalDate.of(2020, 4, 15), 76.99, "UTILITIES", "Electric Bill"), EXPENSES_COLLECTION);
    }
}