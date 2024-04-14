package org.example.climatica.auth;

import org.example.climatica.auth.dto.LoginDto;
import org.example.climatica.auth.dto.UserIdDto;
import org.example.climatica.auth.dto.UserRegistrationDto;
import org.example.climatica.accounts.dto.UserResponseDto;
import org.example.climatica.model.User;
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

    public UserResponseDto registerUser(UserRegistrationDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName().trim());
        user.setLastName(userDto.getLastName().trim());
        user.setEmail(userDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = accountRepository.save(user);
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(savedUser.getId());
        userResponseDto.setEmail(savedUser.getEmail());
        userResponseDto.setFirstName(savedUser.getFirstName());
        userResponseDto.setLastName(savedUser.getLastName());
        userResponseDto.setId(user.getId());

        return userResponseDto;
    }

    public UserIdDto loginUser(LoginDto loginDto) {
        Optional<User> user = accountRepository.findByEmail(loginDto.getEmail());
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
