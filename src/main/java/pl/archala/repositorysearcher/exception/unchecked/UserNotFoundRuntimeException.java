package pl.archala.repositorysearcher.exception.unchecked;

public class UserNotFoundRuntimeException extends RuntimeException {

    public UserNotFoundRuntimeException(String message) {
        super(message);
    }

}
