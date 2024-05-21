package nl.taskmate.boardservice.presentation.dto;

import java.util.UUID;

public record MoveTaskRequestDto(UUID newTaskListId, UUID taskId) {
}
