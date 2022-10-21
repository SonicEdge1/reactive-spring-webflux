package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {
    private MovieInfoService movieInfoService;

    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    @PostMapping("/movieInfo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfoToAdd) {
        return movieInfoService.addMovieInfo(movieInfoToAdd).log();
    }

    @PutMapping("/movieInfo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MovieInfo> modifyMovieById(@PathVariable String id, @RequestBody @Valid MovieInfo updatedMovieInfo) {
        return movieInfoService.updateMovieInfoById(id, updatedMovieInfo).log();
    }

    @GetMapping("/movieInfo/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return movieInfoService.getMovieInfoById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/movieInfo/year/{year}")
    public Flux<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable Integer year) {
        return movieInfoService.getMovieInfoByYear(year)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build()).log();
    }

    @DeleteMapping("/movieInfo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
        return movieInfoService.deleteMovieInfoById(id);
    }

    @GetMapping("/movieInfo")
    public Flux<MovieInfo> getAllMovieInfo(@RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "titleWord", required = false) String titleWord) {
        if (year != null) {
            return movieInfoService.getMovieInfoByYear(year);
        }
        if (titleWord != null) {
            return movieInfoService.getAllMovieInfo().filter(movieInfo -> movieInfo.getTitle().contains(titleWord));
        }
        if (title != null) {
            return movieInfoService.getMovieInfoByTitle(title);
        }
        return movieInfoService.getAllMovieInfo().log();
    }
}
