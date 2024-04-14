package org.example.climatica.accounts;

import org.example.climatica.accounts.dto.UserResponseDto;
import org.example.climatica.auth.dto.UserRegistrationDto;
import org.example.climatica.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private final AccountRepository accountRepository;

    public UserResponseDto getUserById(int id) {
        User user = accountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setFirstName(user.getLastName());
        return userResponseDto;
    }

    public UserResponseDto updateUser(int id, UserRegistrationDto userDto) {
        User currentUser = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        if (!currentUser.getEmail().equals(userDto.getEmail()) && accountRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (!isAuthorizedToUpdate(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to update this user");
        }

        currentUser.setFirstName(userDto.getFirstName());
        currentUser.setLastName(userDto.getLastName());
        currentUser.setEmail(userDto.getEmail());
        User savedUser = accountRepository.save(currentUser);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(savedUser.getId());
        userResponseDto.setEmail(savedUser.getEmail());
        userResponseDto.setFirstName(savedUser.getFirstName());
        userResponseDto.setLastName(savedUser.getLastName());
        return userResponseDto;
    }

    private boolean isAuthorizedToUpdate(int userId) {
        //todo: Логика проверки авторизации пользователя для обновления информации
        return true;
    }

    public List<UserResponseDto> searchUsers(String firstName, String lastName, String email, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> page = accountRepository.findByFirstNameContainingAndLastNameContainingAndEmailContaining(
                firstName, lastName, email, pageable);

        return page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public boolean findByEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }

    public void deleteUser(int id) {
        if (!accountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not found or not authorized to delete");
        }
        if (!isAuthorizedToDelete(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this user");
        }
        accountRepository.deleteById(id);
    }

    private boolean isAuthorizedToDelete(int userId) {
        //todo: Логика проверки авторизации пользователя для удаления аккаунта
        return true;
    }

    private UserResponseDto convertToDto(User user) {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
