package pl.archala.repositorysearcher.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DomainConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

}
