package pl.archala.repositorysearcher.service;

import pl.archala.repositorysearcher.exception.checked.UserNotFoundException;
import pl.archala.repositorysearcher.exception.checked.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.checked.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;

public interface GithubClientService {

    GithubUser findUserRepositories(String username) throws UserNotFoundException, InternalServerException, RepositoriesNotFoundException;

}
