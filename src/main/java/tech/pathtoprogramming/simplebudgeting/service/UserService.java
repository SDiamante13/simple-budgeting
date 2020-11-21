package tech.pathtoprogramming.simplebudgeting.service;

import tech.pathtoprogramming.simplebudgeting.dto.BudgetAllowanceDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserCreationDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserDto;

public interface UserService {
    void createUser(UserCreationDto userDto);
    UserDto getUser(String username);
    void updateBudgetAllowance(String username, String category, BudgetAllowanceDto updatedBudgetAllowanceDto);
    void addBudgetAllowance(String username, BudgetAllowanceDto budgetAllowance);
}
