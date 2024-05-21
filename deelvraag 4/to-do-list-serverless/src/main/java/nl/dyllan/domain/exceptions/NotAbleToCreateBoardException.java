package nl.dyllan.domain.exceptions;

public class NotAbleToCreateBoardException extends RuntimeException {
    public NotAbleToCreateBoardException(String message) {
        super(message);
    }
}
