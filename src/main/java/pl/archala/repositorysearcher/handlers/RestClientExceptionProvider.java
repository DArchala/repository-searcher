package pl.archala.repositorysearcher.handlers;

import org.springframework.web.client.RestClient;
import pl.archala.repositorysearcher.exception.unchecked.InternalServerRuntimeException;
import pl.archala.repositorysearcher.exception.unchecked.UserNotFoundRuntimeException;

public final class RestClientExceptionProvider {

    public static RestClient.ResponseSpec.ErrorHandler throwUserNotFoundException(String message) {
        return (request, response) -> {
            throw new UserNotFoundRuntimeException(message);
        };
    }

    public static RestClient.ResponseSpec.ErrorHandler throwInternalServerException() {
        return (request, response) -> {
            throw new InternalServerRuntimeException(response.getStatusText());
        };
    }
}
