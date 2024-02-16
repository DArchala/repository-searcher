package pl.archala.repositorysearcher.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.model.GithubUser;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WireMockTest(httpPort = 8181)
class GithubClientServiceImplTest {

    @Autowired
    private GithubClientService githubClientService;

    @SneakyThrows
    @Test
    public void shouldReturnGithubUser() {
        //given
        String username = "someUserName";

        //when
        stubFor(get("/users/someUserName/repos")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                                [
                                    {
                                        "name": "repositoryName",
                                        "owner": {
                                            "login": "someUserName"
                                        },
                                        "fork": false
                                    }
                                ]
                                """)
                ));

        stubFor(get("/repos/someUserName/repositoryName/branches")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                                [
                                     {
                                         "name": "master",
                                         "commit": {
                                             "sha": "38ffe9fbd1df35678692d288eb57a18202d1de44",
                                             "url": "https://api.github.com/repos/someUserName/repositoryName/commits/38ffe9fbd1df35678692d288eb57a18202d1de44"
                                         },
                                         "protected": false
                                     }
                                 ]
                                """)
                ));

        GithubUser actualGithubUser = githubClientService.findUserRepositories(username);

        //then
        assertNotNull(actualGithubUser);

    }

    @SneakyThrows
    @Test
    public void shouldThrowRepositoriesNotFoundException() {
        //given
        String username = "someUserName";

        //when
        stubFor(get("/users/someUserName/repos")
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("[]")
                ));

        //then
        assertThrows(RepositoriesNotFoundException.class, () -> githubClientService.findUserRepositories(username));

    }

    @SneakyThrows
    @Test
    public void shouldThrowGithubUserNotFoundException() {
        //given
        String username = "someUserName";

        //when
        stubFor(get("/users/someUserName/repos")
                .willReturn(notFound()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBody("""
                                {
                                    "message": "Not Found",
                                    "documentation_url": "https://docs.github.com/rest/repos/repos#list-repositories-for-a-user"
                                }
                                """)
                ));

        //then
        assertThrows(UserNotFoundException.class, () -> githubClientService.findUserRepositories(username));

    }


}