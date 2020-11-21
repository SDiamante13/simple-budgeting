package tech.pathtoprogramming.simplebudgeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExpenseDto {
    String expenseId;
    String category;
    double amount;
    String description;
    String transactionDate;
}
