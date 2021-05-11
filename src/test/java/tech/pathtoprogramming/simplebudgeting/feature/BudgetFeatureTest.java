package tech.pathtoprogramming.simplebudgeting.feature;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.Expense;
import tech.pathtoprogramming.simplebudgeting.document.User;

import java.time.LocalDate;
import java.time.Month;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.pathtoprogramming.simplebudgeting.Constants.EXPENSES_COLLECTION;
import static tech.pathtoprogramming.simplebudgeting.Constants.USERS_COLLECTION;

@SpringBootTest
@AutoConfigureDataMongo
class BudgetFeatureTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mockMvc;

    private static final String USERNAME = "bob123";

    private final Month month = LocalDate.now().getMonth();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        initializeDatabase();
    }

    @Test
    void getBudgetOverview_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/users/bob123/budget-overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month", Matchers.is(month.name())))
                .andExpect(jsonPath("$.budgetStats[0].category", Matchers.is("Shopping")))
                .andExpect(jsonPath("$.budgetStats[0].currentAmount", Matchers.is(300.00)))
                .andExpect(jsonPath("$.budgetStats[0].maxThreshold", Matchers.is(400)));
    }

    private void initializeDatabase() {
        Expense expense1 = Expense.builder()
                .username(USERNAME)
                .category("Shopping")
                .amount(100.00)
                .description("Table")
                .transactionDate(LocalDate.of(2021, month, 2))
                .build();

        Expense expense2 = Expense.builder()
                .username(USERNAME)
                .category("Shopping")
                .amount(200.00)
                .description("New clothes")
                .transactionDate(LocalDate.of(2021, month, 5))
                .build();

        User user = User.builder()
                .username(USERNAME)
                .budgetAllowances(singletonList(BudgetAllowance.builder()
                        .category("Shopping")
                        .maxThreshold(400)
                        .build()))
                .build();

        mongoTemplate.insert(expense1, EXPENSES_COLLECTION);
        mongoTemplate.insert(expense2, EXPENSES_COLLECTION);
        mongoTemplate.insert(user, USERS_COLLECTION);
    }

}
