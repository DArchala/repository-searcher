package pl.archala.repositorysearcher.service;

import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;

public interface GithubClientService {

    GithubUser findUserRepositories(String username) throws GithubUserNotFoundException, InternalServerException;

}
