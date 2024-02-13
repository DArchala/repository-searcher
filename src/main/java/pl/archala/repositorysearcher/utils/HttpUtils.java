package pl.archala.repositorysearcher.utils;

import java.net.URI;
import java.net.http.HttpRequest;

public final class HttpUtils {

    private static final String USER_REPOSITORIES_URL_TEMPLATE = "https://api.github.com/users/%s/repos";
    private static final String REPOSITORY_BRANCHES_URL_TEMPLATE = "https://api.github.com/repos/%s/%s/branches";

    public static HttpRequest getUserReposHttpRequest(String username) {
        return getHttpRequestByUri(URI.create(String.format(USER_REPOSITORIES_URL_TEMPLATE, username)));
    }

    public static HttpRequest getReposBranchesHttpRequest(String username, String repository) {
        return getHttpRequestByUri(URI.create(String.format(REPOSITORY_BRANCHES_URL_TEMPLATE, username, repository)));
    }

    private static HttpRequest getHttpRequestByUri(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }
}
