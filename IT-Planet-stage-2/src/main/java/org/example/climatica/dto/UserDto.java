package org.example.climatica.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100)
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100)
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    private String password;
}
