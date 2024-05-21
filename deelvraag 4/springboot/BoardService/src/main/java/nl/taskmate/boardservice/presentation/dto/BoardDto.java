package nl.taskmate.boardservice.presentation.dto;

import nl.taskmate.boardservice.domain.Board;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record BoardDto(UUID id, String title, String description, String owner, Set<TaskListDto> taskLists,
                       Set<TagDto> tags, Set<String> assignedUsers) {

    public static BoardDto fromBoard(Board board) {
        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getDescription(),
                board.getOwner(),
                board.getTaskLists().stream().map(TaskListDto::fromTaskList).collect(Collectors.toSet()),
                board.getTags().stream().map(TagDto::fromTag).collect(Collectors.toSet()),
                board.getUsers()
        );
    }
}
