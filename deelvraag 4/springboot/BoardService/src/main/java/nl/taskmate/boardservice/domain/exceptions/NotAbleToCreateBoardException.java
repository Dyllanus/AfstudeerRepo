package nl.taskmate.boardservice.domain.exceptions;

public class NotAbleToCreateBoardException extends RuntimeException {
    public NotAbleToCreateBoardException(String message) {
        super(message);
    }
}
