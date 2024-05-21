package nl.taskmate.boardservice.domain.exceptions;

public class ColorFormatException extends RuntimeException {
    public ColorFormatException(String message) {
        super(message);
    }
}
