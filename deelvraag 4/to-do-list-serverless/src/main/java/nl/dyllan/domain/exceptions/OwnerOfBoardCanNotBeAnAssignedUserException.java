package nl.dyllan.domain.exceptions;

public class OwnerOfBoardCanNotBeAnAssignedUserException extends RuntimeException {
    public OwnerOfBoardCanNotBeAnAssignedUserException(String message) {
        super(message);
    }
}
