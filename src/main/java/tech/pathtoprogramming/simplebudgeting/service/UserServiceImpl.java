package tech.pathtoprogramming.simplebudgeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tech.pathtoprogramming.simplebudgeting.document.BudgetAllowance;
import tech.pathtoprogramming.simplebudgeting.document.User;
import tech.pathtoprogramming.simplebudgeting.dto.BudgetAllowanceDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserCreationDto;
import tech.pathtoprogramming.simplebudgeting.dto.UserDto;
import tech.pathtoprogramming.simplebudgeting.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createUser(UserCreationDto userCreationDto) {
        User user = userRepository.createUser(convertToUser(userCreationDto));
        log.info("New user has registered - {} with budget allowances {}", user.getUsername(), user.getBudgetAllowances());
    }

    @Override
    public UserDto getUser(String username) {
        User user = userRepository.getUser(username);
        return convertToUserDto(user);
    }

    @Override
    public void updateBudgetAllowance(String username, String category, BudgetAllowanceDto updatedBudgetAllowanceDto) {
        userRepository.updateBudgetAllowance(username, category, convertToBudgetAllowance(updatedBudgetAllowanceDto));
    }

    @Override
    public void addBudgetAllowance(String username, BudgetAllowanceDto budgetAllowanceDto) {
        userRepository.addBudgetAllowance(username, convertToBudgetAllowance(budgetAllowanceDto));

    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToUser(UserCreationDto userCreationDto) {
        return modelMapper.map(userCreationDto, User.class);
    }

    private BudgetAllowance convertToBudgetAllowance(BudgetAllowanceDto budgetAllowanceDto) {
        return modelMapper.map(budgetAllowanceDto, BudgetAllowance.class);
    }
}
