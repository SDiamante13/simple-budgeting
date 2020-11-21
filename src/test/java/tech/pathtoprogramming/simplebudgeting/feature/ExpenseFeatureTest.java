package tech.pathtoprogramming.simplebudgeting.feature;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tech.pathtoprogramming.simplebudgeting.document.Expense;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.pathtoprogramming.simplebudgeting.Constants.EXPENSES_COLLECTION;

@SpringBootTest
@AutoConfigureDataMongo
class ExpenseFeatureTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final String USERNAME = "sdiamante13";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        resetDatabase();
    }

    @Test
    void getAllExpenses_returns200() throws Exception {
        initializeDatabase();

        mockMvc.perform(get("/api/v1/users/{username}/expenses?month=May", USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses", hasSize(2)))
                .andExpect(jsonPath("$.expenses[0].description", is("Drinks with the boys")))
                .andExpect(jsonPath("$.expenses[1].transactionDate", is("5/21/2020")));
    }

    @Test
    void addExpense_returns201() throws Exception {
        initializeDatabase();

        ExpenseDto expense = ExpenseDto.builder()
                .amount(12.99)
                .category("FOOD")
                .description("Burger King")
                .transactionDate("12/1/2020")
                .build();

        mockMvc.perform(post("/api/v1/users/{username}/expenses", USERNAME)
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated());
    }

    @Test
    void removeExpense_returns204_whenExpenseIsSuccessfullyDeleted() throws Exception {
        List<Expense> expenses = initializeDatabase();

        mockMvc.perform(delete("/api/v1/users/{username}/expenses/{expenseId}", USERNAME, expenses.get(0).getExpenseId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeExpense_returns404_andAnErrorMessage_whenDeletionIsNotSuccessful() throws Exception {
        initializeDatabase();

        mockMvc.perform(delete("/api/v1/users/{username}/expenses/{expenseId}", USERNAME, "22"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Expense with id 22 could not be deleted")));
    }

    private void resetDatabase() {
        mongoTemplate.dropCollection(EXPENSES_COLLECTION);
    }

    private List<Expense> initializeDatabase() {
        Expense expense1 = Expense.builder()
                .username(USERNAME)
                .category("OTHER")
                .amount(65.50)
                .description("Drinks with the boys")
                .transactionDate(LocalDate.of(2020, 5, 28))
                .build();

        Expense expense2 = Expense.builder()
                .username(USERNAME)
                .category("SHOPPING")
                .amount(127.88)
                .description("New clothes")
                .transactionDate(LocalDate.of(2020, 5, 21))
                .build();

        return Arrays.asList(expenseRepository.addExpense(expense1), expenseRepository.addExpense(expense2));
    }
}

