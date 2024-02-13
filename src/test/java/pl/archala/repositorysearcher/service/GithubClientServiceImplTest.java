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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GithubClientServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();

    @Test
    public void shouldReturnCorrectGithubUserBranchesData1() throws Exception {
        //given
        String username = "ripienaar";

        //when
        MvcResult mvcGithubUser = mockMvc.perform(get("/api/branches")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(200)).andReturn();

        GithubUser githubUser = gson.fromJson(mvcGithubUser.getResponse().getContentAsString(), GithubUser.class);

        //then
        assertNotNull(githubUser);

    }

    @SneakyThrows
    @Test
    public void shouldReturnUserNotFoundInfo() {
        //given
        String username = "notExistingGithubUser";

        //when
        mockMvc.perform(get("/api/branches")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is(404));

        //then


    }
}