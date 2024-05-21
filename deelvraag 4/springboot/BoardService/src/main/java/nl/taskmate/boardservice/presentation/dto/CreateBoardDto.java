package nl.taskmate.boardservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBoardDto(@NotBlank String title, String description) {
}
