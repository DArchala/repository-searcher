package pl.archala.repositorysearcher.service;

import pl.archala.repositorysearcher.exception.GithubUserNotFoundException;
import pl.archala.repositorysearcher.exception.InternalServerException;
import pl.archala.repositorysearcher.model.GithubUser;

public interface GithubClientService {

    GithubUser findBranchesByUsername(String username) throws GithubUserNotFoundException, InternalServerException;

}
