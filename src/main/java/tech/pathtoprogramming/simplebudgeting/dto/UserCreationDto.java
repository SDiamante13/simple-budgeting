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
public class UserCreationDto {
    private String username;
    private String password;
    private String email;
    private List<BudgetAllowanceDto> budgetAllowances;
}
