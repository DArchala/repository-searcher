package pl.archala.repositorysearcher.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class GithubClientServiceImpl implements GithubClientService {

    private final HttpRequestExecutor httpRequestExecutor;
    private final Gson gson;

    public GithubClientServiceImpl(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public GithubUser findBranchesByUsername(String username) throws GithubUserNotFoundException, InternalServerException {
        GithubUser githubUser = findUserByName(username);
        completeUserBranches(githubUser);
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

    private void completeUserBranches(GithubUser user) throws InternalServerException {
        try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
            List<RepositoryFiller> tasks = user.getRepositories().stream().map(repo -> new RepositoryFiller(httpRequestExecutor, gson, user.getOwnerLogin(), repo)).toList();
            user.getRepositories().clear();
            for (Future<Repository> result : e.invokeAll(tasks)) {
                user.getRepositories().add(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
