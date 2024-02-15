package pl.archala.repositorysearcher.model;

import java.util.List;

public record GithubUser(String ownerLogin, List<Repository> repositories) {

}
