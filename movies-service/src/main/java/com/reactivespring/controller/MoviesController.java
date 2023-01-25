package com.reactivespring.controller;

import com.reactivespring.client.MovieInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MovieInfoRestClient movieInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MovieInfoRestClient movieInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.movieInfoRestClient = movieInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id") String movieId){
        log.info("\n\n************* movieInfoRestClient: " + movieInfoRestClient + " ****************\n\n");
        log.info("\n\n************* reviewsRestClient: " + reviewsRestClient + " ****************\n\n");
        return movieInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var reviewsListMono = reviewsRestClient.retrieveReviews(movieId).collectList();
                    return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }


}
