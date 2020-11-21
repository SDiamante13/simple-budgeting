package tech.pathtoprogramming.simplebudgeting.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tech.pathtoprogramming.simplebudgeting.document.Expense;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseDto;
import tech.pathtoprogramming.simplebudgeting.dto.ExpenseListDto;
import tech.pathtoprogramming.simplebudgeting.repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static tech.pathtoprogramming.simplebudgeting.Constants.dateFormatter;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveExpense(String username, ExpenseDto expenseDto) {
        Expense expense = convertToDocument(username, expenseDto);
        expenseRepository.addExpense(expense);
    }

    @Override
    public ExpenseListDto getExpenseHistory(String username) {
        List<Expense> expenses = expenseRepository.findAllExpensesByUsername(username);

        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ExpenseListDto.builder()
                .username(username)
                .expenses(expenseDtos)
                .build();
    }

    @Override
    public ExpenseListDto getMonthlyExpensesByCategory(String username, String category, Month month) {
        List<Expense> monthlyExpenses = expenseRepository.getExpensesByCategoryAndMonth(username, category, month);
        List<ExpenseDto> expenseDtos = monthlyExpenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ExpenseListDto.builder()
                .username(username)
                .expenses(expenseDtos)
                .build();
    }

    @Override
    public void removeExpense(String username, String expenseId) {
        expenseRepository.deleteExpense(username, expenseId);
    }

    private Expense convertToDocument(String username, ExpenseDto expenseDto) {
        Expense expense = modelMapper.map(expenseDto, Expense.class);
        LocalDate transactionDate = LocalDate.parse(expenseDto.getTransactionDate(), dateFormatter);
        return expense.toBuilder()
                .username(username)
                .transactionDate(transactionDate)
                .build();
    }

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto expenseDto = modelMapper.map(expense, ExpenseDto.class);
        String transactionDate = expense.getTransactionDate().format(dateFormatter);
        return expenseDto.toBuilder()
                .transactionDate(transactionDate)
                .build();
    }
}
