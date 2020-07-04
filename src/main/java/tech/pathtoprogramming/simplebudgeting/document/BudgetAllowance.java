package tech.pathtoprogramming.simplebudgeting.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BudgetAllowance {
    @Indexed
    private String category;
    private int maxThreshold;
}
