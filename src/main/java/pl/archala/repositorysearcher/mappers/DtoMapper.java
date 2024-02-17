package pl.archala.repositorysearcher.mappers;

import pl.archala.repositorysearcher.dto.BranchDTO;
import pl.archala.repositorysearcher.dto.RepositoryDTO;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.Repository;

import java.util.ArrayList;

public final class DtoMapper {

    public static Repository toRepository(RepositoryDTO repositoryDTO) {
        return new Repository(repositoryDTO.name(), new ArrayList<>());
    }

    public static Branch toBranch(BranchDTO branchDTO) {
        return new Branch(branchDTO.name(), branchDTO.commit().sha());
    }
}
