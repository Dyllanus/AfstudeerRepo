package nl.taskmate.boardservice.application;

import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.exceptions.BoardNotFoundException;
import nl.taskmate.boardservice.domain.exceptions.NotAbleToCreateBoardException;
import nl.taskmate.boardservice.domain.exceptions.UserNotAssignedToBoardException;
import nl.taskmate.boardservice.domain.exceptions.UserNotOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    private final String owner = "Dyllan";
    private final String assignedUser = "Stije";
    @Mock
    private BoardRepository boardRepository;
    @InjectMocks
    private BoardService boardService;
    @Mock
    private SendRequestToUserService userService;
    @Captor
    private ArgumentCaptor<Board> boardCaptor;
    private Set<Board> boardsUserOwner = new HashSet<>();
    private Set<Board> boardsUserAssignedTo = new HashSet<>();
    private Board board;

    @BeforeEach
    void init() {
        this.board = new Board("Cool board", "cool description", owner);
        Board tempBoard = new Board("Cool board", "cool description", owner);
        this.board.setUsers(Collections.singleton(assignedUser));
        tempBoard.setUsers(Collections.singleton(assignedUser));
        this.boardsUserOwner.add(this.board);
        this.boardsUserOwner.add(tempBoard);
        this.boardsUserAssignedTo.add(new Board("Cool board", "cool description", assignedUser));
        this.boardsUserAssignedTo.add(new Board("Cool board", "cool description", assignedUser));
        this.boardsUserAssignedTo.add(this.board);
        this.boardsUserAssignedTo.add(tempBoard);
    }

    @Test
    void testGetBoardsThatUserIsInvolvedInOwner() {
        //Arrange
        when(this.boardRepository.findBoardsByOwnerIgnoreCase(owner)).thenReturn(boardsUserOwner);
        //Act
        Set<Board> boards = this.boardService.getBoardsThatUserIsInvolvedIn(owner, true);
        //Assert
        verify(boardRepository, Mockito.times(1)).findBoardsByOwnerIgnoreCase(owner);
        assertThat(boards).isEqualTo(boardsUserOwner);
    }

    @Test
    void testGetBoardsThatUserIsInvolvedInAssignedUsers() {
        //Arrange
        when(this.boardRepository.findBoardsByAssignedUsers(assignedUser)).thenReturn(boardsUserAssignedTo);
        //Act
        Set<Board> boards = this.boardService.getBoardsThatUserIsInvolvedIn(assignedUser, false);
        //Assert
        verify(boardRepository, Mockito.times(1)).findBoardsByAssignedUsers(assignedUser);
        assertThat(boards).isEqualTo(boardsUserAssignedTo);
    }

    @Test
    void testGetBoard() {
        //Arrange
        when(this.boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(this.board));

        //Act
        Board resultBoard = this.boardService.get(UUID.randomUUID(), "Dyllan");

        //Assert
        assertThat(resultBoard).isEqualTo(this.board);
        assertThat(resultBoard.getTitle()).isEqualTo(this.board.getTitle());
        assertThat(resultBoard.getDescription()).isEqualTo(this.board.getDescription());
        assertThat(resultBoard.getOwner()).isEqualTo(this.board.getOwner());
        assertThat(resultBoard.getUsers()).isEqualTo(this.board.getUsers());
        assertThat(resultBoard.getTags()).isEqualTo(this.board.getTags());
    }

    @Test
    void testGetBoardWhenUserNotAssigned() {
        //Arrange
        String username = "Arjen";
        when(this.boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(this.board));
        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.get(UUID.randomUUID(), username));
        //Assert
        verify(boardRepository, Mockito.times(1)).findById(any(UUID.class));
        assertThat(thrown)
                .isInstanceOf(UserNotAssignedToBoardException.class)
                .hasMessage("User '" + username + "' is not assigned to this board");
    }

    @Test
    void testGetBoardThrowsExceptionWhenBoardNotFound() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.get(uuid, "Dyllan"));
        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        assertThat(thrown)
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessage("Board with uuid '" + uuid + "' not found.");
    }

    @Test
    void testBoardCreation() {
        //Arrange
        when(boardRepository.save(boardCaptor.capture())).thenReturn(this.board);
        //Act
        final Board boardResult = this.boardService.create(this.board.getTitle(), this.board.getDescription(), this.board.getOwner());
        //Assert
        verify(boardRepository, Mockito.times(1)).save(any(Board.class));
        assertThat(boardResult).isEqualTo(this.board);
        assertThat(boardCaptor.getValue().getOwner()).isEqualTo(this.board.getOwner());
        assertThat(boardCaptor.getValue().getTitle()).isEqualTo(this.board.getTitle());
        assertThat(boardCaptor.getValue().getDescription()).isEqualTo(this.board.getDescription());
    }

    @Test
    void testBoardCreationEmptyTitle() {
        //Arrange
        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.create("", "cool description", ""));
        //Assert
        assertThat(thrown)
                .isInstanceOf(NotAbleToCreateBoardException.class)
                .hasMessage("Cannot create board either the title: () or the owner: () is empty");
        verify(boardRepository, Mockito.times(0)).save(any(Board.class));
    }

    @Test
    void testUpdateBoard() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        String updateTitle = "AWS is leuk";
        String updateDescription = "Het komt goed";
        Board tempBoard = new Board("AWS Hell", "", "HELP US");
        when(this.boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(tempBoard));
        Board savedBoard = new Board(updateTitle, updateDescription, "HELP US");
        when(this.boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        //Act
        Board boardResult = this.boardService.update(uuid, updateTitle, updateDescription, "HELP US");

        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        verify(boardRepository, Mockito.times(1)).save(tempBoard);
        assertThat(boardResult.getTitle()).isEqualTo(updateTitle);
        assertThat(boardResult.getDescription()).isEqualTo(updateDescription);
    }

    @Test
    void testUpdateBoardUserNotOwnerThrowsException() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        String notAnOwner = "hfjkdsahfjkds";
        String updateTitle = "AWS is leuk";
        String updateDescription = "Het komt goed";
        Board tempBoard = new Board("AWS Hell", "", "HELP US");
        when(this.boardRepository.findById(any(UUID.class))).thenReturn(Optional.of(tempBoard));

        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.update(uuid, updateTitle, updateDescription, notAnOwner));

        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        verify(boardRepository, Mockito.times(0)).save(tempBoard);
        assertThat(thrown).isInstanceOf(UserNotOwnerException.class)
                .hasMessage("User '" + notAnOwner + "' is not the owner of the board.");
    }

    @Test
    void testDeleteBoard() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        //Act
        this.boardService.delete(uuid, this.owner);
        //Assert
        verify(boardRepository, Mockito.times(1)).delete(this.board);
        verify(boardRepository, Mockito.times(1)).findById(uuid);
    }

    @Test
    void testDeleteBoardThrowsExceptionWhenUserNotOwner() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        String notAnOwner = "hfjkdsahfjkds";
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.delete(uuid, notAnOwner));

        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        verify(boardRepository, Mockito.times(0)).delete(this.board);
        assertThat(thrown).isInstanceOf(UserNotOwnerException.class)
                .hasMessage("User '" + notAnOwner + "' is not the owner of the board.");
    }

    @Test
    void testAssignUserToBoard() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        String newAssignee = "Laurens";
        int assigneeSize = this.board.getUsers().size();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        Set<String> newAssignees = new HashSet<>(this.board.getUsers());
        newAssignees.add(newAssignee);

        //Act
        Board resultBoard = this.boardService.updatedAssignedUsers(owner, uuid, newAssignees);

        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        verify(boardRepository, Mockito.times(1)).save(resultBoard);
        assertTrue(resultBoard.getUsers().contains(newAssignee));
        assertEquals(assigneeSize + 1, resultBoard.getUsers().size());
    }

    @Test
    void testCannotAssignUserWhenUsernameNotOwner() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        String notAnOwner = "hfjkdsahfjkds";
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        Set<String> newAssignees = new HashSet<>();
        newAssignees.add("Dyllan");

        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.updatedAssignedUsers(notAnOwner, uuid, newAssignees));

        //Assert
        verify(boardRepository, Mockito.times(1)).findById(uuid);
        verify(boardRepository, Mockito.times(0)).delete(this.board);
        assertThat(thrown).isInstanceOf(UserNotOwnerException.class)
                .hasMessage("User '" + notAnOwner + "' is not the owner of the board.");
    }

//    @Test
//    void testRemoveUserFromBoard() {
//        //Arrange
//        UUID uuid = UUID.randomUUID();
//        String deleteAssignee = "Stije";
//        int assigneeSize = this.board.getUsers().size();
//        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
//        Set<String> assigneesToDelete = new HashSet<>();
//        assigneesToDelete.add("Stije");
//
//        //Act
//        this.boardService.removeUsersFromBoard(owner, uuid, assigneesToDelete);
//
//        //Assert
//        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
//        verify(this.boardRepository, Mockito.times(1)).save(this.board);
//        assertFalse(this.board.getUsers().contains(deleteAssignee));
//        assertEquals(assigneeSize - 1 ,this.board.getUsers().size());
//    }

    @Test
    void testCreateTag() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));

        //Act
        Tag resultTag = this.boardService.createTag("Dyllan", uuid, "Urgent", Color.BLUE);

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        assertTrue(this.board.getTags().contains(resultTag));
    }

    @Test
    void testCreateTagThrowsExceptionWhenUserNotAssignedToBoard() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        String notAnOwner = "hfjkdsahfjkds";

        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.createTag(notAnOwner, uuid, "Urgent", Color.BLUE));

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        assertThat(thrown).isInstanceOf(UserNotAssignedToBoardException.class)
                .hasMessage("User '" + notAnOwner + "' is not assigned to this board");
    }

    @Test
    void testUpdateTag() {
        //Arrange
        Color updatedTagColor = Color.YELLOW;
        String updatedTagTitle = "Medium-urgency";
        UUID uuid = UUID.randomUUID();
        this.board.addTag(new Tag("Urgent", Color.BLUE));
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));

        //Act
        Tag resultTag = this.boardService.updateTag("Dyllan", uuid, "Urgent", updatedTagTitle, updatedTagColor);

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        assertTrue(this.board.getTags().contains(resultTag));
        assertEquals(updatedTagColor, resultTag.getColor());
        assertEquals(updatedTagTitle, resultTag.getTitle());
    }

    @Test
    void testUpdateTagThrowsExceptionWhenUserNotAssignedToBoard() {
        //Arrange
        Color updatedTagColor = Color.YELLOW;
        String updatedTagTitle = "Medium-urgency";
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        this.board.addTag(new Tag("Urgent", Color.BLUE));
        String notAnOwner = "hfjkdsahfjkds";

        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.updateTag(notAnOwner, uuid, "Urgent", updatedTagTitle, updatedTagColor));

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        assertThat(thrown).isInstanceOf(UserNotAssignedToBoardException.class)
                .hasMessage("User '" + notAnOwner + "' is not assigned to this board");
    }

    @Test
    void testDeleteTag() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        Tag tempTag = new Tag("Urgent", Color.BLUE);
        this.board.addTag(tempTag);


        //Act
        this.boardService.deleteTag("Dyllan", uuid, "Urgent");

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        assertFalse(this.board.getTags().contains(tempTag));
    }

    @Test
    void testDeleteTagThrowsExceptionWhenUserNotAssignedToBoard() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(this.boardRepository.findById(uuid)).thenReturn(Optional.of(this.board));
        Tag tempTag = new Tag("Urgent", Color.BLUE);
        this.board.addTag(tempTag);
        String notAnOwner = "hfjkdsahfjkds";

        //Act
        Throwable thrown = catchThrowable(() -> this.boardService.deleteTag(notAnOwner, uuid, "Urgent"));

        //Assert
        verify(this.boardRepository, Mockito.times(1)).findById(uuid);
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        assertThat(thrown).isInstanceOf(UserNotAssignedToBoardException.class)
                .hasMessage("User '" + notAnOwner + "' is not assigned to this board");
    }

}