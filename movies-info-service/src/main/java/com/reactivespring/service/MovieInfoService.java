package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieInfoService {

    private MovieInfoRepository movieInfoRepository;

    public MovieInfoService(MovieInfoRepository movieInfoRepository) {
        this.movieInfoRepository = movieInfoRepository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfoToAdd) {
        return movieInfoRepository.save(movieInfoToAdd);
    }

    public Flux<MovieInfo> getAllMovieInfo() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Flux<MovieInfo> getMovieInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
    public Flux<MovieInfo> getMovieInfoByTitle(String title) {
        return movieInfoRepository.findByTitle(title);
    }

    public Mono<Void> deleteMovieInfoById(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Mono<MovieInfo> updateMovieInfoById(String id, MovieInfo updatedMovieInfo) {
        return movieInfoRepository.findById(id).flatMap( movieInfo -> {
            movieInfo.setTitle(updatedMovieInfo.getTitle());
            movieInfo.setYear(updatedMovieInfo.getYear());
            movieInfo.setCast(updatedMovieInfo.getCast());
            movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
            return movieInfoRepository.save(movieInfo);
        });
    }
}
