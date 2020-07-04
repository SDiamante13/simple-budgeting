package tech.pathtoprogramming.simplebudgeting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.User;
import tech.pathtoprogramming.simplebudgeting.exception.UserRegistrationException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.pathtoprogramming.simplebudgeting.Constants.USERS_COLLECTION;
import static tech.pathtoprogramming.simplebudgeting.Constants.findByUsernameQuery;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class UserRepositoryImplTest {

    @Autowired
    MongoTemplate mongoTemplate;

    private UserRepository userRepository;

    private static final String USERNAME = "bob123";

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(mongoTemplate);

        mongoTemplate.dropCollection(USERS_COLLECTION);

        mongoTemplate.insert(User.builder().username(USERNAME).budgetAllowances(getBudgetAllowances()).build(), USERS_COLLECTION);
    }

    @Test
    void createUser_canSaveNewUsersAndTheirInitialBudgetAllowances() {
        List<BudgetAllowance> budgetAllowances = Arrays.asList(
                BudgetAllowance.builder()
                        .category("Groceries")
                        .maxThreshold(500)
                        .build(),
                BudgetAllowance.builder()
                        .category("Shopping")
                        .maxThreshold(250)
                        .build()
        );

        userRepository.createUser(User.builder().username("sam55").budgetAllowances(budgetAllowances).build());

        User savedUser = mongoTemplate.findOne(findByUsernameQuery("sam55"), User.class);

        assertThat(savedUser).isNotNull();
    }

    @Test
    void createUser_throwsUserRegistrationException_whenUsernameAlreadyExistsInTheDatabase() {
        assertThrows(UserRegistrationException.class, () ->
                userRepository.createUser(User.builder().username(USERNAME).build())
        );
    }

    @Test
    void getUser_returnsTheUserFromTheDatabase_thatMatchesTheGivenUsername() {
        User actualUser = userRepository.getUser(USERNAME);

        assertThat(actualUser.getUsername()).isEqualTo(USERNAME);
        assertThat(actualUser.getBudgetAllowances().size()).isEqualTo(6);
    }

    @Test
    void updateBudgetAllowance_updatesTheBudgetAllowanceWhichMatchesTheGivenCategory() {
        BudgetAllowance newBudgetAllowance = BudgetAllowance.builder()
                .category("Movies")
                .maxThreshold(120)
                .build();
        userRepository.updateBudgetAllowance(USERNAME, "AAA", newBudgetAllowance);

        User actualUser = mongoTemplate.findOne(findByUsernameQuery(USERNAME), User.class);

        assertThat(actualUser.getBudgetAllowances().get(2).getCategory()).isEqualTo("Movies");
        assertThat(actualUser.getBudgetAllowances().get(2).getMaxThreshold()).isEqualTo(120);
    }

    @Test
    void addBudgetAllowance_addsBudgetAllowanceToGivenUser() {
        BudgetAllowance budgetAllowance = BudgetAllowance.builder()
                .category("Gas")
                .maxThreshold(100)
                .build();
        userRepository.addBudgetAllowance(USERNAME, budgetAllowance);

        User actualUser = mongoTemplate.findOne(findByUsernameQuery(USERNAME), User.class);

        assertThat(actualUser.getBudgetAllowances().size()).isEqualTo(7);
        assertThat(actualUser.getBudgetAllowances().get(6).getCategory()).isEqualTo("Gas");
    }

    private static List<BudgetAllowance> getBudgetAllowances() {
        return Arrays.asList(
                BudgetAllowance.builder()
                        .category("Groceries")
                        .maxThreshold(300).build(),
                BudgetAllowance.builder()
                        .category("Shopping")
                        .maxThreshold(500).build(),
                BudgetAllowance.builder()
                        .category("Entertainment")
                        .maxThreshold(200).build(),
                BudgetAllowance.builder()
                        .category("Outside Food")
                        .maxThreshold(125).build(),
                BudgetAllowance.builder()
                        .category("Mortgage")
                        .maxThreshold(2200).build(),
                BudgetAllowance.builder()
                        .category("Utilities")
                        .maxThreshold(200).build()
        );
    }
}