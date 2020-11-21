package tech.pathtoprogramming.simplebudgeting.service;

import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseListDto;

import java.time.Month;

public interface ExpenseService {
    void saveExpense(String username, ExpenseDto expenseDto);
    ExpenseListDto getExpenseHistory(String username);
    ExpenseListDto getMonthlyExpensesByCategory(String username, String category, Month month);
    void removeExpense(String username, String expenseId);
}
