package pl.archala.repositorysearcher.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import pl.archala.repositorysearcher.dto.BranchDTO;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.utils.HttpRequestExecutor;
import pl.archala.repositorysearcher.utils.HttpUtils;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class RepositoryFiller implements Callable<Repository> {

    private final HttpRequestExecutor httpRequestExecutor;
    private final Gson gson;
    private final String username;
    private final Repository repository;

    @Override
    public Repository call() throws Exception {
        HttpRequest request = HttpUtils.getReposBranchesHttpRequest(username, repository.getName());
        HttpResponse<String> response = httpRequestExecutor.getResponse(request);
        List<BranchDTO> branchDTOS = Arrays.asList(gson.fromJson(response.body(), BranchDTO[].class));
        repository.getBranches().addAll(branchDTOS.stream().map(Branch::new).toList());
        return repository;
    }
}
