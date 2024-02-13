package pl.archala.repositorysearcher.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import pl.archala.repositorysearcher.dto.BranchDTO;
import pl.archala.repositorysearcher.dto.UserRepoDTO;
import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.utils.HttpUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static pl.archala.repositorysearcher.utils.HttpUtils.REPOSITORY_BRANCHES_URL_TEMPLATE;
import static pl.archala.repositorysearcher.utils.HttpUtils.USER_REPOSITORIES_URL_TEMPLATE;

@Service
public class GithubClientServiceImpl implements GithubClientService {

    private final HttpClient httpClient;
    private final Gson gson;

    public GithubClientServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public GithubUser findBranchesByUsername(String username) throws GithubUserNotFoundException, InternalServerException {
        GithubUser githubUser = findUserByName(username);
        completeUserBranches(githubUser);
        return githubUser;
    }

    private GithubUser findUserByName(String username) throws GithubUserNotFoundException, InternalServerException {
        HttpRequest request = HttpUtils.getHttpRequest(USER_REPOSITORIES_URL_TEMPLATE, username);
        HttpResponse<String> response = getRequestResponse(request);
        if (response.statusCode() == 404) {
            throw new GithubUserNotFoundException(username);
        }

        return new GithubUser(username, Arrays.asList(gson.fromJson(response.body(), UserRepoDTO[].class)));
    }

    private void completeUserBranches(GithubUser user) throws InternalServerException {
        for (Repository repository : user.getRepositories()) {
            HttpRequest request = HttpUtils.getHttpRequest(REPOSITORY_BRANCHES_URL_TEMPLATE, user.getOwnerLogin(), repository.getName());
            HttpResponse<String> response = getRequestResponse(request);

            List<BranchDTO> branchDTOS = Arrays.asList(gson.fromJson(response.body(), BranchDTO[].class));
            repository.getBranches().addAll(branchDTOS.stream().map(Branch::new).toList());
        }
    }

    private HttpResponse<String> getRequestResponse(HttpRequest request) throws InternalServerException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 404 && HttpStatusCode.valueOf(response.statusCode()).isError()) {
                throw new InternalServerException("Error occurred during sending request: " + response.body());
            }
            return response;
        } catch (InterruptedException | IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
