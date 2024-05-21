package nl.taskmate.boardservice.domain;

import nl.taskmate.boardservice.domain.exceptions.TaskNotInTaskListException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {
    private TaskList taskListWithTask;
    private TaskList taskList;
    private Task task;

    @BeforeEach
    void init() {
        this.taskListWithTask = new TaskList("ToDo", "This task hasn't been started");
        this.taskList = new TaskList("Doing", "These tasks are being worked on");
        this.task = new Task("Clean the Floor");
        this.taskListWithTask.addTask(task);
    }

    @Test
    void testConstructors() {
        //Arrange
        String title = "Done";
        String description = "For the task which are completed";
        //Act
        TaskList taskList = new TaskList("Done", "For the task which are completed");
        TaskList taskList1 = new TaskList(title);
        //Assert
        assertEquals(title, taskList1.getTitle());
        assertEquals(title, taskList.getTitle());
        assertEquals(description, taskList.getDescription());
    }

    @Test
    void testAddTask() {
        //Arrange
        Task task = new Task("Clean dishes");
        //Act
        this.taskList.addTask(task);
        //Assert
        assertTrue(taskList.getTasks().contains(task));
        assertEquals(1, taskList.getTasks().size());
    }

    @Test
    void testTaskCannotBeAddedTwice() {
        //Arrange
        Task task = new Task("Clean dishes");
        //Act
        this.taskList.addTask(task);
        int tasksSize = this.taskList.getTasks().size();
        this.taskList.addTask(task);
        //Assert
        assertEquals(tasksSize, this.taskList.getTasks().size());
    }

    @Test
    void testTaskDeletion() {
        //Arrange
        //Act
        this.taskListWithTask.deleteTask(this.task);
        //Assert
        assertFalse(this.taskListWithTask.getTasks().contains(this.task));
    }

    @Test
    void testMoveTaskWhenTaskExits() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        //Act
        this.task.setId(uuid);
        this.taskListWithTask.moveTask(this.taskList, uuid);
        //Assert
        assertFalse(this.taskListWithTask.getTasks().contains(this.task));
        assertTrue(this.taskList.getTasks().contains(this.task));
    }

    @Test
    void testMoveTaskWhenTaskDoesNotExists() {
        //Arrange
        //Act
        //Assert
        assertThrows(TaskNotInTaskListException.class, () ->
                this.taskListWithTask.moveTask(this.taskList, UUID.randomUUID()));
    }


    @Test
    void testEqualsObjectsEqual() {
        //Arrange
        TaskList taskListEqual = new TaskList("Doing", "These tasks are being worked on");
        //Act
        //Assert
        assertEquals(taskList, taskListEqual);
        assertEquals(taskList, taskList);
    }

    @Test
    void testEqualsObjectsNotEqual() {
        //Arrange
        TaskList taskListEqual = new TaskList("Done", "These tasks are done");
        //Act
        //Assert
        assertNotEquals(taskList, taskListEqual);
    }

    @Test
    void testEqualsDifferentObject() {
        //Assert
        assertNotEquals(taskList, task);
    }

}