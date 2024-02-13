package pl.archala.repositorysearcher.utils;

import java.net.URI;
import java.net.http.HttpRequest;

public final class HttpUtils {

    public static final String USER_REPOSITORIES_URL_TEMPLATE = "https://api.github.com/users/%s/repos";
    public static final String REPOSITORY_BRANCHES_URL_TEMPLATE = "https://api.github.com/repos/%s/%s/branches";

    public static HttpRequest getHttpRequest(String url, String... params) {
        return HttpRequest.newBuilder()
                .uri(URI.create(String.format(url, params)))
                .GET()
                .build();
    }
}
