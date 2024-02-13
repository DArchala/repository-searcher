package pl.archala.repositorysearcher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import pl.archala.repositorysearcher.dto.UserRepoDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Repository {

    private final String name;
    @JsonIgnore
    private final boolean fork;
    private final List<Branch> branches = new ArrayList<>();

    public Repository(UserRepoDTO userRepoDTO) {
        this.name = userRepoDTO.name();
        this.fork = userRepoDTO.fork();
    }
}
