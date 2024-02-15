package pl.archala.repositorysearcher.configuration;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.archala.repositorysearcher.exception.checked.UserNotFoundException;
import pl.archala.repositorysearcher.exception.checked.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.checked.InternalServerException;
import pl.archala.repositorysearcher.model.ResponseError;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> handleUserNotFoundException(UserNotFoundException e) {
        return getResponseEntityFromError(new ResponseError(NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(RepositoriesNotFoundException.class)
    public ResponseEntity<ResponseError> handleRepositoriesNotFoundException(RepositoriesNotFoundException e) {
        return getResponseEntityFromError(new ResponseError(NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ResponseError> handleInternalServerException(InternalServerException e) {
        return getResponseEntityFromError(new ResponseError(INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException e) {
        return getResponseEntityFromError(new ResponseError(BAD_REQUEST, e.getConstraintViolations().iterator().next().getMessageTemplate()));
    }

    private ResponseEntity<ResponseError> getResponseEntityFromError(ResponseError responseError) {
        return ResponseEntity.status(responseError.status().value()).body(responseError);
    }
}
