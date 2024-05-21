package nl.dyllan.domain.exceptions;

public class UserNotAssignedToBoardException extends RuntimeException {
    public UserNotAssignedToBoardException(String message) {
        super(message);
    }
}
