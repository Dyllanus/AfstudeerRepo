package nl.taskmate.boardservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private final String name = "Dyllan";
    private Task taskWithoutTags;
    private Task taskWithTags;
    private Task taskWithUser;
    private Tag tag;

    @BeforeEach
    void init() {
        tag = new Tag("postponed", Color.PINK);
        taskWithoutTags = new Task("Do the Groceries");
        taskWithTags = new Task("Walk The Dog");
        taskWithUser = new Task("Clean the Floor");
        taskWithUser.assignUser(name);
        taskWithTags.addTag(tag);
    }

    @Test
    void testConstructor() {
        //Arrange
        String title = "Fill up car";
        String description = "Go to the gasstation and fill up the car";
        Date deadline = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));
        //Act
        Task task = new Task(title, description, deadline);
        //Assert
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(deadline, task.getDeadline());

    }

    @Test
    void testIfMultipleTagsCanBeAdded() {
        //Arrange
        Tag newTag = new Tag("Urgent", Color.RED);
        //Act
        taskWithoutTags.addTag(newTag);
        //Assert
        assertTrue(taskWithoutTags.getTags().contains(newTag));
    }

    @Test
    void testIfTagCannotBeAddedTwice() {
        //Arrange
        //Act
        taskWithoutTags.addTag(tag);
        int tagSize = taskWithoutTags.getTags().size();
        taskWithoutTags.addTag(tag);
        //Assert
        assertEquals(1, tagSize);
    }

    @Test
    void testAddTags() {
        //Arrange
        Tag tag = new Tag("Urgent", Color.ORANGE);
        Tag tag1 = new Tag("Urgent", Color.ORANGE);
        Set<Tag> tags = new HashSet<>(Arrays.asList(tag, tag1));
        //Act
        this.taskWithTags.addTags(tags);
        tags.add(this.tag);
        //Assert
        assertEquals(tags, this.taskWithTags.getTags());
    }

    @Test
    void testIfTagCanBeRemoved() {
        //Arrange
        //Act
        taskWithTags.removeTags(tag);
        //Assert
        assertEquals(0, taskWithTags.getTags().size());

    }

    @Test
    void testIfUserCannotBeAddedTwice() {
        //Arrange
        //Act
        taskWithoutTags.assignUser(name);
        int userSize = taskWithoutTags.getAssignedUsers().size();
        taskWithoutTags.assignUser(name);
        //Assert
        assertEquals(1, userSize);
    }

    @Test
    void testIfMultipleUsersCanBeAssigned() {
        //Arrange
        String name = "Stije";
        //Act
        taskWithUser.assignUser(name);
        //Assert
        assertTrue(taskWithUser.getAssignedUsers().contains(name));
        assertEquals(2, taskWithUser.getAssignedUsers().size());
    }

    @Test
    void testIfUserCanBeRemoved() {
        //Arrange
        //Act
        taskWithUser.removeUser(name);
        //Assert
        assertFalse(taskWithUser.getAssignedUsers().contains(name));
        assertEquals(0, taskWithUser.getAssignedUsers().size());
    }

    @Test
    void testAddUsers() {
        //Arrange
        taskWithUser.assignUser("Stije");
        Set<String> expectedUsers = new HashSet<>(Arrays.asList(this.name, "Stije"));
        Set<String> addedUsers = new HashSet<>(Arrays.asList("Arjen", "Laurens"));
        //Act
        this.taskWithUser.assignUsers(addedUsers);
        expectedUsers.addAll(addedUsers);
        //Assert
        assertEquals(expectedUsers, this.taskWithUser.getAssignedUsers());
    }

}