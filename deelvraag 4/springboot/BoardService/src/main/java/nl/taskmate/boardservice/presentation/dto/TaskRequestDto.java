package nl.taskmate.boardservice.presentation.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

import javax.annotation.Nullable;
import java.util.Set;

public record TaskRequestDto(@NotBlank @Max(50) String title, @Nullable String description, @Nullable String deadline,
                             @Nullable Set<TagDto> tags, @Nullable Set<String> assignedUsers) {
}
