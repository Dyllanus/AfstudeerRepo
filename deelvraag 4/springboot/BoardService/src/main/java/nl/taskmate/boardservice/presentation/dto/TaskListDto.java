package nl.taskmate.boardservice.presentation.dto;


import nl.taskmate.boardservice.domain.TaskList;

import java.util.Set;
import java.util.stream.Collectors;

public record TaskListDto(String id, String title, String description, Set<TaskResponseDto> tasks) {
    public static TaskListDto fromTaskList(TaskList taskList) {
        return new TaskListDto(
                taskList.getId().toString(),
                taskList.getTitle(),
                taskList.getDescription(),
                taskList.getTasks().stream().map(TaskResponseDto::fromTask).collect(Collectors.toSet())
        );
    }

}
