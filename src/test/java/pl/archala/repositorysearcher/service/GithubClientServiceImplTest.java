package pl.archala.repositorysearcher.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import wiremock.org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.StringTemplate.STR;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest(httpPort = 8181)
class GithubClientServiceImplTest {

    @Autowired
    private GithubClientService githubClientService;

    @SneakyThrows
    @Test
    public void shouldReturnGithubUser1() {
        //given
        String username = "PolskaBot";

        String basePath = "/github-mock-responses/mock-user-1/";

        String userMockJsonContent = IOUtils.resourceToString(STR."\{basePath}user.json", StandardCharsets.UTF_8);
        String repo1MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-1.json", StandardCharsets.UTF_8);
        String repo2MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-2.json", StandardCharsets.UTF_8);
        String repo3MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-3.json", StandardCharsets.UTF_8);

        String repo1 = "PolskaBotCore";
        String repo2 = "PolskaBotFade";
        String repo3 = "PolskaBotRemote";

        String userUrl = STR."/users/\{username}/repos";
        String repo1Url = STR."/repos/\{username}/\{repo1}/branches";
        String repo2Url = STR."/repos/\{username}/\{repo2}/branches";
        String repo3Url = STR."/repos/\{username}/\{repo3}/branches";

        //when
        stub(userUrl, 200, userMockJsonContent);
        stub(repo1Url, 200, repo1MockJsonContent);
        stub(repo2Url, 200, repo2MockJsonContent);
        stub(repo3Url, 200, repo3MockJsonContent);

        GithubUser actualGithubUser = githubClientService.findUserRepositories(username);

        //then
        assertNotNull(actualGithubUser);
        assertNotNull(actualGithubUser.repositories());
        assertEquals(username, actualGithubUser.ownerLogin());
        assertEquals(3, actualGithubUser.repositories().size());

        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo1)).toList().size());
        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo2)).toList().size());
        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo3)).toList().size());

        assertTrue(actualGithubUser.repositories().stream().noneMatch(Repository::fork));

    }

    @SneakyThrows
    @Test
    public void shouldReturnGithubUser2() {
        //given
        String username = "VikAnt8";

        String basePath = "/github-mock-responses/mock-user-2/";

        String userMockJsonContent = IOUtils.resourceToString(STR."\{basePath}user.json", StandardCharsets.UTF_8);
        String repo1MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-1.json", StandardCharsets.UTF_8);
        String repo2MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-2.json", StandardCharsets.UTF_8);
        String repo3MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-3.json", StandardCharsets.UTF_8);
        String repo4MockJsonContent = IOUtils.resourceToString(STR."\{basePath}user-repository-4.json", StandardCharsets.UTF_8);

        String repo1 = "Fishhub";
        String repo2 = "FlickrTestApp";
        String repo3 = "IndependenceDay";
        String repo4 = "PolskaVisa";

        String userUrl = STR."/users/\{username}/repos";
        String repo1Url = STR."/repos/\{username}/\{repo1}/branches";
        String repo2Url = STR."/repos/\{username}/\{repo2}/branches";
        String repo3Url = STR."/repos/\{username}/\{repo3}/branches";
        String repo4Url = STR."/repos/\{username}/\{repo4}/branches";

        //when
        stub(userUrl, 200, userMockJsonContent);
        stub(repo1Url, 200, repo1MockJsonContent);
        stub(repo2Url, 200, repo2MockJsonContent);
        stub(repo3Url, 200, repo3MockJsonContent);
        stub(repo4Url, 200, repo4MockJsonContent);

        GithubUser actualGithubUser = githubClientService.findUserRepositories(username);

        //then
        assertNotNull(actualGithubUser);
        assertNotNull(actualGithubUser.repositories());
        assertEquals(username, actualGithubUser.ownerLogin());
        assertEquals(4, actualGithubUser.repositories().size());

        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo1)).toList().size());
        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo2)).toList().size());
        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo3)).toList().size());
        assertEquals(1, actualGithubUser.repositories().stream().filter(r -> r.name().equals(repo4)).toList().size());

        assertTrue(actualGithubUser.repositories().stream().noneMatch(Repository::fork));

    }

    @SneakyThrows
    @Test
    public void shouldThrowRepositoriesNotFoundException() {
        //given
        String username = "userWithoutRepositories";
        String url = STR."/users/\{username}/repos";

        //when
        stub(url, 200, "[]");

        //then
        assertThrows(RepositoriesNotFoundException.class, () -> githubClientService.findUserRepositories(username));
    }

    @SneakyThrows
    @Test
    public void shouldThrowGithubUserNotFoundException() {
        //given
        String basePath = "/github-mock-responses/";

        String username = "notExistingUserName";
        String userNotFoundJsonContent = IOUtils.resourceToString(STR."\{basePath}/user-not-found.json", StandardCharsets.UTF_8);
        String url = STR."/users/\{username}/repos";

        //when
        stub(url, 404, userNotFoundJsonContent);

        //then
        assertThrows(UserNotFoundException.class, () -> githubClientService.findUserRepositories(username));

    }

    private void stub(String url, int status, String body) {
        stubFor(get(url)
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(status)
                        .withBody(body)
                ));

    }

}