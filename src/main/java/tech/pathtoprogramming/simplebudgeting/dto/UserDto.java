package tech.pathtoprogramming.simplebudgeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private String id;
    private String username;
    private List<BudgetAllowanceDto> budgetAllowances;
}
