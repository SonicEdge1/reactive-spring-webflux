package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    private final static String MOVIE_INFO_URL = "/v1/movieInfo";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoServiceMock;

    @Test
    void getAllMoviesInfo() {

        var movieInfoList = List.of(
                new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))
        );

        when(movieInfoServiceMock.getAllMovieInfo()).thenReturn(Flux.fromIterable(movieInfoList));

        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);

    }

    @Test
    void addMovieInfoTest() {

        var newMovieInfo = new MovieInfo(
                "mock",
                "Weird Science",
                1985,
                List.of("Kelly LeBrock", "Anthony Michael Hall", "Ilan Mitchell-Smith"),
                LocalDate.parse("1985-08-02")
        );

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(newMovieInfo));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertNotNull(savedMovieInfo.getMovieInfoId());
                    assertEquals("Weird Science", savedMovieInfo.getTitle());
                });
    }

    @Test
    void FailAddMovieInfoBlankTitleTest() {

        var newMovieInfo = new MovieInfo(
                "mock",
                null,
                1985,
                List.of("Kelly LeBrock", "Anthony Michael Hall", "Ilan Mitchell-Smith"),
                LocalDate.parse("1985-08-02")
        );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var errorMessage = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(errorMessage);
                    assertEquals("Movie Title Must Be Present", errorMessage);
                });
    }

    @Test
    void FailAddMovieInfoBadYearTest() {

        var newMovieInfo = new MovieInfo(
                "mock",
                "Weird Science",
                -1985,
                List.of("Kelly LeBrock", "Anthony Michael Hall", "Ilan Mitchell-Smith"),
                LocalDate.parse("1985-08-02")
        );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println("Resp[onse Body: " + responseBody);
                    var errorMessage = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(errorMessage);
                    assertEquals("Year must be a positive value", errorMessage);
                });
    }

    @Test
    void FailAddMovieInfoEmptyCastTest() {

        var newMovieInfo = new MovieInfo(
                "mock",
                "Weird Science",
                1985,
                List.of(""),
                LocalDate.parse("1985-08-02")
        );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println("Resp[onse Body: " + responseBody);
                    var errorMessage = stringEntityExchangeResult.getResponseBody();
                    assertNotNull(errorMessage);
                    assertEquals("At least one cast member must be present", errorMessage);
                });
    }

    @Test
    void getMovieInfoByIdTest() {

        String movieInfoId = "mib3";
        var queriedMovie = new MovieInfo(movieInfoId, "Men in Black 3",
                2012, List.of("Will Smith", "Tommy Lee Jones"), LocalDate.parse("2012-07-13"));


        when(movieInfoServiceMock.getMovieInfoById(isA(String.class))).thenReturn(Mono.just(queriedMovie));

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
    void failToGetMovieInfoByIdTest() {

        String movieInfoId = "nonya";

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
                "updatedMock",
                "You've Been Flashy Thinged",
                0,
                List.of("Unknown", "Unknown", "Unknown"),
                LocalDate.now()
        );

        var movieInfoId = "mib3";

        when(movieInfoServiceMock.updateMovieInfoById(isA(String.class), isA(MovieInfo.class))).thenReturn(Mono.just(updatedMovie));

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
        when(movieInfoServiceMock.deleteMovieInfoById(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);

    }

}