package nl.dyllan.handler.tasklist.dto;

import java.util.UUID;

public record MoveTaskRequestDto(UUID newTaskListId, UUID taskId) {
}
