package nl.taskmate.userservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank String username, String password) {
}
