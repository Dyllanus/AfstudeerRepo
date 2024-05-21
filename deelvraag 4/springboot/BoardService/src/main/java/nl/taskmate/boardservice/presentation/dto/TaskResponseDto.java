package nl.taskmate.boardservice.presentation.dto;

import nl.taskmate.boardservice.domain.Task;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record TaskResponseDto(UUID id, String title, String description, String deadline,
                              Set<TagDto> tags, Set<String> assignedUsers) {
    public static TaskResponseDto fromTask(Task task) {
        String deadline = task.getDeadline() == null ? "" : task.getDeadlineFormatted();
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                deadline,
                task.getTags().stream().map(TagDto::fromTag).collect(Collectors.toSet()),
                task.getAssignedUsers()
        );
    }
}
