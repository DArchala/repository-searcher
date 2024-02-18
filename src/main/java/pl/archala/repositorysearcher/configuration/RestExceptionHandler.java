package pl.archala.repositorysearcher.configuration;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.ResponseError;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> handleUserNotFoundException(UserNotFoundException e) {
        return getResponseEntity(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(RepositoriesNotFoundException.class)
    public ResponseEntity<ResponseError> handleRepositoriesNotFoundException(RepositoriesNotFoundException e) {
        return getResponseEntity(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ResponseError> handleInternalServerException(InternalServerException e) {
        return getResponseEntity(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException e) {
        return getResponseEntity(BAD_REQUEST, e.getConstraintViolations().iterator().next().getMessageTemplate());
    }

    private ResponseEntity<ResponseError> getResponseEntity(HttpStatus status, String message) {
        ResponseError error = new ResponseError(status, message);
        return ResponseEntity.status(error.status().value()).body(error);
    }

}
