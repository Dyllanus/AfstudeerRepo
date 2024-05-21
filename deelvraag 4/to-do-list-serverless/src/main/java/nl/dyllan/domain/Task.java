package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import nl.dyllan.DateUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
public class Task extends BaseEntity {
    private String title;
    private String description;

    private Set<Tag> tags = new HashSet<>();

    private Set<String> assignedUsers = new HashSet<>();

    public Task(String title) {
        this(title, "");
    }

    public Task(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void removeTags(Tag tag) {
        this.tags.remove(tag);
    }

    public void assignUser(String username) {
        this.assignedUsers.add(username);
    }

    public void assignUsers(Set<String> usernames) {
        this.assignedUsers.addAll(usernames);
    }

    public void removeUser(String username) {
        this.assignedUsers.remove(username);
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public Set<Tag> getTags () {
        return tags;
    }

    public void setTags (Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<String> getAssignedUsers () {
        return assignedUsers;
    }

    public void setAssignedUsers (Set<String> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
}
