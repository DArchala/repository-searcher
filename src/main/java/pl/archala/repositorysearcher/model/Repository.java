package pl.archala.repositorysearcher.model;

import java.util.List;

public record Repository(String name, boolean fork, List<Branch> branches) {

}
