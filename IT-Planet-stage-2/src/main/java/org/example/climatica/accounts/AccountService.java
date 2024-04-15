package org.example.climatica.accounts;

import org.example.climatica.auth.dto.UserRegistrationDto;
import org.example.climatica.model.Account;
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

    public AccountResponseDto getUserById(int id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(account.getId());
        accountResponseDto.setEmail(account.getEmail());
        accountResponseDto.setLastName(account.getLastName());
        accountResponseDto.setFirstName(account.getLastName());
        return accountResponseDto;
    }

    public AccountResponseDto updateUser(int id, UserRegistrationDto userDto) {
        Account currentAccount = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        if (!currentAccount.getEmail().equals(userDto.getEmail()) && accountRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        if (!isAuthorizedToUpdate(id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to update this user");
        }

        currentAccount.setFirstName(userDto.getFirstName());
        currentAccount.setLastName(userDto.getLastName());
        currentAccount.setEmail(userDto.getEmail());
        Account savedAccount = accountRepository.save(currentAccount);

        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(savedAccount.getId());
        accountResponseDto.setEmail(savedAccount.getEmail());
        accountResponseDto.setFirstName(savedAccount.getFirstName());
        accountResponseDto.setLastName(savedAccount.getLastName());
        return accountResponseDto;
    }

    private boolean isAuthorizedToUpdate(int userId) {
        //todo: Логика проверки авторизации пользователя для обновления информации
        return true;
    }

    public List<AccountResponseDto> searchUsers(String firstName, String lastName, String email, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<Account> page = accountRepository.findByFirstNameContainingAndLastNameContainingAndEmailContaining(
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

    private AccountResponseDto convertToDto(Account account) {
        AccountResponseDto userDto = new AccountResponseDto();
        userDto.setId(account.getId());
        userDto.setFirstName(account.getFirstName());
        userDto.setLastName(account.getLastName());
        userDto.setEmail(account.getEmail());
        return userDto;
    }
}
