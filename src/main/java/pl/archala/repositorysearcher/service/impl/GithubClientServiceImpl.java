package pl.archala.repositorysearcher.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import pl.archala.repositorysearcher.dto.RepositoryDTO;
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

@Service
@RequiredArgsConstructor
public class GithubClientServiceImpl implements GithubClientService {

    @Value("${github.url}")
    private String githubUrl;
    private final RestClient restClient;

    @Override
    public GithubUser findUserRepositories(String username) throws UserNotFoundException, InternalServerException, RepositoriesNotFoundException {
        try {
            return findByUsername(username);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new UserNotFoundException(STR."User with name \{username} does not exist.");
                default -> throw new InternalServerException(e.getMessage());
            }
        }
    }

    private GithubUser findByUsername(String username) throws RepositoriesNotFoundException {
        List<RepositoryDTO> repositoryDTOS = restClient.get()
                .uri(STR."\{githubUrl}/users/\{username}/repos")
                .retrieve()
                .body(new RepositoryDTOType());

        if (repositoryDTOS.isEmpty()) {
            throw new RepositoriesNotFoundException(STR."User with name \{username} does not have any repositories.");
        }

        List<Repository> repositories = repositoryDTOS.stream()
                .filter(repoDTO -> !repoDTO.fork())
                .map(DtoMapper::toRepository)
                .peek(repository -> putBranches(repository, username))
                .toList();
        return new GithubUser(username, repositories);
    }

    private void putBranches(Repository repository, String username) {
        restClient.get()
                .uri(STR."\{githubUrl}/repos/\{username}/\{repository.name()}/branches")
                .retrieve()
                .body(new BranchDTOType())
                .stream()
                .map(DtoMapper::toBranch)
                .forEach(branch -> repository.branches().add(branch));
    }

}
