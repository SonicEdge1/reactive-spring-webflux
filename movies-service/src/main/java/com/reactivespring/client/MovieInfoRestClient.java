package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MovieInfoRestClient {

    private WebClient webClient;

    @Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl;

    public MovieInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var url = movieInfoUrl.concat("/{id}");
        return webClient
                .get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    log.info("retrieveMovieInfo call status code is: " + clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new MoviesInfoClientException(
                                "There is no Movie Available for Id: " + movieId, clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(
                                    responseMessage, clientResponse.statusCode().value())));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    log.info("retrieveMovieInfo call status code is: " + clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorMessage -> Mono.error(new MoviesInfoServerException(
                                    "Problem encountered with MovieInfoService: " + errorMessage)));
                })
                .bodyToMono(MovieInfo.class)
                .log();
    }
}
