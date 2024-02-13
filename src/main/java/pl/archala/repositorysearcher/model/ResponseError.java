package pl.archala.repositorysearcher.model;

import org.springframework.http.HttpStatus;

public record ResponseError(HttpStatus status, String message) {

}
