package pl.archala.repositorysearcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRepoDTO(
        String name,
        boolean fork,
        OwnerDTO owner) {
}
