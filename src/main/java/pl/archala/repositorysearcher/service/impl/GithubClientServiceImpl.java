package pl.archala.repositorysearcher.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.service.GithubClientService;
import pl.archala.repositorysearcher.typeReferences.BranchDTOType;
import pl.archala.repositorysearcher.typeReferences.UserRepoDTOType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
public class GithubClientServiceImpl implements GithubClientService {

    private static final String USER_REPOSITORIES_URL_TEMPLATE = "https://api.github.com/users/%s/repos";
    private static final String REPOSITORY_BRANCHES_URL_TEMPLATE = "https://api.github.com/repos/%s/%s/branches";

    private final RestClient restClient;

    @Override
    public GithubUser findUserRepositories(String username) {
        List<Repository> repositories = restClient.get()
                .uri(USER_REPOSITORIES_URL_TEMPLATE.formatted(username))
                .retrieve()
                .body(new UserRepoDTOType()).stream()
                .filter(repoDTO -> !repoDTO.fork())
                .map(r -> new Repository(r.name(), r.fork(), new ArrayList<>()))
                .peek(repository -> fillRepository(repository, username))
                .toList();
        return new GithubUser(username, repositories);
    }

    private void fillRepository(Repository repository, String username) {
        restClient.get()
                .uri(REPOSITORY_BRANCHES_URL_TEMPLATE.formatted(username, repository.name()))
                .retrieve()
                .body(new BranchDTOType()).stream()
                .map(branchDTO -> new Branch(branchDTO.name(), branchDTO.commit().sha()))
                .forEach(branch -> repository.branches().add(branch));
    }

}
