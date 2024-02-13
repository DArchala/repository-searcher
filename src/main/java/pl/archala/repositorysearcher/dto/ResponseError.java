package pl.archala.repositorysearcher.dto;

import org.springframework.http.HttpStatus;

public record ResponseError(HttpStatus status, String message) {

}
