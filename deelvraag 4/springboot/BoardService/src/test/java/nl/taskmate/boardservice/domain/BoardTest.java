package nl.taskmate.boardservice.domain;

import nl.taskmate.boardservice.domain.exceptions.TagNotFoundException;
import nl.taskmate.boardservice.domain.exceptions.UserNotAssignedToBoardException;
import nl.taskmate.boardservice.domain.exceptions.UserNotOwnerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private TaskList taskList;
    private Tag tag;
    private String ownerName;
    private String assigneeName;
    private Set<String> standardUsers = new HashSet<>();

    @BeforeEach
    void init() {
        this.ownerName = "Dyllan";
        this.assigneeName = "Laurens";
        this.standardUsers.add("Laurens");
        this.board = new Board("Personal Stuff", "Todo's for my personal issues", ownerName);
        this.taskList = new TaskList("personal");
        this.tag = new Tag("Header", Color.GRAY);
        this.board.addTaskList(taskList);
        this.board.addTag(tag);
        this.board.setUsers(this.standardUsers);
    }

    @Test
    void testConstructor() {
        //Arrange
        String title = "Chores-board";
        String description = "Task board for every chore";
        String owner = "Dyllan";
        //Act
        Board board = new Board(title, description, owner);
        //Assert
        assertEquals(title, board.getTitle());
        assertEquals(description, board.getDescription());
        assertEquals(owner, board.getOwner());
        // when a board is made there should be standard Tasklists and tags
        assertFalse(board.getTags().isEmpty());
        assertFalse(board.getTaskLists().isEmpty());
        assertTrue(board.getUsers().isEmpty());
    }

    @Test
    void testAddTaskList() {
        //Arrange
        TaskList taskList = new TaskList("extra's");
        int taskListSize = this.board.getTaskLists().size();
        //Act
        this.board.addTaskList(taskList);
        //Assert
        assertEquals(taskListSize + 1, this.board.getTaskLists().size());
        assertTrue(this.board.getTaskLists().contains(taskList));
    }

    @Test
    void testDeleteTaskList() {
        //Arrange
        int taskListSize = this.board.getTaskLists().size();
        //Act
        this.board.deleteTaskList(this.taskList);
        //Assert
        assertEquals(taskListSize - 1, this.board.getTaskLists().size());
        assertFalse(this.board.getTaskLists().contains(this.taskList));
    }

    @Test
    void testAddTag() {
        //Arrange
        Tag tag = new Tag("Real", Color.ORANGE);
        //Act
        this.board.addTag(tag);
        //Assert
        assertTrue(this.board.getTags().contains(tag));
    }

    @Test
    void testAddTags() {
        //Arrange
        Tag real = new Tag("Real", Color.ORANGE);
        Tag fakeNews = new Tag("Fake news", Color.RED);
        HashSet<Tag> tags = new HashSet<>(Arrays.asList(real, fakeNews));
        int tagSizeBefore = this.board.getTags().size();
        //Act
        this.board.addTags(tags);
        //Assert
        assertEquals(tagSizeBefore + 2, this.board.getTags().size());
        assertTrue(this.board.getTags().containsAll(tags));
    }

    @Test
    void testDeleteTagByTitle() {
        //Arrange
        String title = tag.getTitle();
        //Act
        this.board.deleteTagByTitle(title);
        //Assert
        assertFalse(board.getTags().contains(tag));
    }

    @Test
    void testDeleteTagByTitleWhenTagDoesNotExists() {
        //Arrange
        String title = "NAMEOFTAGTHATDOESNOTEXISTS";
        int boardTagSize = this.board.getTags().size();
        //Act
        //Assert
        assertDoesNotThrow(() -> this.board.deleteTagByTitle(title));
        assertEquals(boardTagSize, this.board.getTags().size());
    }

    @Test
    void testUpdateTagByTitle() {
        //Arrange
        String oldTitle = this.tag.getTitle();
        String newTagTitle = "Break";
        Color color = new Color(1, 12, 65);
        //Act
        Tag tag = this.board.updateTagByTitle(oldTitle, newTagTitle, color);
        //Assert
        assertEquals(newTagTitle, tag.getTitle());
        assertEquals(color, tag.getColor());
    }

    @Test
    void testUpdateTagByTitleWhenTagDoesNotExists() {
        //Arrange
        String title = "NAMEOFTAGTHATDOESNOTEXISTS";
        String newTagTitle = "Break";
        Color color = new Color(1, 12, 65);
        //Act
        //Assert
        assertThrows(TagNotFoundException.class, () -> this.board.updateTagByTitle(title, newTagTitle, color));
    }

    @Test
    void testAssignUser() {
        //Arrange
        String user = "Stije";
        Set<String> currentUsers = new HashSet<>(this.board.getUsers());
        int userSize = currentUsers.size();
        currentUsers.add(user);

        //Act
        this.board.setUsers(currentUsers);
        //Assert
        assertTrue(board.getUsers().contains(user));
        assertEquals(userSize + 1, board.getUsers().size());
    }

    @Test
    void testRemoveUser() {
        //Arrange
        int userSize = this.board.getUsers().size();
        //Act
        this.board.setUsers(Collections.emptySet());
        //Assert
        assertFalse(this.board.getUsers().contains(assigneeName));
        assertEquals(userSize - 1, this.board.getUsers().size());
    }

    @Test
    void testCheckIfUserIsOwner() {
        //Arrange
        String nameOfOwner = this.ownerName;
        //Act
        //Assert
        assertDoesNotThrow(() -> this.board.checkIfIsOwner(nameOfOwner));
    }

    @Test
    void testCheckIfUserNotOwnerThrowsException() {
        //Arrange
        String nonExistingPerson = "NAMEFORPERSON";
        //Act
        //Assert
        assertThrows(UserNotOwnerException.class, () -> this.board.checkIfIsOwner(nonExistingPerson));
    }

    @Test
    void testIfIsInAssignedUsers() {
        //Arrange
        String nameOfOwner = this.ownerName;
        String nameofAssignee = this.assigneeName;
        //Act
        //Assert
        assertDoesNotThrow(() -> this.board.checkIfInAssignedUsers(nameOfOwner));
        assertDoesNotThrow(() -> this.board.checkIfInAssignedUsers(nameofAssignee));
    }

    @Test
    void testIfIsNotInAssignedUsersThrowsException() {
        //Arrange
        String nonExistingPerson = "NAMEFORPERSON";
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> this.board.checkIfInAssignedUsers(nonExistingPerson));
    }

    @Test
    void testCheckIfInAssignedUsers() {
        //Arrange
        Set<String> users = new HashSet<>(Arrays.asList(ownerName, assigneeName));
        //Act
        //Assert
        assertDoesNotThrow(() -> this.board.checkIfInAssignedUsers(users));
    }

    @Test
    void testCheckIfInAssignedUsersThrowsExceptionWhenPersonNotAssigned() {
        //Arrange
        String nonExistingPerson = "NAMEFORPERSON";
        Set<String> users = new HashSet<>(Arrays.asList(ownerName, assigneeName, nonExistingPerson));
        //Act
        //Assert
        assertThrows(UserNotAssignedToBoardException.class, () -> board.checkIfInAssignedUsers(users));
    }
}