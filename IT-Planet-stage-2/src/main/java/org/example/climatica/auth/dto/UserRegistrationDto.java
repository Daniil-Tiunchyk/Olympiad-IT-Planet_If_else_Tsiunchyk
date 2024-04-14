package org.example.climatica.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "First name is required and cannot be empty or just whitespace")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters long")
    private String firstName;

    @NotBlank(message = "Last name is required and cannot be empty or just whitespace")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters long")
    private String lastName;

    @NotBlank(message = "Email is required and cannot be empty or just whitespace")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required and cannot be empty or just whitespace")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    private String password;
}
