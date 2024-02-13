package pl.archala.repositorysearcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRepoDTO(
        String name,
        boolean fork,
        OwnerDTO ownerDTO) {
}
