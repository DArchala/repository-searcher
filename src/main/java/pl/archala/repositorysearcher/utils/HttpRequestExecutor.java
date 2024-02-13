package pl.archala.repositorysearcher.utils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import pl.archala.repositorysearcher.exception.InternalServerException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class HttpRequestExecutor {

    private final HttpClient httpClient;

    public HttpResponse<String> getResponse(HttpRequest request) throws InternalServerException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (HttpStatusCode.valueOf(response.statusCode()).isError() && response.statusCode() != 404) {
                throw new InternalServerException("Error occurred during sending request: " + response.body());
            }
            return response;
        } catch (InterruptedException | IOException e) {
            throw new InternalServerException(e.getMessage());
        }
    }
}
