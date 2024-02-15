package pl.archala.repositorysearcher.service;

import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;

public interface GithubClientService {

    GithubUser findUserRepositories(String username) throws UserNotFoundException, InternalServerException, RepositoriesNotFoundException;

}
