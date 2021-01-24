package tech.pathtoprogramming.simplebudgeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetOverviewDto {
    private String username;
    private String month;
    private List<BudgetStat> budgetStats;
}
