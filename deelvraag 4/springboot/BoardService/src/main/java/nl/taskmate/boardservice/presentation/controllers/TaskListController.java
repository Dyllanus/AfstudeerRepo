package nl.taskmate.boardservice.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import nl.taskmate.boardservice.application.TaskListService;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.presentation.dto.CreateTaskListDto;
import nl.taskmate.boardservice.presentation.dto.MoveTaskRequestDto;
import nl.taskmate.boardservice.presentation.dto.TaskListDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/boards/{boardId}/tasklists")
@RequiredArgsConstructor
@Validated
public class TaskListController {
    private final TaskListService taskListService;

    @Operation(summary = "Create a new tasklist to add tasks to")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create a new tasklist on a board",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskListDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<TaskListDto> createTaskList(@PathVariable UUID boardId,
                                                      @RequestHeader("userdata") String username,
                                                      @RequestBody CreateTaskListDto taskListDto) {
        TaskList taskList = this.taskListService.create(boardId, username, taskListDto.title(), taskListDto.description());
        return new ResponseEntity<>(TaskListDto.fromTaskList(taskList), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing tasklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a description and title of a tasklist.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskListDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/user is not found.",
                    content = @Content)
    })
    @PutMapping("/{taskListId}")
    public ResponseEntity<TaskListDto> updateTaskList(@PathVariable UUID boardId,
                                                      @PathVariable UUID taskListId,
                                                      @RequestHeader("userdata") String username,
                                                      @RequestBody CreateTaskListDto taskListDto) {
        TaskList taskList = this.taskListService.update(boardId, taskListId, username, taskListDto.title(), taskListDto.description());
        return new ResponseEntity<>(TaskListDto.fromTaskList(taskList), HttpStatus.OK);
    }

    @Operation(summary = "Move task from one tasklist to another one.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is successfully moved from one tasklist to another one"),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/user is not found.",
                    content = @Content)
    })
    @PatchMapping("/{taskListId}")
    public ResponseEntity<TaskListDto> moveTask(@PathVariable UUID boardId, @RequestHeader("userdata") String username,
                                                @RequestBody MoveTaskRequestDto moveTaskDto,
                                                @PathVariable UUID taskListId) {
        return ResponseEntity.ok(TaskListDto.fromTaskList(this.taskListService.moveTask(boardId, username, taskListId, moveTaskDto.newTaskListId(), moveTaskDto.taskId())));
    }

    @Operation(summary = "Delete a tasklist from a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasklist is successfully deleted from the board"),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/tasklist/user is not found.",
                    content = @Content)
    })
    @DeleteMapping("/{taskListId}")
    public ResponseEntity<Void> deleteTaskList(@PathVariable UUID boardId,
                                               @PathVariable UUID taskListId,
                                               @RequestHeader("userdata") String username) {
        this.taskListService.delete(boardId, taskListId, username);
        return ResponseEntity.noContent().build();
    }
}
