package pl.archala.repositorysearcher.model;

import lombok.Getter;
import pl.archala.repositorysearcher.dto.BranchDTO;

@Getter
public class Branch {
    private final String name;
    private final String lastCommitSha;

    public Branch(BranchDTO branchDTO) {
        this.name = branchDTO.name();
        this.lastCommitSha = branchDTO.commit().sha();
    }
}
