package org.example.climatica.auth;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.climatica.accounts.AccountResponseDto;
import org.example.climatica.auth.dto.LoginDto;
import org.example.climatica.auth.dto.UserIdDto;
import org.example.climatica.auth.dto.UserRegistrationDto;
import org.example.climatica.accounts.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@Tag(name = "Auth Controller", description = "APIs for registration and authorisation")
public class AuthController {
    private final AuthService authService;
    private final AccountService accountService;

    public AuthController(AuthService authService, AccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new user", responses = {
            @ApiResponse(description = "User created successfully", responseCode = "201", content = @Content(schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Forbidden - authorized account", responseCode = "403"),
            @ApiResponse(description = "Conflict - email already exists", responseCode = "409")
    })
    @PostMapping("/registration")
    public ResponseEntity<AccountResponseDto> registerUser(@RequestBody UserRegistrationDto userDto) {
        String email = userDto.getEmail() != null ? userDto.getEmail().trim() : "";
        String firstName = userDto.getFirstName() != null ? userDto.getFirstName().trim() : "";
        String lastName = userDto.getLastName() != null ? userDto.getLastName().trim() : "";
        String password = userDto.getPassword() != null ? userDto.getPassword().trim() : "";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Authorized accounts cannot create new users");
        }

        if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fields cannot be blank or contain only whitespace");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (accountService.findByEmail(email.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }

        AccountResponseDto createdUser = authService.registerUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "User login", responses = {
            @ApiResponse(description = "User logged in successfully", responseCode = "200", content = @Content(schema = @Schema(implementation = UserIdDto.class))),
            @ApiResponse(description = "Unauthorized - incorrect email or password", responseCode = "401")
    })
    @PostMapping("/login")
    public ResponseEntity<UserIdDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            UserIdDto userIdDto = authService.loginUser(loginDto);
            return ResponseEntity.ok(userIdDto);
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
}
