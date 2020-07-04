package tech.pathtoprogramming.simplebudgeting.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.User;
import tech.pathtoprogramming.simplebudgeting.exception.UserRegistrationException;

import static tech.pathtoprogramming.simplebudgeting.Constants.USERS_COLLECTION;
import static tech.pathtoprogramming.simplebudgeting.Constants.findByUsernameQuery;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public User createUser(User user) {
        if (mongoTemplate.exists(findByUsernameQuery(user.getUsername()), User.class)) {
            throw new UserRegistrationException("The username " + user.getUsername() + " is already taken");
        }

        return mongoTemplate.insert(user, USERS_COLLECTION);
    }

    @Override
    public User getUser(String username) {
        return mongoTemplate.findOne(findByUsernameQuery(username), User.class);
    }

    @Override
    public void updateBudgetAllowance(String username, String category, BudgetAllowance updatedBudgetAllowance) {
        Query query = findByUsernameQuery(username)
                .addCriteria(Criteria.where("budgetAllowances.category").is(category));

        Update update = new Update()
                .set("budgetAllowances.$.category", updatedBudgetAllowance.getCategory())
                .set("budgetAllowances.$.maxThreshold", updatedBudgetAllowance.getMaxThreshold());

        mongoTemplate.updateFirst(query, update, User.class);
    }

    @Override
    public void addBudgetAllowance(String username, BudgetAllowance budgetAllowance) {
        Update update = new Update().addToSet("budgetAllowances", budgetAllowance);
        mongoTemplate.updateFirst(findByUsernameQuery(username), update, User.class);
    }
}
