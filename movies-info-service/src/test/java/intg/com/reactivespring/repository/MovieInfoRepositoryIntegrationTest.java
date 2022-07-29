package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieInfoList = List.of(
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")),
                new MovieInfo(null, "Men in Black 3",
                        2012, List.of("Will Smith", "Tommy Lee Jones"), LocalDate.parse("2012-07-13"))
        );

        movieInfoRepository.saveAll(movieInfoList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAllTest() {
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void findByIdTest() {
        var id = "abc";
        var moviesInfoMono = movieInfoRepository.findById(id).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(movieInfo.getName(), "Dark Knight Rises");
                })
                 .verifyComplete();
    }

    @Test
    void saveMovieTest() {
        var newMovie = new MovieInfo(null, "Time Bandits",
                1981, List.of("John Cleese", "Sean Connery", "Shelly Duvall"), LocalDate.parse("1981-11-06"));
        var moviesInfoMono = movieInfoRepository.save(newMovie).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Time Bandits", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieTest() {
        var movieToChange = movieInfoRepository.findById("abc").block();
        movieToChange.setYear(1999);
        var moviesInfoMono = movieInfoRepository.save(movieToChange).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals(1999, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieTest() {
        var movieToDelete = movieInfoRepository.findById("abc").block();
//      movieInfoRepository.deleteById("abc");  //shortcut
        movieInfoRepository.delete(movieToDelete).block();
        var allMovies = movieInfoRepository.findAll();

        StepVerifier.create(allMovies)
                .expectNextCount(3);
    }

    @Test
    void findByExampleTest() {
        var exampleMovie = new MovieInfo(null, null,
                2008, null, null);
        //findOne will throw an exception if more than one result is returned.
        //findAll(Example) will find multiples.
        var myMovie = movieInfoRepository.findOne(Example.of(exampleMovie)).log();
        StepVerifier.create(myMovie)
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getName());
                }).verifyComplete();
    }

    @Test
    void findAllByExampleTest() {
        var exampleMovie = new MovieInfo(null, null,
                2012, null, null);
        //findOne will throw an exception if more than one result is returned.
        //findAll(Example) will find multiples.
        var myMovie = movieInfoRepository.findAll(Example.of(exampleMovie)).log();
        StepVerifier.create(myMovie)
                .expectNextCount(2)
                .verifyComplete();
    }
}