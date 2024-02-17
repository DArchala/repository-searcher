package pl.archala.repositorysearcher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record Repository(String name, List<Branch> branches) {

}
