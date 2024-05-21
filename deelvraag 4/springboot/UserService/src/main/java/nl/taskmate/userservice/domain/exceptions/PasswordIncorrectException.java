package nl.taskmate.userservice.domain.exceptions;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException() {
        super("Password is incorrect!");
    }

    public PasswordIncorrectException(String message) {
        super(message);
    }
}
