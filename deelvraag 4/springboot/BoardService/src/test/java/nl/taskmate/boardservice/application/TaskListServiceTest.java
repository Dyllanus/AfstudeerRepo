package nl.taskmate.boardservice.application;

import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.domain.exceptions.UserNotAssignedToBoardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskListServiceTest {
    private final String boardOwner = "Dyllan";
    private final UUID taskId = UUID.randomUUID();
    private final UUID boardUuid = UUID.randomUUID();
    private final UUID taskListUuid = UUID.randomUUID();
    @Mock
    private BoardRepository boardRepository;
    @InjectMocks
    private BoardService boardService;
    @InjectMocks
    private TaskListService taskListService;
    private Board board;
    private TaskList taskList;
    private Task task;

    @BeforeEach
    void init() {
        this.taskListService = new TaskListService(boardRepository, boardService);
        this.taskList = new TaskList("Question", "For all questions");
        this.task = new Task("Coding", "Program more tests");
        this.task.setId(taskId);
        this.taskList.addTask(this.task);
        taskList.setId(taskListUuid);
        this.board = new Board("Title", "description", boardOwner);
        // Normally these taskLists would have an id.
        for (TaskList tl : this.board.getTaskLists()) {
            tl.setId(UUID.randomUUID());
        }
        this.board.addTaskList(taskList);
        this.board.setId(boardUuid);
    }

    @Test
    void testCreateTaskList() {
        //Arrange
        String taskListTitle = "Menu";
        String taskListDescription = "cool menu items";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        TaskList resultTaskList = this.taskListService.create(this.boardUuid, this.boardOwner, taskListTitle, taskListDescription);
        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertEquals(taskListTitle, resultTaskList.getTitle());
        assertEquals(taskListDescription, resultTaskList.getDescription());
        assertTrue(this.board.getTaskLists().contains(resultTaskList));
    }

    @Test
    void testCreateTaskListUserNotAssignedToBoard() {
        //Arrange
        String taskListTitle = "Menu";
        String taskListDescription = "cool menu items";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskListService.create(this.boardUuid, "NotAnUser", taskListTitle, taskListDescription));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
    }

    @Test
    void testUpdateTask() {
        //Arrange
        String taskListTitle = "Menu";
        String taskListDescription = "cool menu items";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        TaskList resultTaskList = this.taskListService.update(this.boardUuid, this.taskListUuid, this.boardOwner, taskListTitle, taskListDescription);

        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertEquals(taskListTitle, resultTaskList.getTitle());
        assertEquals(taskListDescription, resultTaskList.getDescription());
        assertTrue(this.board.getTaskLists().contains(resultTaskList));
    }

    @Test
    void testUpdateTaskUserNotAssigned() {
        //Arrange
        String taskListTitle = "Menu";
        String taskListDescription = "cool menu items";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskListService.update(this.boardUuid, taskListUuid, "NotAnUser", taskListTitle, taskListDescription));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
    }

    @Test
    void testMoveTask() {
        //Arrange
        TaskList newTaskList = new TaskList("newTaskList", "Cool description");
        UUID newTaskListUuid = UUID.randomUUID();
        newTaskList.setId(newTaskListUuid);
        this.board.addTaskList(newTaskList);
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        when(this.boardRepository.save(this.board)).thenReturn(this.board);

        //Act
        this.taskListService.moveTask(this.boardUuid, this.boardOwner, taskListUuid, newTaskListUuid, taskId);

        //Assert
        verify(this.boardRepository, Mockito.times(2)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(newTaskList.getTasks().contains(this.task));
        assertFalse(this.taskList.getTasks().contains(this.task));
    }

    @Test
    void testMoveTaskUserNotAssigned() {
        //Arrange
        TaskList newTaskList = new TaskList("newTaskList", "Cool description");
        UUID newTaskListUuid = UUID.randomUUID();
        newTaskList.setId(newTaskListUuid);
        this.board.addTaskList(newTaskList);
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskListService.moveTask(this.boardUuid, "NotAnUser", taskListUuid, newTaskListUuid, taskId));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(newTaskList.getTasks().contains(this.task));
        assertTrue(this.taskList.getTasks().contains(this.task));
    }

    @Test
    void testDeleteTaskList() {
        //Arrange
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        this.taskListService.delete(this.boardUuid, this.taskListUuid, boardOwner);
        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTaskLists().contains(this.taskList));
    }

    @Test
    void testDeleteTaskListUserNotAssigned() {
        //Arrange
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskListService.delete(this.boardUuid, this.taskListUuid, "NotAnUser"));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(this.board.getTaskLists().contains(this.taskList));
    }

}