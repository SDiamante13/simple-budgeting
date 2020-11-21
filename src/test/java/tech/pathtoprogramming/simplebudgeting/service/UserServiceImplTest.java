package tech.pathtoprogramming.simplebudgeting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.User;
import tech.pathtoprogramming.simplebudgeting.dto.BudgetAllowanceDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserCreationDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserDto;
import tech.pathtoprogramming.simplebudgeting.repository.UserRepository;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    private ModelMapper modelMapper = new ModelMapper();

    private UserService userService;

    private static final String USERNAME = "bob123";

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(mockUserRepository, modelMapper);
    }

    @Test
    void createUser_convertsUserDtoToUser_thenSavesToDatabase() {
        UserCreationDto userDto = UserCreationDto.builder()
                .email("myemail@gmail.com")
                .username(USERNAME)
                .password("1234")
                .budgetAllowances(singletonList(BudgetAllowanceDto.builder()
                        .category("Video Games")
                        .maxThreshold(50)
                        .build()))
                .build();

        User expectedUser = getOneUser();

        when(mockUserRepository.createUser(any()))
                .thenReturn(expectedUser.toBuilder().id("123456789").build());

        userService.createUser(userDto);

        verify(mockUserRepository).createUser(expectedUser);
    }

    @Test
    void getUser_getsUserFromRepository() {
        when(mockUserRepository.getUser(USERNAME)).thenReturn(getOneUser());

        UserDto userDto = userService.getUser(USERNAME);

        assertThat(userDto.getUsername()).isEqualTo(USERNAME);
        assertThat(userDto.getBudgetAllowances().size()).isEqualTo(1);
    }

    @Test
    void updateBudgetAllowance_convertsUpdatedBudgetAllowanceDtoToDocument_andUpdatedGivenCategory() {
        userService.updateBudgetAllowance(USERNAME, "Shopping",
                BudgetAllowanceDto.builder().category("Amazon").maxThreshold(300).build());

        verify(mockUserRepository).updateBudgetAllowance(USERNAME, "Shopping", BudgetAllowance.builder()
                .category("Amazon")
                .maxThreshold(300)
                .build());
    }

    @Test
    void addBudgetAllowance_convertsBudgetAllowanceDtoToDocument_andCallsRepositoryToAddToBudgetAllowances() {
        BudgetAllowanceDto budgetAllowanceDto = BudgetAllowanceDto.builder()
                .category("Electronics")
                .maxThreshold(350)
                .build();

        userService.addBudgetAllowance(USERNAME, budgetAllowanceDto);

        verify(mockUserRepository).addBudgetAllowance(USERNAME, BudgetAllowance.builder()
                .category("Electronics")
                .maxThreshold(350)
                .build());
    }

    private User getOneUser() {
        return User.builder()
                .username(USERNAME)
                .budgetAllowances(singletonList(BudgetAllowance.builder()
                        .category("Video Games")
                        .maxThreshold(50)
                        .build()))
                .build();
    }
}
