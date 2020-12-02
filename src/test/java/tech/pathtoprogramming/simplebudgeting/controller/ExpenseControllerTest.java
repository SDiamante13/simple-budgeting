package tech.pathtoprogramming.simplebudgeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.exception.DeletionException;
import tech.pathtoprogramming.simplebudgeting.exception.handler.GlobalExceptionHandler;
import tech.pathtoprogramming.simplebudgeting.service.ExpenseService;

import static java.time.Month.FEBRUARY;
import static java.time.Month.MARCH;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService mockExpenseService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ExpenseController expenseController = new ExpenseController(mockExpenseService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(expenseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void saveExpense_callsExpenseServiceToSaveExpenseInTheDatabase() throws Exception {
        ExpenseDto expense = ExpenseDto.builder().build();

        mockMvc.perform(post("/api/v1/users/bob123/expenses")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().is(201));

        verify(mockExpenseService).saveExpense("bob123", expense);
    }

    @Test
    void getAllExpensesForMonth() throws Exception {
        mockMvc.perform(get("/api/v1/users/bob123/expenses?month=February"))
                .andExpect(status().is(200));

        verify(mockExpenseService).getMonthlyExpenses("bob123", FEBRUARY);
    }

    @Test
    void getAllExpensesForMonth_takesACategoryParameter() throws Exception {
        mockMvc.perform(get("/api/v1/users/bob123/expenses?month=March&category=SHOPPING"))
                .andExpect(status().is(200));

        verify(mockExpenseService).getMonthlyExpensesByCategory("bob123", "SHOPPING", MARCH);
    }

    @Test
    void removeExpense() throws Exception {
        mockMvc.perform(delete("/api/v1/users/bob123/expenses/1"))
                .andExpect(status().is(204));

        verify(mockExpenseService).removeExpense("bob123", "1");
    }

    @Test
    void removeExpense_returnsErrorMessage_whenDeletionCanNotBeCarriedOut() throws Exception {
        doThrow(new DeletionException("42")).when(mockExpenseService).removeExpense("bob123", "42");

        mockMvc.perform(delete("/api/v1/users/bob123/expenses/42"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message", is("Expense with id 42 could not be deleted")));
    }
}