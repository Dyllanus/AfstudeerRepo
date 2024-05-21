package nl.taskmate.userservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDisplayNameDto(@NotBlank String displayName) {
}
