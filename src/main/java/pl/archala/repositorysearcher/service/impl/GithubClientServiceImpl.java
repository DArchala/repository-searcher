package pl.archala.repositorysearcher.service.impl;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.archala.repositorysearcher.dto.UserRepoDTO;
import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.service.GithubClientService;
import pl.archala.repositorysearcher.task.RepositoryFiller;
import pl.archala.repositorysearcher.utils.HttpRequestExecutor;
import pl.archala.repositorysearcher.utils.HttpUtils;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class GithubClientServiceImpl implements GithubClientService {

    private final HttpRequestExecutor httpRequestExecutor;
    private final Gson gson;

    @Override
    public GithubUser findRepositoriesByUsername(String username) throws GithubUserNotFoundException, InternalServerException {
        GithubUser githubUser = findUserByName(username);
        fillInUserBranches(githubUser);
        return githubUser;
    }

    private GithubUser findUserByName(String username) throws GithubUserNotFoundException, InternalServerException {
        HttpRequest request = HttpUtils.getUserReposHttpRequest(username);
        HttpResponse<String> response = httpRequestExecutor.getResponse(request);
        if (response.statusCode() == 404) {
            throw new GithubUserNotFoundException(username);
        }

        return new GithubUser(username, Arrays.asList(gson.fromJson(response.body(), UserRepoDTO[].class)));
    }

    private void fillInUserBranches(GithubUser user) throws InternalServerException {
        try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
            List<RepositoryFiller> tasks = user.getRepositories().stream().map(repo -> new RepositoryFiller(httpRequestExecutor, gson, user.getOwnerLogin(), repo)).toList();
            user.getRepositories().clear();
            for (Future<Repository> repository : e.invokeAll(tasks)) {
                user.getRepositories().add(repository.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
