package pl.archala.repositorysearcher.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.mappers.DtoMapper;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.service.GithubClientService;
import pl.archala.repositorysearcher.typeReferences.BranchDTOType;
import pl.archala.repositorysearcher.typeReferences.RepositoryDTOType;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class GithubClientServiceImpl implements GithubClientService {

    private final RestClient restClient;

    @Override
    public GithubUser findUserRepositories(String username) throws UserNotFoundException, InternalServerException, RepositoriesNotFoundException {
        try {
            return findByUsername(username);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new UserNotFoundException("User with name %s does not exist.".formatted(username));
                default -> throw new InternalServerException(e.getMessage());
            }
        }
    }

    private GithubUser findByUsername(String username) throws RepositoriesNotFoundException {
        Consumer<Repository> addBranches = (repository) -> restClient.get()
                .uri("/repos/{username}/{repository}/branches", username, repository.name())
                .retrieve()
                .body(new BranchDTOType())
                .stream()
                .map(DtoMapper::toBranch)
                .forEach(branch -> repository.branches().add(branch));

        List<Repository> repositories = restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new RepositoryDTOType())
                .stream()
                .filter(repoDTO -> !repoDTO.fork())
                .map(DtoMapper::toRepository)
                .peek(addBranches)
                .toList();

        if (repositories.isEmpty()) {
            throw new RepositoriesNotFoundException("User with name %s does not have any repositories.".formatted(username));
        }

        return new GithubUser(username, repositories);
    }


}
