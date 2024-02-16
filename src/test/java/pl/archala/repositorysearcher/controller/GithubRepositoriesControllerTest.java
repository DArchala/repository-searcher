package pl.archala.repositorysearcher.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import pl.archala.repositorysearcher.service.GithubClientService;
import wiremock.org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class GithubRepositoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubClientService githubClientService;

    private final String baseUrl = "/expected-controller-response";

    @SneakyThrows
    @Test
    void shouldReturnGithubUser() {
        //given
        String username = "PolskaBot";
        String expectedJsonPath = "%s/user-with-repositories.json".formatted(baseUrl);
        String expectedJsonContent = IOUtils.resourceToString(expectedJsonPath, StandardCharsets.UTF_8);

        List<Repository> repositories = List.of(new Repository("PolskaBotCore", false, List.of(new Branch("develop", "3d6738f7811424e1c9047c09f1b6b17511f90f20"))),
                new Repository("PolskaBotFade", false, List.of(new Branch("develop", "abc95eaa35989277c626d09849511c42b5b891ee"))),
                new Repository("PolskaBotRemote", false, List.of(new Branch("develop", "5ba55fc2d511d663b0ad8211e39b7b1eebc67261"))));

        GithubUser githubUser = new GithubUser(username, repositories);

        //when
        when(githubClientService.findUserRepositories(username)).thenReturn(githubUser);

        //then
        String actualJsonContent = mockMvc.perform(get("/api/repositories")
                        .contentType("application/json")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        assertEquals(expectedJsonContent, actualJsonContent);
    }

    @SneakyThrows
    @Test
    public void shouldReturnUserNotFoundErrorResponse() {
        //given
        String username = "notExistingUser";
        String exceptionMessage = "User with name %s does not exist.".formatted(username);
        String expectedJsonPath = "%s/user-not-found-error.json".formatted(baseUrl);
        String expectedJsonContent = IOUtils.resourceToString(expectedJsonPath, StandardCharsets.UTF_8);

        //when
        when(githubClientService.findUserRepositories(username)).thenThrow(new UserNotFoundException(exceptionMessage));

        //then
        String actualJsonContent = mockMvc.perform(get("/api/repositories")
                        .contentType("application/json")
                        .param("username", username))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        assertEquals(expectedJsonContent, actualJsonContent);
    }

    @SneakyThrows
    @Test
    public void shouldReturnRepositoriesNotFoundErrorResponse() {
        //given
        String username = "userWithNoRepository";
        String exceptionMessage = "User with name %s does not have any repositories.".formatted(username);
        String expectedJsonPath = "%s/user-has-no-repositories-error.json".formatted(baseUrl);
        String expectedJsonContent = IOUtils.resourceToString(expectedJsonPath, StandardCharsets.UTF_8);

        //when
        when(githubClientService.findUserRepositories(username)).thenThrow(new RepositoriesNotFoundException(exceptionMessage));

        //then
        String actualJsonContent = mockMvc.perform(get("/api/repositories")
                        .contentType("application/json")
                        .param("username", username))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString();

        assertEquals(expectedJsonContent, actualJsonContent);
    }

}