package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerIntegrationTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    private final static String MOVIE_INFO_URL = "/v1/movieInfo";

    @BeforeEach
    void setUp() {
        var movieInfoList = List.of(
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")),
                new MovieInfo("mib3", "Men in Black 3",
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
    void addMovieInfoTest() {

        MovieInfo newMovie = new MovieInfo(
                null,
                "Weird Science",
                1985,
                List.of("Kelly LeBrock", "Anthony Michael Hall", "Ilan Mitchell-Smith"),
                LocalDate.parse("1985-08-02")
        );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(newMovie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("Weird Science", movieInfo.getTitle());
                });
   }

    @Test
    void getMovieInfoByIdTest() {

        var movieInfoId = "mib3";

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfo != null;
                    assert movieInfo.getMovieInfoId() != null;
                    assert movieInfo.getTitle().equals("Men in Black 3");
                });
   }

    @Test
    void getMovieInfoByIdTest2() {

        var movieInfoId = "mib3";

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Men in Black 3")
                .jsonPath("$.year").isEqualTo(2012);
    }

    //SHOULD BE 404!!!
    @Test
    void shouldFailToGetMovieInfoByIdTest() {

        var movieInfoId = "none";

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void shouldUpdateMovieInfoByIdTest() {
        MovieInfo updatedMovie = new MovieInfo(
                null,
                "You've Been Flashy Thinged",
                0,
                List.of("Unknown", "Unknown", "Unknown"),
                LocalDate.now()
        );

        var movieInfoId = "mib3";
        webTestClient.put()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertNotNull(movieInfo.getMovieInfoId());
                    assertEquals("You've Been Flashy Thinged", movieInfo.getTitle());
                    assertEquals(0, movieInfo.getYear());
                    assertEquals(LocalDate.now(), movieInfo.getRelease_date());
                    assertEquals(List.of("Unknown", "Unknown", "Unknown"), movieInfo.getCast());
                });
    }


    @Test
    void deleteMovieInfoByIdTest() {

        var movieInfoId = "mib3";

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(4);
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent().expectBody(Void.class);
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }


    @Test
    void getAllMovieInfoTest() {

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(4);
   }

    @Test
    void getMovieInfoByYearTest() {

        Integer year = 2012;
        var movieInfoFlux = movieInfoRepository.findByYear(year);

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllMovieInfoByYearTestByPathVariable() {

        Integer year = 2012;

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/year/{year}", year)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void getAllMovieInfoByYearTestByRequestParameter() {

        Integer year = 2012;

        webTestClient
                .get()
                .uri("/v1/movieInfo?year=" + year)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }

    @Test
    void getAllMovieInfoByYearTestByRequestParameter2() {

        Integer year = 2012;
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                        .queryParam("year", year)
                        .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2);
    }


    @Test
    void getAllMovieInfoByPartialTitleTestByRequestParameter2() {

        String partialTitle =  "Dark";
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                .queryParam("partialTitle", partialTitle)
                .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(2)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoList = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfoList);
                    assertTrue(!movieInfoList.isEmpty());
                    assertEquals("The Dark Knight", movieInfoList.get(0).getTitle());
                    assertEquals(2008, movieInfoList.get(0).getYear());
                    assertEquals(LocalDate.parse("2008-07-18"), movieInfoList.get(0).getRelease_date());
                    assertTrue(movieInfoList.get(0).getCast().contains("HeathLedger"));
                });
    }


    @Test
    void getMovieInfoByTitleTest() {

        String title = "Batman Begins";
        var movieInfoFlux = movieInfoRepository.findByTitle(title);

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllMovieInfoByTitleTestByRequestParameter2() {

        String title =  "The Dark Knight";
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                .queryParam("title", title)
                .buildAndExpand().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoList = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfoList);
                    assertTrue(!movieInfoList.isEmpty());
                    assertEquals("The Dark Knight", movieInfoList.get(0).getTitle());
                    assertEquals(2008, movieInfoList.get(0).getYear());
                    assertEquals(LocalDate.parse("2008-07-18"), movieInfoList.get(0).getRelease_date());
                    assertTrue(movieInfoList.get(0).getCast().contains("HeathLedger"));
                });
    }
}