package nl.taskmate.boardservice.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nl.taskmate.boardservice.application.TaskService;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.presentation.dto.TagDto;
import nl.taskmate.boardservice.presentation.dto.TaskRequestDto;
import nl.taskmate.boardservice.presentation.dto.TaskResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/boards/{boardId}/tasklists/{taskListId}/tasks")
@Validated
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task is successfully added to an tasklist ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/user is not found.",
                    content = @Content)
    })
    @PostMapping()
    public ResponseEntity<TaskResponseDto> createTask(@PathVariable UUID boardId, @RequestHeader("userdata") String username, @PathVariable UUID taskListId,
                                                      @RequestBody TaskRequestDto dto) {
        Set<String> assignedUsers = dto.assignedUsers() == null ? new HashSet<>() : dto.assignedUsers();
        String deadline = dto.deadline() == null ? "" : dto.deadline();
        Task task = this.taskService.create(boardId, username, taskListId, dto.title(), dto.description(), deadline,
                TagDto.toTag(dto.tags()), assignedUsers);
        return new ResponseEntity<>(TaskResponseDto.fromTask(task), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a tasks: title/description/assigned user/tag. New tags can also be created")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskRequestDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/task/user is not found.",
                    content = @Content)
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable UUID boardId, @RequestHeader("userdata") String username, @PathVariable UUID taskListId,
                                                      @PathVariable UUID taskId, @RequestBody TaskRequestDto dto) {
        Set<String> assignedUsers = dto.assignedUsers() == null ? new HashSet<>() : dto.assignedUsers();
        String deadline = dto.deadline() == null ? "" : dto.deadline();
        Task task = this.taskService.update(boardId, username, taskListId, taskId, dto.title(), dto.description(), deadline,
                TagDto.toTag(dto.tags()), assignedUsers);
        return new ResponseEntity<>(TaskResponseDto.fromTask(task), HttpStatus.OK);
    }

    @Operation(summary = "Delete a task from a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasklist is successfully deleted from the board"),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/task/user is not found.",
                    content = @Content)
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID boardId, @RequestHeader("userdata") String username,
                                           @PathVariable UUID taskListId, @PathVariable UUID taskId) {
        this.taskService.deleteTask(boardId, username, taskListId, taskId);
        return ResponseEntity.noContent().build();
    }
}
