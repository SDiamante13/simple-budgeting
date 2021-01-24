package tech.pathtoprogramming.simplebudgeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.pathtoprogramming.simplebudgeting.dto.*;
import tech.pathtoprogramming.simplebudgeting.exception.handler.GlobalExceptionHandler;
import tech.pathtoprogramming.simplebudgeting.service.ExpenseService;
import tech.pathtoprogramming.simplebudgeting.service.UserService;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BudgetControllerTest {

    @Mock
    UserService mockUserService;

    @Mock
    ExpenseService mockExpenseService;

    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    private static final String USERNAME = "bob123";

    @BeforeEach
    void setUp() {
        BudgetController budgetController = new BudgetController(mockUserService, mockExpenseService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(budgetController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getBudgetOverview_returnsABudgetOverviewDto() throws Exception {
        when(mockUserService.getUser(USERNAME)).thenReturn(UserDto.builder()
                .id("1")
                .username(USERNAME)
                .budgetAllowances(Arrays.asList(BudgetAllowanceDto.builder()
                                .category("Groceries")
                                .maxThreshold(500)
                                .build(),
                        BudgetAllowanceDto.builder()
                                .category("Travel")
                                .maxThreshold(200)
                                .build()))
                .build());

        List<ExpenseDto> expenses = Arrays.asList(ExpenseDto.builder()
                        .category("Travel")
                        .amount(60.00)
                        .build(),
                ExpenseDto.builder()
                        .category("Travel")
                        .amount(120.00)
                        .build(),
                ExpenseDto.builder()
                        .category("Groceries")
                        .amount(120.00)
                        .build(),
                ExpenseDto.builder()
                        .category("Travel")
                        .amount(50.00)
                        .build(),
                ExpenseDto.builder()
                        .category("Groceries")
                        .amount(140.00)
                        .build());

        when(mockExpenseService.getMonthlyExpenses(USERNAME, Month.JANUARY)).thenReturn(ExpenseListDto.builder()
                .username(USERNAME)
                .expenses(expenses)
                .build());

        BudgetOverviewDto expectedBudgetOverview = BudgetOverviewDto.builder()
                .username(USERNAME)
                .month("JANUARY")
                .budgetStats(Arrays.asList(BudgetStat.builder()
                        .category("Groceries")
                        .currentAmount(260.00)
                        .maxThreshold(500)
                        .build(),
                        BudgetStat.builder()
                                .category("Travel")
                                .currentAmount(230.00)
                                .maxThreshold(200)
                                .build()
                ))
                .build();

        String actualBudgetOverview = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/bob123/budget-overview"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(actualBudgetOverview).isEqualTo(mapper.writeValueAsString(expectedBudgetOverview));
    }
}
