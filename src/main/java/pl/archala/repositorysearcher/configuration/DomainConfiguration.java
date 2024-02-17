package pl.archala.repositorysearcher.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DomainConfiguration {

    @Value("${github.url}")
    private String githubUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(githubUrl).build();
    }

}
