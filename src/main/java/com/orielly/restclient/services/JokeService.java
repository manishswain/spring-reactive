package com.orielly.restclient.services;

import com.orielly.restclient.json.JokeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JokeService {

    final private RestTemplate template;

    final private WebClient client;

    @Autowired //dependency Injection
    public JokeService(RestTemplateBuilder builder, WebClient.Builder webClientBuilder) {
        template = builder.build();
        client = webClientBuilder.baseUrl("http://api.icndb.com").build();
    }

    public String getJokeSync(String first, String last) {
        String base = "http://api.icndb.com/jokes/random?limitTo=[nerdy]";
        String url = String.format("%s&firstName=%s&lastName=%s", base, first, last);
        return template.getForObject(url, JokeResponse.class).getValue().getJoke();
    }

    public Mono<String> getJokeAsync(String first, String last) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/jokes/random")
                        .queryParam("limitTo", "[nerdy]")
                        .queryParam("firstName", first)
                        .queryParam("lastName", last)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JokeResponse.class)//basically it is a promise
                .log()
                .map(jokeResponse -> jokeResponse.getValue().getJoke());
    }


}
