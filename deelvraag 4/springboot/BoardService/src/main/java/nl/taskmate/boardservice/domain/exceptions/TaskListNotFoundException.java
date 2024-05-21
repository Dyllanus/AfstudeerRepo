package nl.taskmate.boardservice.domain.exceptions;

public class TaskListNotFoundException extends RuntimeException {
    public TaskListNotFoundException(String message) {
        super(message);
    }
}
