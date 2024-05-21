package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.dyllan.domain.exceptions.TaskNotInTaskListException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Setter
@Getter
@RegisterForReflection
public class TaskList extends BaseEntity {

    private UUID boardId;
    private String title;
    private String description;

    private Set<Task> tasks = new HashSet<>();

    public TaskList(String title) {
        this(title, "");
    }

    public TaskList(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    @Builder
    public TaskList(UUID id, UUID boardId, Long version, Instant created, Instant lastModified, String title, String description, Set<Task> tasks) {
        super(id, version, created, lastModified);
        this.title = title;
        this.description = description;
        this.tasks = tasks;
        this.boardId = boardId;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    public void deleteTask(UUID taskId){
        this.tasks.removeIf(task -> task.getId().equals(taskId));
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
