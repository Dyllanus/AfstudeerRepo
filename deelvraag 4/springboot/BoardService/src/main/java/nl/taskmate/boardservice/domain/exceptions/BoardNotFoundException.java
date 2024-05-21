package nl.taskmate.boardservice.domain.exceptions;

public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}
