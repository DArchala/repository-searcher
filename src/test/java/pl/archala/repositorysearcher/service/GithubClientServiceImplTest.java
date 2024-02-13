package pl.archala.repositorysearcher.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.archala.repositorysearcher.model.GithubUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GithubClientServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @Test
    public void shouldReturnCorrectGithubUserBranchesData1() throws Exception {
        //given
        String username = "DITAS-PROJECT";

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/repositories")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(200))
                .andReturn();

        GithubUser user = gson.fromJson(mvcResult.getResponse().getContentAsString(), GithubUser.class);

        //then
        assertNotNull(user);
        assertEquals(29, user.getRepositories().size());
        assertEquals("DITAS-PROJECT", user.getOwnerLogin());
        assertEquals(26, user.getRepositories().getFirst().getBranches().size());
        assertEquals("application-requirements-interface", user.getRepositories().getFirst().getName());

    }

    @SneakyThrows
    @Test
    public void shouldReturnUserNotFoundInfoIfGithubUserDoesNotExist() {
        //given
        String username = "notExistingGithubUser";

        //when
        MvcResult mvcResult = mockMvc.perform(get("/api/repositories")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        //then
        assertEquals(404, mvcResult.getResponse().getStatus());

    }
}