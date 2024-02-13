package pl.archala.repositorysearcher.model;

import lombok.Getter;
import pl.archala.repositorysearcher.dto.UserRepoDTO;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GithubUser {
    private final String ownerLogin;
    private final List<Repository> repositories;

    public GithubUser(String name, List<UserRepoDTO> userRepoDTOS) {
        this.ownerLogin = name;
        this.repositories = userRepoDTOS.stream().filter(r -> !r.fork()).map(Repository::new).collect(Collectors.toList());
    }
}
