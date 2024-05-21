package nl.dyllan.domain.exceptions;

public class TaskNotInTaskListException extends RuntimeException {
    public TaskNotInTaskListException(String message) {
        super(message);
    }
}
