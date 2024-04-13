package org.example.climatica.controller;

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
import io.swagger.v3.oas.annotations.media.ArraySchema;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "User Controller", description = "APIs for managing users in the system")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", responses = {
            @ApiResponse(description = "User created successfully", responseCode = "201", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Forbidden - authorized account", responseCode = "403"),
            @ApiResponse(description = "Conflict - email already exists", responseCode = "409")
    })
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }

    @Operation(summary = "User login", responses = {
            @ApiResponse(description = "User logged in successfully", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(description = "Unauthorized - incorrect email or password", responseCode = "401")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.loginUser(loginDto));
    }

    @Operation(summary = "Get user by ID", responses = {
            @ApiResponse(description = "User found", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "User not found", responseCode = "404")
    })
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int accountId) {
        return ResponseEntity.ok(userService.getUserById(accountId));
    }

    @Operation(summary = "Search accounts", description = "Search for accounts by first name, last name, or email with pagination",
            responses = {
                    @ApiResponse(description = "Search results returned successfully", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
                    @ApiResponse(description = "Bad Request - invalid form or size parameters", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized - invalid authentication credentials", responseCode = "401")
            })
    @GetMapping("/accounts/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam(required = false) String firstName,
                                                     @RequestParam(required = false) String lastName,
                                                     @RequestParam(required = false) String email,
                                                     @RequestParam(defaultValue = "0") int form,
                                                     @RequestParam(defaultValue = "10") int size) {

        if (form < 0 || size <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.searchUsers(firstName, lastName, email, form, size));
    }

    @Operation(summary = "Update user", responses = {
            @ApiResponse(description = "User updated successfully", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden - not owner or user not found", responseCode = "403"),
            @ApiResponse(description = "Conflict - email already exists", responseCode = "409")
    })
    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable int accountId, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(accountId, userDto));
    }

    @Operation(summary = "Delete user", responses = {
            @ApiResponse(description = "User deleted successfully", responseCode = "200"),
            @ApiResponse(description = "Bad Request", responseCode = "400"),
            @ApiResponse(description = "Unauthorized", responseCode = "401"),
            @ApiResponse(description = "Forbidden - not owner or user not found", responseCode = "403")
    })
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int accountId) {
        userService.deleteUser(accountId);
        return ResponseEntity.ok().build();
    }
}
