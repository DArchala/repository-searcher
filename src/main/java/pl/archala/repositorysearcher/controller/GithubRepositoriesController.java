package pl.archala.repositorysearcher.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.service.GithubClientService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/repositories")
public class GithubRepositoriesController {

    private final GithubClientService githubClientService;

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GithubUser> findUserRepositories(@Valid @NotBlank(message = "Username must not be blank") @RequestParam String username) throws InternalServerException, GithubUserNotFoundException {
        return ResponseEntity.ok(githubClientService.findUserRepositories(username));
    }
}
