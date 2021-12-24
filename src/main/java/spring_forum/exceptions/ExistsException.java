package spring_forum.exceptions;

public class ExistsException extends RuntimeException {

    public ExistsException(String message) {
        super(message);
    }
}
