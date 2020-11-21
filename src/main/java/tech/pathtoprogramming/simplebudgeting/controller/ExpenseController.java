package tech.pathtoprogramming.simplebudgeting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseListDto;
import tech.pathtoprogramming.simplebudgeting.service.ExpenseService;

import java.time.Month;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/users/{username}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveExpense(@PathVariable String username, @RequestBody ExpenseDto expenseDto) {
        expenseService.saveExpense(username, expenseDto);
    }

    @GetMapping("/users/{username}/expenses")
    public ExpenseListDto getAllExpensesForMonth(
            @PathVariable String username,
            @RequestParam Month month,
            @RequestParam(required = false) String category) {
        if (category != null) {
            return expenseService.getMonthlyExpensesByCategory(username, category, month);
        }

        return expenseService.getMonthlyExpenses(username, month);
    }

    @DeleteMapping("/users/{username}/expenses/{expenseId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeExpense(
            @PathVariable String username,
            @PathVariable String expenseId) {
        expenseService.removeExpense(username, expenseId);
    }
}
