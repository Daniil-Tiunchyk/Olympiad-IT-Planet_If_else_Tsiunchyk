package org.example.climatica.auth;

import org.example.climatica.auth.dto.LoginDto;
import org.example.climatica.auth.dto.UserIdDto;
import org.example.climatica.auth.dto.UserRegistrationDto;
import org.example.climatica.accounts.AccountResponseDto;
import org.example.climatica.model.Account;
import org.example.climatica.accounts.AccountRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponseDto registerUser(UserRegistrationDto userDto) {
        Account account = new Account();
        account.setFirstName(userDto.getFirstName().trim());
        account.setLastName(userDto.getLastName().trim());
        account.setEmail(userDto.getEmail().trim().toLowerCase());
        account.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Account savedAccount = accountRepository.save(account);
        AccountResponseDto accountResponseDto = new AccountResponseDto();

        accountResponseDto.setId(savedAccount.getId());
        accountResponseDto.setEmail(savedAccount.getEmail());
        accountResponseDto.setFirstName(savedAccount.getFirstName());
        accountResponseDto.setLastName(savedAccount.getLastName());
        accountResponseDto.setId(account.getId());

        return accountResponseDto;
    }

    public UserIdDto loginUser(LoginDto loginDto) {
        Optional<Account> user = accountRepository.findByEmail(loginDto.getEmail());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (passwordEncoder.matches(loginDto.getPassword(), user.get().getPassword())) {
            return new UserIdDto(user.get().getId());
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
