package nl.taskmate.boardservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.taskmate.boardservice.application.DateUtils;
import nl.taskmate.boardservice.data.converter.SetStringsToStringConverter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class Task extends BaseEntity {
    private String title;
    private String description;
    private Date deadline;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Tag> tags = new HashSet<>();

    @Convert(converter = SetStringsToStringConverter.class)
    private Set<String> assignedUsers = new HashSet<>();

    public Task(String title) {
        this(title, "");
    }

    public Task(String title, String description) {
        this(title, description, null);
    }

    public Task(String title, String description, Date deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
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

    public String getDeadlineFormatted() {
        return DateUtils.convertDateToString(this.deadline);
    }
}
