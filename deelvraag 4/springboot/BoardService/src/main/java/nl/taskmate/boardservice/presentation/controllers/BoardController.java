package nl.taskmate.boardservice.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import nl.taskmate.boardservice.application.BoardService;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.exceptions.ColorFormatException;
import nl.taskmate.boardservice.presentation.dto.BoardDto;
import nl.taskmate.boardservice.presentation.dto.CreateBoardDto;
import nl.taskmate.boardservice.presentation.dto.SetUsersDto;
import nl.taskmate.boardservice.presentation.dto.TagDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/boards")
@Validated
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping()
    @Operation(summary = "Get all boards that the user are part of.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All boards of a user. Depending on the boolean ownerOnly",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = BoardDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content)
    })
    public ResponseEntity<Set<BoardDto>> getBoardsThatUserIsInvolvedIn(@RequestHeader("userdata") String username,
                                                                       @RequestParam(name = "ownerOnly", required = false, defaultValue = "false") boolean ownerOnly) {
        return ResponseEntity.ok(
                this.boardService.getBoardsThatUserIsInvolvedIn(username, ownerOnly)
                        .stream().map(BoardDto::fromBoard).collect(Collectors.toSet())
        );
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "Get board by uuid. Includes all lists and tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All boards of a user. Depending on the boolean ownerOnly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board is not found",
                    content = @Content)
    })
    public ResponseEntity<BoardDto> getBoard(@PathVariable UUID boardId, @RequestHeader("userdata") String username) {
        return new ResponseEntity<>(BoardDto.fromBoard(boardService.get(boardId, username)), HttpStatus.OK);
    }

    @Operation(summary = "Create a new board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Board is created successfully.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board is not found",
                    content = @Content)
    })
    @PostMapping()
    public ResponseEntity<BoardDto> createBoard(@RequestHeader("userdata") String username,
                                                @RequestBody CreateBoardDto boardDto) {
        Board board = this.boardService.create(boardDto.title(), boardDto.description(), username);
        UriComponents uriComponents = UriComponentsBuilder.fromPath("/board/{boardId}").buildAndExpand(board.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(BoardDto.fromBoard(board));
    }

    @Operation(summary = "Update the title and description of a board. Only the owner of the board is allowed to change it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the description and title of a board. ",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not part of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable UUID boardId,
                                                @RequestHeader("userdata") String username, @RequestBody CreateBoardDto boardDto) {
        Board board = this.boardService.update(boardId, boardDto.title(), boardDto.description(), username);
        return new ResponseEntity<>(BoardDto.fromBoard(board), HttpStatus.OK);
    }

    @Operation(summary = "Delete board by uuid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Update a description and title of a board. Only the owner of the board is allowed to change it."),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not the owner of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable UUID boardId, @RequestHeader("userdata") String username) {
        this.boardService.delete(boardId, username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "update the assigned users to the specified board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update the assigned users, excluding the owner, of a board. Only the owner of the board is allowed to change it.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BoardDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "At least one of the users given is not the owner of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @PutMapping("/{boardId}/users")
    public ResponseEntity<BoardDto> updateAssignedUsers(@PathVariable UUID boardId,
                                                        @RequestBody SetUsersDto assignedUsers,
                                                        @RequestHeader("userdata") String username) {
        return ResponseEntity.ok(
                BoardDto.fromBoard(this.boardService.updatedAssignedUsers(username, boardId, assignedUsers.users()))
        );
    }

    @Operation(summary = "Create a new tag for a board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New tag created on a board.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TagDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not assigned to this board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @PostMapping("/{boardId}/tags")
    public ResponseEntity<TagDto> createTag(@PathVariable UUID boardId,
                                            @RequestBody TagDto tagDto,
                                            @RequestHeader("userdata") String username) {
        if (!(tagDto.color().startsWith("#") && tagDto.color().length() == 7)) {
            throw new ColorFormatException("Color string:'" + tagDto.color() + "' not able to decode. Make sure it's a hexadecimal code.");
        }
        Tag tag = boardService.createTag(username, boardId, tagDto.name(), Color.decode(tagDto.color()));
        return new ResponseEntity<>(TagDto.fromTag(tag), HttpStatus.CREATED);
    }

    @Operation(summary = "Update tag title and/or tag color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the title and/or color of a tag",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TagDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not assigned to this board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @PutMapping("/{boardId}/tags/{title}")
    public ResponseEntity<TagDto> updateTag(@PathVariable UUID boardId,
                                            @PathVariable String title,
                                            @RequestBody TagDto tagDto,
                                            @RequestHeader("userdata") String username) {
        if (!(tagDto.color().startsWith("#") && tagDto.color().length() == 7)) {
            throw new ColorFormatException("Color string:'" + tagDto.color() + "' not able to decode. Make sure it's a hexadecimal code.");
        }
        Tag tag = this.boardService.updateTag(username, boardId, title, tagDto.name(), Color.decode(tagDto.color()));
        return new ResponseEntity<>(TagDto.fromTag(tag), HttpStatus.OK);
    }

    @Operation(summary = "Delete tag by title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted a tag"),
            @ApiResponse(responseCode = "400", description = "Invalid arguments given",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User given is not the owner of the board",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Board/user is not found.",
                    content = @Content)
    })
    @DeleteMapping("/{boardId}/tags/{title}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID boardId,
                                          @PathVariable String title,
                                          @RequestHeader("userdata") String username) {
        this.boardService.deleteTag(username, boardId, title);
        return ResponseEntity.noContent().build();
    }
}
