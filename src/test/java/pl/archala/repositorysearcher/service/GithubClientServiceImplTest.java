package pl.archala.repositorysearcher.service;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.archala.repositorysearcher.exception.RepositoriesNotFoundException;
import pl.archala.repositorysearcher.exception.UserNotFoundException;
import pl.archala.repositorysearcher.model.Branch;
import pl.archala.repositorysearcher.model.GithubUser;
import pl.archala.repositorysearcher.model.Repository;
import wiremock.org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WireMockTest(httpPort = 8181)
class GithubClientServiceImplTest {

    @Autowired
    private GithubClientService githubClientService;

    private final String basePath = "/github-mock-responses";

    @SneakyThrows
    @Test
    public void shouldReturnGithubUser1() {
        //given
        String userPath = "%s/mock-user-1".formatted(basePath);
        String username = "PolskaBot";

        String userJsonContent = IOUtils.resourceToString("%s/user.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo1JsonContent = IOUtils.resourceToString("%s/user-repository-1.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo2JsonContent = IOUtils.resourceToString("%s/user-repository-2.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo3JsonContent = IOUtils.resourceToString("%s/user-repository-3.json".formatted(userPath), StandardCharsets.UTF_8);

        String repo1 = "PolskaBotCore";
        String repo2 = "PolskaBotFade";
        String repo3 = "PolskaBotRemote";

        String userUrl = "/users/%s/repos".formatted(username);
        String repo1Url = "/repos/%s/%s/branches".formatted(username, repo1);
        String repo2Url = "/repos/%s/%s/branches".formatted(username, repo2);
        String repo3Url = "/repos/%s/%s/branches".formatted(username, repo3);

        //when
        stub(userUrl, 200, userJsonContent);
        stub(repo1Url, 200, repo1JsonContent);
        stub(repo2Url, 200, repo2JsonContent);
        stub(repo3Url, 200, repo3JsonContent);

        GithubUser actualGithubUser = githubClientService.findUserRepositories(username);

        //then
        assertNotNull(actualGithubUser);
        assertNotNull(actualGithubUser.repositories());
        assertEquals(username, actualGithubUser.ownerLogin());
        assertEquals(3, actualGithubUser.repositories().size());

        Repository repository1 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo1));
        Repository repository2 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo2));
        Repository repository3 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo3));

        Branch branch1 = assertDoesNotThrow(() -> findBranchByName(repository1.branches(), "develop"));
        Branch branch2 = assertDoesNotThrow(() -> findBranchByName(repository2.branches(), "develop"));
        Branch branch3 = assertDoesNotThrow(() -> findBranchByName(repository3.branches(), "develop"));

        assertEquals(1, repository1.branches().size());
        assertEquals("3d6738f7811424e1c9047c09f1b6b17511f90f20", branch1.lastCommitSha());

        assertEquals(1, repository2.branches().size());
        assertEquals("abc95eaa35989277c626d09849511c42b5b891ee", branch2.lastCommitSha());

        assertEquals(1, repository3.branches().size());
        assertEquals("5ba55fc2d511d663b0ad8211e39b7b1eebc67261", branch3.lastCommitSha());

        assertTrue(actualGithubUser.repositories().stream().noneMatch(Repository::fork));
    }

    @SneakyThrows
    @Test
    public void shouldReturnGithubUser2() {
        //given
        String username = "VikAnt8";

        String userPath = "%s/mock-user-2".formatted(basePath);

        String userJsonContent = IOUtils.resourceToString("%s/user.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo1JsonContent = IOUtils.resourceToString("%s/user-repository-1.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo2JsonContent = IOUtils.resourceToString("%s/user-repository-2.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo3JsonContent = IOUtils.resourceToString("%s/user-repository-3.json".formatted(userPath), StandardCharsets.UTF_8);
        String repo4JsonContent = IOUtils.resourceToString("%s/user-repository-4.json".formatted(userPath), StandardCharsets.UTF_8);

        String repo1 = "Fishhub";
        String repo2 = "FlickrTestApp";
        String repo3 = "IndependenceDay";
        String repo4 = "PolskaVisa";

        String userUrl = "/users/%s/repos".formatted(username);
        String repo1Url = "/repos/%s/%s/branches".formatted(username, repo1);
        String repo2Url = "/repos/%s/%s/branches".formatted(username, repo2);
        String repo3Url = "/repos/%s/%s/branches".formatted(username, repo3);
        String repo4Url = "/repos/%s/%s/branches".formatted(username, repo4);

        //when
        stub(userUrl, 200, userJsonContent);
        stub(repo1Url, 200, repo1JsonContent);
        stub(repo2Url, 200, repo2JsonContent);
        stub(repo3Url, 200, repo3JsonContent);
        stub(repo4Url, 200, repo4JsonContent);

        GithubUser actualGithubUser = githubClientService.findUserRepositories(username);

        //then
        assertNotNull(actualGithubUser);
        assertNotNull(actualGithubUser.repositories());
        assertEquals(username, actualGithubUser.ownerLogin());
        assertEquals(4, actualGithubUser.repositories().size());

        Repository repository1 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo1));
        Repository repository2 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo2));
        Repository repository3 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo3));
        Repository repository4 = assertDoesNotThrow(() -> findRepoByName(actualGithubUser.repositories(), repo4));

        Branch branch1 = assertDoesNotThrow(() -> findBranchByName(repository1.branches(), "master"));
        Branch branch2 = assertDoesNotThrow(() -> findBranchByName(repository2.branches(), "master"));
        Branch branch3 = assertDoesNotThrow(() -> findBranchByName(repository3.branches(), "master"));
        Branch branch4 = assertDoesNotThrow(() -> findBranchByName(repository4.branches(), "master"));

        assertEquals(1, repository1.branches().size());
        assertEquals("e331c9cc1eb7a38ccaf4f01bc737f074cc402200", branch1.lastCommitSha());

        assertEquals(1, repository2.branches().size());
        assertEquals("faf56c15b0ba53a4d4d4a73a907cd164297bf2d8", branch2.lastCommitSha());

        assertEquals(1, repository3.branches().size());
        assertEquals("60fad251cdc6325f36a1e3e7e69d7ecfbbeb4cfa", branch3.lastCommitSha());

        assertEquals(1, repository4.branches().size());
        assertEquals("ecfa7b77dac1549bdee64295799ac0f2918fc02d", branch4.lastCommitSha());

        assertTrue(actualGithubUser.repositories().stream().noneMatch(Repository::fork));
    }

    @SneakyThrows
    @Test
    public void shouldThrowRepositoriesNotFoundException() {
        //given
        String username = "userWithoutRepositories";
        String url = "/users/%s/repos".formatted(username);

        //when
        stub(url, 200, "[]");

        //then
        assertThrows(RepositoriesNotFoundException.class, () -> githubClientService.findUserRepositories(username));
    }

    @SneakyThrows
    @Test
    public void shouldThrowGithubUserNotFoundException() {
        //given
        String username = "notExistingUserName";
        String userNotFoundJsonContent = IOUtils.resourceToString("%s/user-not-found.json".formatted(basePath), StandardCharsets.UTF_8);
        String url = "/users/%s/repos".formatted(username);

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

    private Repository findRepoByName(List<Repository> repositories, String name) {
        return repositories.stream().filter(r -> r.name().equals(name)).findAny().orElseThrow();
    }

    private Branch findBranchByName(List<Branch> branches, String name) {
        return branches.stream().filter(b -> b.name().equals(name)).findAny().orElseThrow();
    }

}