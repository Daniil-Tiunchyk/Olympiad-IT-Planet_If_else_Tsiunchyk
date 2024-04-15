package org.example.climatica.accounts;

import lombok.Data;

@Data
public class AccountResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
