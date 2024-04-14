package org.example.climatica.controller;

import io.micrometer.common.util.StringUtils;
import org.example.climatica.dto.UserDto;
import org.example.climatica.dto.UserRegistrationDto;
import org.example.climatica.dto.UserResponseDto;
import org.example.climatica.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account Controller", description = "APIs for managing accounts in the system")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Get user by ID", responses = {
            @ApiResponse(description = "User found", responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "User not found", responseCode = "404")
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer accountId) {
        if (accountId == null || accountId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account ID");
        }
        return ResponseEntity.ok(accountService.getUserById(accountId));
    }

    @Operation(summary = "Search accounts", description = "Search for accounts by first name, last name, or email with pagination",
            responses = {
                    @ApiResponse(description = "Search results returned successfully", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
                    @ApiResponse(description = "Bad Request - invalid form or size parameters", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized - invalid authentication credentials", responseCode = "401")
            })
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam(required = false) String firstName,
                                                             @RequestParam(required = false) String lastName,
                                                             @RequestParam(required = false) String email,
                                                             @RequestParam(defaultValue = "0") int form,
                                                             @RequestParam(defaultValue = "10") int size) {

        if (form < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(accountService.searchUsers(firstName, lastName, email, form, size));
    }


    @Operation(summary = "Update user", responses = {
            @ApiResponse(description = "User updated successfully", responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden - not owner or user not found", responseCode = "403"),
            @ApiResponse(description = "Conflict - email already exists", responseCode = "409")
    })
    @PutMapping("/{accountId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer accountId, @Valid @RequestBody UserRegistrationDto userDto) {
        if (accountId == null || accountId <= 0
                || StringUtils.isBlank(userDto.getFirstName())
                || StringUtils.isBlank(userDto.getLastName())
                || StringUtils.isBlank(userDto.getEmail())
                || StringUtils.isBlank(userDto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data");
        }
        return ResponseEntity.ok(accountService.updateUser(accountId, userDto));
    }

    @Operation(summary = "Delete user", responses = {
            @ApiResponse(description = "User deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden - not owner or user not found", responseCode = "403")
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer accountId) {
        if (accountId == null || accountId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account ID");
        }
        accountService.deleteUser(accountId);
        return ResponseEntity.ok().build();
    }
}
