package org.example.climatica.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AllArgsConstructor;
import org.example.climatica.dto.LoginDto;
import org.example.climatica.dto.UserDto;
import org.example.climatica.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Tag(name = "User Controller", description = "APIs for managing users in the system")
public class UserController {

    private UserService userService;

    @Operation(summary = "Create a new user", description = "This method allows you to create a new user.")
    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    @Operation(summary = "User login", description = "This method allows a user to log in.")
    @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.loginUser(loginDto));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID.")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int accountId) {
        return ResponseEntity.ok(userService.getUserById(accountId));
    }

    @Operation(summary = "Search users", description = "Search for users based on optional criteria.")
    @ApiResponse(responseCode = "200", description = "Search results found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    @GetMapping("/accounts/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String firstName,
                                                     @RequestParam(required = false) String lastName,
                                                     @RequestParam(required = false) String email,
                                                     @RequestParam(defaultValue = "0") int form,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchUsers(firstName, lastName, email, form, size));
    }

    @Operation(summary = "Update user", description = "Update an existing user's information.")
    @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserDto.class)))
    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int accountId, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(accountId, userDto));
    }

    @Operation(summary = "Delete user", description = "Delete a user from the system.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int accountId) {
        userService.deleteUser(accountId);
        return ResponseEntity.ok().build();
    }
}
