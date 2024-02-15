package pl.archala.repositorysearcher.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pl.archala.repositorysearcher.dto.RepositoryDTO;
import pl.archala.repositorysearcher.exception.checked.UserNotFoundException;
import pl.archala.repositorysearcher.exception.checked.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.checked.InternalServerException;
import pl.archala.repositorysearcher.exception.unchecked.UserNotFoundRuntimeException;
import pl.archala.repositorysearcher.exception.unchecked.InternalServerRuntimeException;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.service.GithubClientService;
import pl.archala.repositorysearcher.typeReferences.BranchDTOType;
import pl.archala.repositorysearcher.typeReferences.RepositoryDTOType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubClientServiceImpl implements GithubClientService {

    private static final String USER_REPOSITORIES_URL = "https://api.github.com/users/%s/repos";
    private static final String REPOSITORY_BRANCHES_URL = "https://api.github.com/repos/%s/%s/branches";
    private static final String USER_DOES_NOT_EXIST = "User with name %s does not exist.";
    private static final String USER_REPOSITORIES_DO_NOT_EXIST = "User with name %s does not have any repositories.";

    private final RestClient restClient;

    @Override
    public GithubUser findUserRepositories(String username) throws UserNotFoundException, InternalServerException, RepositoriesNotFoundException {
        try {
            return findByUsername(username);
        } catch (UserNotFoundRuntimeException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (InternalServerRuntimeException e) {
            throw new InternalServerException(e.getMessage());
        }

    }

    private GithubUser findByUsername(String username) throws RepositoriesNotFoundException {
        List<RepositoryDTO> repositoryDTOS = restClient.get()
                .uri(USER_REPOSITORIES_URL.formatted(username))
                .retrieve()
                .onStatus(code -> code.value() == 404, (request, response) -> {
                    throw new UserNotFoundRuntimeException(USER_DOES_NOT_EXIST.formatted(username));
                })
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new InternalServerRuntimeException(response.getStatusText());
                })
                .body(new RepositoryDTOType());

        if (repositoryDTOS.isEmpty()) {
            throw new RepositoriesNotFoundException(USER_REPOSITORIES_DO_NOT_EXIST.formatted(username));
        }

        List<Repository> repositories = repositoryDTOS.stream()
                .filter(repoDTO -> !repoDTO.fork())
                .map(repoDTO -> new Repository(repoDTO.name(), repoDTO.fork(), new ArrayList<>()))
                .peek(repository -> putBranches(repository, username))
                .toList();
        return new GithubUser(username, repositories);
    }

    private void putBranches(Repository repository, String username) {
        restClient.get()
                .uri(REPOSITORY_BRANCHES_URL.formatted(username, repository.name()))
                .retrieve()
                .body(new BranchDTOType())
                .stream()
                .map(branchDTO -> new Branch(branchDTO.name(), branchDTO.commit().sha()))
                .forEach(branch -> repository.branches().add(branch));
    }

}
