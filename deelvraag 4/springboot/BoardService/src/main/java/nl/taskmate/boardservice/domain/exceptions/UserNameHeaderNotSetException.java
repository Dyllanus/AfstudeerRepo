package nl.taskmate.boardservice.domain.exceptions;

public class UserNameHeaderNotSetException extends RuntimeException {
    public UserNameHeaderNotSetException(String message) {
        super(message);
    }
}
