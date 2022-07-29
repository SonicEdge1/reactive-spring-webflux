package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {
    private MovieInfoService movieInfoService;

    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movieInfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfoToAdd) {
        return movieInfoService.addMovieInfo(movieInfoToAdd).log();
    }

    @GetMapping("/movieInfo/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> getMovieInfoById(@PathVariable String id) {
        return movieInfoService.getMovieInfoById(id).log();
    }

    @GetMapping("/movieInfo")
    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoService.getAllMovieInfo().log();
    }
}
