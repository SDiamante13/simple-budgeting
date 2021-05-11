package tech.pathtoprogramming.simplebudgeting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tech.pathtoprogramming.simplebudgeting.dto.*;
import tech.pathtoprogramming.simplebudgeting.service.ExpenseService;
import tech.pathtoprogramming.simplebudgeting.service.UserService;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final UserService userService;
    private final ExpenseService expenseService;

    @GetMapping("/api/v1/users/{username}/budget-overview")
    public BudgetOverviewDto getBudgetOverview(@PathVariable String username) {
        Month currentMonth = LocalDate.now().getMonth();
        List<BudgetAllowanceDto> budgetAllowances = userService.getUser(username).getBudgetAllowances();
        ExpenseListDto expenses = expenseService.getMonthlyExpenses(username, currentMonth);

        return BudgetOverviewDto.builder()
                .username(username)
                .month(currentMonth.name())
                .budgetStats(getBudgetStats(budgetAllowances, expenses))
                .build();
    }

    private List<BudgetStat> getBudgetStats(List<BudgetAllowanceDto> budgetAllowances, ExpenseListDto expenses) {
        Map<String, Double> categoryTotalMap = new HashMap<>();

        for (ExpenseDto expense : expenses.getExpenses()) {
            categoryTotalMap.put(expense.getCategory(), categoryTotalMap.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        List<BudgetStat> budgetStats = new ArrayList<>();

        for (BudgetAllowanceDto budgetAllowance : budgetAllowances) {
            budgetStats.add(BudgetStat.builder()
                    .category(budgetAllowance.getCategory())
                    .currentAmount(categoryTotalMap.get(budgetAllowance.getCategory()))
                    .maxThreshold(budgetAllowance.getMaxThreshold())
                    .build());
        }

        return budgetStats;
    }

}
