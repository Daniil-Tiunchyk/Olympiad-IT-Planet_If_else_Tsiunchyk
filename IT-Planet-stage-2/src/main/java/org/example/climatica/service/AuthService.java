package org.example.climatica.service;

import org.example.climatica.dto.*;
import org.example.climatica.model.User;
import org.example.climatica.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto registerUser(UserRegistrationDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName().trim());
        user.setLastName(userDto.getLastName().trim());
        user.setEmail(userDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(savedUser.getId());
        userResponseDto.setEmail(savedUser.getEmail());
        userResponseDto.setFirstName(savedUser.getFirstName());
        userResponseDto.setLastName(savedUser.getLastName());
        userResponseDto.setId(user.getId());

        return userResponseDto;
    }

    public UserIdDto loginUser(LoginDto loginDto) {
        Optional<User> user = userRepository.findByEmail(loginDto.getEmail());
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
