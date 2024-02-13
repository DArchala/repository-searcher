package pl.archala.repositorysearcher.exception;

public class GithubUserNotFoundException extends Exception {

    public GithubUserNotFoundException(String username) {
        super(String.format("User with name %s does not exist.", username));
    }
}
