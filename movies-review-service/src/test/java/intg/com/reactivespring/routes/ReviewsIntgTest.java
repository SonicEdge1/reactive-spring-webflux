package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    ReviewHandler reviewHandler;

    @Autowired
    WebTestClient webTestClient;

    private static final String MOVIE_REVIEW_URL = "/v1/reviews";
    private static final int INITIAL_REVIEWS_COUNT = 4;

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
                new Review(null, "1", "Awesome Movie", 9.0),
                new Review(null, "1", "Great Movie", 9.0),
                new Review(null, "2", "Excellent Movie", 8.0),
                new Review("abc123", "3", "Excellent Movie", 7.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    void testShouldAddReview() {
        var review = new Review(null, "1", "I Cried", 1.0);

        webTestClient
                .post()
                .uri(MOVIE_REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(savedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals("I Cried", savedReview.getComment());
                    assertEquals(1.0, savedReview.getRating());
                });
    }

    @Test
    void testShouldGetAllReviews() {
        getsAllReviews(INITIAL_REVIEWS_COUNT);
    }

    private void getsAllReviews(int reviewsListSize) {
        webTestClient
                    .get()
                    .uri(MOVIE_REVIEW_URL)
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBodyList(Review.class)
                    .hasSize(reviewsListSize);
    }

//    @Test
//    void testShouldGetReviewByMovieId() {
//        var movieId = "2";
//
//        webTestClient
//                .get()
//                .uri(MOVIE_REVIEW_URL +  "/{movieId}", movieId)
//                .exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBodyList(Review.class)
//                .hasSize(1)
//                .consumeWith(reviewEntityExchangeResult -> {
//                    var queriedReview = reviewEntityExchangeResult.getResponseBody();
//                    assertNotNull(queriedReview);
//                    assertTrue(!queriedReview.isEmpty());
//                    assertNotNull(queriedReview.get(0).getReviewId());
//                    assertEquals("Excellent Movie", queriedReview.get(0).getComment());
//                    assertEquals(8.0, queriedReview.get(0).getRating());
//                });
//    }

    @Test
    void testShouldGetReviewByMovieId() {
        var movieId = "2";

        webTestClient
                .get()
                .uri(MOVIE_REVIEW_URL + "?movieInfoId=" + movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(1)
                .consumeWith(reviewEntityExchangeResult -> {
                    var queriedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(queriedReview);
                    assertTrue(!queriedReview.isEmpty());
                    assertNotNull(queriedReview.get(0).getReviewId());
                    assertEquals("Excellent Movie", queriedReview.get(0).getComment());
                    assertEquals(8.0, queriedReview.get(0).getRating());
                });
    }

    @Test
    void testShouldUpdateReviewByReviewId() {
        var reviewId = "abc123";
        var reviewUpdate = new Review(null, "4", "Four Thumbs Up!", 4.4);
        webTestClient
                .put()
                .uri(MOVIE_REVIEW_URL +  "/{Id}", reviewId)
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotNull(updatedReview);
                    assertEquals("Four Thumbs Up!", updatedReview.getComment());
                    assertEquals(4.4, updatedReview.getRating());
                });
    }

    @Test
    void testFailToUpdateReviewByReviewId_validate_movieInfoId() {
        var reviewId = "4";
        var reviewUpdate = new Review(null, "400", "Four Thumbs Up!", 4.4);
        webTestClient
                .put()
                .uri(MOVIE_REVIEW_URL +  "/{Id}", reviewId)
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .isEqualTo("Review not found for Review ID: " + reviewId);

    }

    @Test
    void testShouldDeleteReviewById() {
        var reviewId = "abc123";

        getsAllReviews(INITIAL_REVIEWS_COUNT);
        webTestClient
                .delete()
                .uri(MOVIE_REVIEW_URL +  "/{Id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
        getsAllReviews(INITIAL_REVIEWS_COUNT - 1);
    }

}
