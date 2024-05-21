package nl.taskmate.userservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.annotation.Nullable;

public record RegistrationDto(@NotBlank @Size(min = 3, max = 20) String username,
                              @Nullable String displayName,
                              @Size(min = 5) String password) {
}
