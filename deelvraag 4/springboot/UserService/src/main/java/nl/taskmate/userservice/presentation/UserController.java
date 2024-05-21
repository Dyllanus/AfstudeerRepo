package nl.taskmate.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.taskmate.userservice.application.UserService;
import nl.taskmate.userservice.presentation.dto.LoginDto;
import nl.taskmate.userservice.presentation.dto.RegistrationDto;
import nl.taskmate.userservice.presentation.dto.UpdateDisplayNameDto;
import nl.taskmate.userservice.presentation.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Gets all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all users which exists in the userservice",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers().stream().map(UserDto::fromUser).toList());
    }

    @Operation(summary = "Get user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got the userobject by the username",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content)
    })
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserDto.fromUser(this.userService.findUserByUsername(username)));
    }

    @Operation(summary = "Change your display name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated display name",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content)
    })
    @PatchMapping("/{username}")
    public ResponseEntity<String> editDisplayName(@PathVariable String username, @RequestBody UpdateDisplayNameDto displayNamedto) {
        this.userService.editDisplayName(username, displayNamedto.displayName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Log in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content)
    })
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@Validated @RequestBody LoginDto loginDto) {
        String userId = this.userService.login(loginDto.username(), loginDto.password()).getId().toString();
        return ResponseEntity.ok("User '" + loginDto.username() + "' logged in successfully. Id: " + userId);
    }

    @Operation(summary = "Checks if all given usernames exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All given users exist",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "One or more usernames are not found",
                    content = @Content)
    })
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkIfUsernamesExits(@RequestParam Set<String> usernames) {
        this.userService.checkIfAllUsersExist(usernames);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Register an account, display name is optional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered an account",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content)
    })
    @PostMapping("/auth/register")
    public void register(@Validated @RequestBody RegistrationDto registrationDto) {
        this.userService.register(
                registrationDto.username(),
                registrationDto.displayName(),
                registrationDto.password()
        );
    }
}
