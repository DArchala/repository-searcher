package pl.archala.repositorysearcher.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.archala.repositorysearcher.dto.UserRepoDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repository {

    private String name;
    @JsonIgnore
    private boolean fork;
    private List<Branch> branches = new ArrayList<>();

    public Repository(UserRepoDTO userRepoDTO) {
        this.name = userRepoDTO.name();
        this.fork = userRepoDTO.fork();
    }
}
