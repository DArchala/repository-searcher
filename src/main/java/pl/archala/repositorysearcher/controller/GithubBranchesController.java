package pl.archala.repositorysearcher.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/branches")
public class GithubBranchesController {

    private final GithubClientService githubClientService;

    @GetMapping
    public ResponseEntity<GithubUser> findUserBranches(@Valid @NotBlank(message = "Username must not be blank") @RequestParam String username) throws InternalServerException, GithubUserNotFoundException {
        GithubUser githubUser = githubClientService.findBranchesByUsername(username);
        return ResponseEntity.ok(githubUser);
    }
}
