package pl.archala.repositorysearcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.archala.repositorysearcher.dto.BranchDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private String name;
    private String lastCommitSha;

    public Branch(BranchDTO branchDTO) {
        this.name = branchDTO.name();
        this.lastCommitSha = branchDTO.commit().sha();
    }
}
