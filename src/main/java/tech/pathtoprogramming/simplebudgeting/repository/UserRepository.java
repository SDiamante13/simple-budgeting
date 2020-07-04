package tech.pathtoprogramming.simplebudgeting.repository;

import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.User;

public interface UserRepository {
    User createUser(User user);
    User getUser(String username);
    void updateBudgetAllowance(String username, String category, BudgetAllowance updatedBudgetAllowance);
    void addBudgetAllowance(String username, BudgetAllowance budgetAllowance);
}
