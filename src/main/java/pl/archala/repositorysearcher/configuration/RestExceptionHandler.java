package pl.archala.repositorysearcher.configuration;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.archala.repositorysearcher.model.ResponseError;
import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GithubUserNotFoundException.class)
    public ResponseEntity<ResponseError> handleGithubUserNotFoundException(GithubUserNotFoundException e) {
        ResponseError error = new ResponseError(NOT_FOUND, e.getMessage());
        return ResponseEntity.status(error.status().value()).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException e) {
        ResponseError error = new ResponseError(BAD_REQUEST, e.getConstraintViolations().iterator().next().getMessageTemplate());
        return ResponseEntity.status(error.status().value()).body(error);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ResponseError> handleInternalServerException(InternalServerException e) {
        ResponseError error = new ResponseError(INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.status(error.status().value()).body(error);
    }

}
