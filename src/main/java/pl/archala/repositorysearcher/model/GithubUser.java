package pl.archala.repositorysearcher.model;

import lombok.Getter;
import pl.archala.repositorysearcher.dto.UserRepoDTO;

import java.util.List;

@Getter
public class GithubUser {
    private final String name;
    private final List<Repository> repositories;

    public GithubUser(String name, List<UserRepoDTO> userRepoDTOS) {
        this.name = name;
        this.repositories = userRepoDTOS.stream().filter(r -> !r.fork()).map(Repository::new).toList();
    }
}
