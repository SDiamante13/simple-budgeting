package tech.pathtoprogramming.simplebudgeting.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Document(collection = "expenses")
public class Expense {
    @Id
    private String expenseId;

    @Indexed
    private String username;

    @Indexed
    @Builder.Default
    private LocalDate transactionDate = LocalDate.now();

    private double amount;

    @Indexed
    private String category;

    private String description;
}
