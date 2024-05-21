package nl.taskmate.boardservice.domain.exceptions;

public class ConvertDateException extends RuntimeException {
    public ConvertDateException(String message) {
        super(message);
    }
}
