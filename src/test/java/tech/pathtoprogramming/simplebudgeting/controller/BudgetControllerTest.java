package tech.pathtoprogramming.simplebudgeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.pathtoprogramming.simplebudgeting.document.Expense;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.service.ExpenseService;
import tech.pathtoprogramming.simplebudgeting.service.UserService;

import java.time.Month;

import static java.time.Month.FEBRUARY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private UserService mockUserService;

    @Mock
    private ExpenseService mockExpenseService;

    private BudgetController budgetController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        budgetController = new BudgetController(mockUserService, mockExpenseService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(budgetController)
//                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    void addExpense() throws Exception {
        ExpenseDto expense = ExpenseDto.builder().build();

        mockMvc.perform(post("/api/v1/users/bob123/expenses")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().is(201));
    }

    @Test
    void getAllExpensesForMonth() throws Exception {
        mockMvc.perform(get("/api/v1/users/bob123/expenses?month=February"))
                .andExpect(status().is(200));

        verify(mockExpenseService).getMonthlyExpenses("bob123", FEBRUARY);
    }

    @Test
    void removeExpense() throws Exception {
        mockMvc.perform(delete("/api/v1/users/bob123/expenses/1"))
                .andExpect(status().is(204));

        verify(mockExpenseService).removeExpense("bob123", "1");
    }
}