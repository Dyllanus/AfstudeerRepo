package nl.taskmate.boardservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.taskmate.boardservice.domain.exceptions.TaskNotInTaskListException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class TaskList extends BaseEntity {

    private String title;
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

    public TaskList(String title) {
        this(title, "");
    }

    public TaskList(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    public void moveTask(TaskList taskList, UUID taskId) {
        Task taskToMove = this.getTaskById(taskId);
        this.tasks.remove(taskToMove);
        taskList.addTask(taskToMove);
    }

    public Task getTaskById(UUID taskId) {
        return this.tasks.stream().filter(t -> taskId.equals(t.getId())).findFirst().orElseThrow(() ->
                new TaskNotInTaskListException("Task with title '" + taskId + "' is not present in tasklist with name: "
                        + this.getTitle()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskList taskList = (TaskList) o;
        return Objects.equals(title, taskList.title) && Objects.equals(description, taskList.description);
    }

}
