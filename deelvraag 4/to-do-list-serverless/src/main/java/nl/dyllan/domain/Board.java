package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import nl.dyllan.domain.exceptions.*;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;


@Getter
@RegisterForReflection
public class Board extends BaseEntity {

    private String title;
    private String description;
    private String owner;
    // This is only used by saving the tasklistsids to the database
    private Set<UUID> tasklistsIds = new HashSet<>();
    private Set<TaskList> taskLists = new HashSet<>();
    private Set<Tag> tags = new HashSet<>();
    private Set<String> users = new HashSet<>();


    @Builder
    public Board (UUID id, Long version, Instant created, Instant lastModified,
                  String title, String description, String owner, Set<TaskList> taskLists, Set<Tag> tags, Set<String> users, Set<UUID> tasklistsIds) {
        super(id, version, created, lastModified);
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.taskLists = taskLists;
        this.tags = tags;
        this.users = users;
        this.tasklistsIds = tasklistsIds;
    }

    public Board(String title, String description, String owner) {
        super();
        if (title.isBlank() || owner.isBlank())
            throw new NotAbleToCreateBoardException("Cannot create board either the title: () or the owner: () is empty");

        this.title = title;
        this.description = description;
        this.owner = owner;
        taskLists.addAll(new HashSet<>(Arrays.asList(
                new TaskList("ToDo", "This task hasn't been started"),
                new TaskList("Doing", "This is actively being worked on"),
                new TaskList("Done", "This has been completed"))));
        tags.addAll(new HashSet<>(Arrays.asList(
                new Tag("Work", Color.BLUE),
                new Tag("Personal", Color.PINK),
                new Tag("Study", Color.PINK),
                new Tag("HighPriority", Color.RED),
                new Tag("MediumPriority", Color.ORANGE),
                new Tag("LowPriority", new Color(205, 253, 2))
        )));
    }

    public void moveTask(UUID newTasklistId, UUID oldTasklistId, UUID task, String username){
        checkIfInAssignedUsers(username);
        var newTaskList = getTaskListById(newTasklistId);
        var oldTaskList = getTaskListById(oldTasklistId);
        oldTaskList.moveTask(newTaskList, task);
    }


    public void addTaskList(TaskList taskList) {
        this.taskLists.add(taskList);
    }

    public void deleteTaskList(TaskList taskList) {
        this.taskLists.remove(taskList);
    }

    public void deleteTaskList(UUID taskListId) {
        this.taskLists.removeIf(taskList -> taskList.getId().equals(taskListId));
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void deleteTagByTitle(String title) {
        this.tags.stream().filter(tag -> tag.getTitle().equals(title)).findFirst().ifPresent(tag -> tags.remove(tag));
    }

    public Tag updateTagByTitle(String oldTitle, String newTitle, Color color) {
        Tag tag = this.tags.stream().filter(t -> t.getTitle().equals(oldTitle)).findFirst().orElseThrow(() ->
                new TagNotFoundException("Tag with name'" + oldTitle + "' does not exists in this board"));
        tag.setTitle(newTitle);
        tag.setColor(color);
        return tag;
    }

    public TaskList getTaskListById(UUID taskListUuid) {
        return this.taskLists.stream()
                .filter(tl -> tl.getId().equals(taskListUuid)).findFirst()
                .orElseThrow(() -> new TaskListNotFoundException("TaskList with uuid '" + taskListUuid + "' was not found"));
    }

    public void setUsers(Set<String> users) {
        if (users.contains(owner))
            throw new OwnerOfBoardCanNotBeAnAssignedUserException("Owner of board cannot to the assigned users list");

        this.users = users;
    }

    public void addAssignee(String username){
        this.users.add(username);
    }

    public void deleteAssignee(String username){
        this.users.remove(username);
    }

    public void checkIfIsOwner(String username) {
        if (!this.getOwner().equals(username))
            throw new UserNotOwnerException("User '" + username + "' is not the owner of the board.");
    }

    public void checkIfInAssignedUsers(String username) {
        if (!(this.owner.equals(username) || this.users.contains(username)))
            throw new UserNotAssignedToBoardException("User '" + username + "' is not assigned to this board");
    }

    public void checkIfInAssignedUsers(Set<String> usernames) {
        List<String> tempListUsernames = new ArrayList<>(usernames);
        tempListUsernames.remove(this.owner);
        if (this.users.containsAll(tempListUsernames)) return;
        tempListUsernames.removeAll(this.users);
        throw new UserNotAssignedToBoardException("One or more users are not assigned to this board. Users not assigned are: " +
                String.join(",", tempListUsernames));
    }

  public void setTitle (String title) {
        this.title = title;
    }

  public void setDescription (String description) {
        this.description = description;
    }

  public void setOwner (String owner) {
        this.owner = owner;
    }

  public void setTaskLists (Set<TaskList> taskLists) {
        this.taskLists = taskLists;
    }

  public void setTags (Set<Tag> tags) {
        this.tags = tags;
    }

}
