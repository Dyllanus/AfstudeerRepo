package nl.taskmate.boardservice.application;

import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.domain.exceptions.ConvertDateException;
import nl.taskmate.boardservice.domain.exceptions.TaskNotFoundException;
import nl.taskmate.boardservice.domain.exceptions.UserNotAssignedToBoardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    private final String boardOwner = "Dyllan";
    private final String assignee = "Stije";
    private final UUID taskId = UUID.randomUUID();
    private final UUID boardUuid = UUID.randomUUID();
    private final UUID taskListUuid = UUID.randomUUID();
    @Mock
    private BoardRepository boardRepository;
    @InjectMocks
    private BoardService boardService;
    @InjectMocks
    private TaskService taskService;
    private Set<Tag> tags = new HashSet<>();
    private Set<String> assignees = new HashSet<>();
    private Board board;
    private TaskList taskList;
    private Task task;

    @BeforeEach
    void init() {
        this.taskService = new TaskService(this.boardService, this.boardRepository);
        this.taskList = new TaskList("Question", "For all questions");
        this.task = new Task("Coding", "Program more tests");
        this.task.setId(this.taskId);
        this.taskList.addTask(this.task);
        this.taskList.setId(this.taskListUuid);
        this.assignees.addAll(Arrays.asList(this.assignee, "Arjen", "Laurens"));
        this.board = new Board("Title", "description", this.boardOwner);
        this.board.setUsers(new HashSet<>(this.assignees));
        // Normally these taskLists would have an id.
        for (TaskList tl : this.board.getTaskLists()) {
            tl.setId(UUID.randomUUID());
        }
        this.board.addTaskList(taskList);
        this.board.setId(boardUuid);
        this.tags.addAll(Arrays.asList(new Tag("Cool tag", Color.ORANGE), new Tag("Other cool Tag", Color.DARK_GRAY)));
    }

    @Test
    void testCreateTask() throws ParseException {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        Task resultTask = this.taskService.create(this.boardUuid, this.boardOwner, this.taskListUuid, titleTask, descriptionTask, "25-10-2023",
                this.tags, this.assignees);

        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(this.board.getTags().containsAll(this.tags));
        assertTrue(resultTask.getTags().containsAll(this.tags));
        assertTrue(resultTask.getAssignedUsers().containsAll(this.assignees));
        assertTrue(this.taskList.getTasks().contains(resultTask));
        assertEquals(formatter.parse("2023-10-25"), resultTask.getDeadline());
    }

    @Test
    void testCreateTaskUserNotAssignedToBoard() {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskService.create(this.boardUuid, "NotAnUser", this.taskListUuid, titleTask, descriptionTask, "25-10-2023",
                this.tags, this.assignees));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTags().containsAll(this.tags));
    }

    @Test
    void testCreateTaskAssigneesNotAssignedToBoard() {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        assignees.add("NotAnUser");
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskService.create(this.boardUuid, this.boardOwner, this.taskListUuid, titleTask, descriptionTask, "25-10-2023",
                this.tags, this.assignees));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTags().containsAll(this.tags));
    }

    @Test
    void testCreateTaskIncorrectDateFormat() {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        //Assert
        assertThrows(ConvertDateException.class, () -> this.taskService.create(this.boardUuid, this.boardOwner, this.taskListUuid, titleTask, descriptionTask, "NotADate",
                this.tags, this.assignees));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTags().containsAll(this.tags));
    }

    @Test
    void testUpdateTask() throws ParseException {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        Task resultTask = this.taskService.update(this.boardUuid, this.boardOwner, this.taskListUuid, this.taskId, titleTask, descriptionTask,
                "26-10-2023", this.tags, this.assignees);

        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(this.board.getTags().containsAll(this.tags));
        assertTrue(resultTask.getTags().containsAll(this.tags));
        assertTrue(resultTask.getAssignedUsers().containsAll(assignees));
        assertTrue(this.taskList.getTasks().contains(resultTask));
        assertEquals(titleTask, this.task.getTitle());
        assertEquals(descriptionTask, this.task.getDescription());
        assertEquals(formatter.parse("2023-10-26"), resultTask.getDeadline());
    }

    @Test
    void testUpdateTaskThrowsExceptionWhenUserNotInBoard() {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskService.update(this.boardUuid, "NotAnUser", this.taskListUuid, this.taskId, titleTask, descriptionTask,
                "26-10-2023", this.tags, this.assignees));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTags().containsAll(this.tags));
        assertNotEquals(titleTask, this.task.getTitle());
        assertNotEquals(descriptionTask, this.task.getDescription());
    }

    @Test
    void testUpdateTaskThrowsExceptionWhenAssigneesNotInBoard() {
        //Arrange
        String titleTask = "Task";
        String descriptionTask = "cool description";
        assignees.add("NotAnUser");
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));

        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskService.update(this.boardUuid, this.boardOwner, this.taskListUuid, this.taskId, titleTask, descriptionTask,
                "26-10-2023", this.tags, this.assignees));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.board.getTags().containsAll(this.tags));
        assertNotEquals(titleTask, this.task.getTitle());
        assertNotEquals(descriptionTask, this.task.getDescription());
    }

    @Test
    void testDeleteTask() {
        //Arrange
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        this.taskService.deleteTask(this.boardUuid, this.boardOwner, this.taskListUuid, this.taskId);
        //Assert
        verify(this.boardRepository, Mockito.times(1)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertFalse(this.taskList.getTasks().contains(this.task));

    }

    @Test
    void testDeleteTaskUserNotAssignedToBoard() {
        //Arrange
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.taskService.deleteTask(this.boardUuid, "NotAnUser", this.taskListUuid, this.taskId));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(this.taskList.getTasks().contains(this.task));
    }

    @Test
    void testDeleteTaskTaskIdDoesNotExist() {
        //Arrange
        when(this.boardRepository.findById(this.boardUuid)).thenReturn(Optional.of(this.board));
        //Act

        //Assert
        assertThrows(TaskNotFoundException.class, () -> this.taskService.deleteTask(this.boardUuid, this.boardOwner, this.taskListUuid, this.taskListUuid));
        verify(this.boardRepository, Mockito.times(0)).save(this.board);
        verify(this.boardRepository, Mockito.times(1)).findById(this.board.getId());
        assertTrue(this.taskList.getTasks().contains(this.task));
    }
}