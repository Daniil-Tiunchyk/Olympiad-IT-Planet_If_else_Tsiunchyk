package org.example.climatica.service;

import org.example.climatica.dto.LoginDto;
import org.example.climatica.dto.UserDto;
import org.example.climatica.model.User;
import org.example.climatica.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserDto registerUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return userDto;
    }

    public UserDto loginUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return convertToDto(user);
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    public UserDto getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertToDto(user);
    }

    public UserDto updateUser(int id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        userRepository.save(user);
        return userDto;
    }

    public List<UserDto> searchUsers(String firstName, String lastName, String email, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> page = userRepository.findByFirstNameContainingAndLastNameContainingAndEmailContaining(
                firstName, lastName, email, pageable);

        return page.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
