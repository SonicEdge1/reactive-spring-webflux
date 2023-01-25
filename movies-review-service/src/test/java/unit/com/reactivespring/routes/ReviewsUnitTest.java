package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {
    @MockBean
    ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static final String MOVIE_REVIEW_URL = "/v1/reviews";

    @Test
    void testShouldAddReview() {
        var review = new Review("test_SAR", "1", "I Cried", 1.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));

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
                    assertEquals("test_SAR", savedReview.getReviewId());
                    assertEquals("I Cried", savedReview.getComment());
                    assertEquals(1.0, savedReview.getRating());
                });
    }

    @Test
    void testFailToAddReview_validate_movieInfoId() {
        var review = new Review(null, null, "I Cried", 1.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));

        webTestClient
                .post()
                .uri(MOVIE_REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.movieInfoId : must not be null");
    }

    @Test
    void testFailToAddReview_validate_rating() {
        var review = new Review(null, "abc123", "I Cried", -.09);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(review));

        webTestClient
                .post()
                .uri(MOVIE_REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("review.rating.negative : please pass a non-negative value");
    }

    @Test
    void testShouldGetAllReviews() {

        var reviewsList = List.of(
                new Review(null, "2", "Excellent Movie", 8.0),
                new Review("test_SGAR1", "1", "Awesome Movie", 9.0),
                new Review("test_SGAR2", "1", "Great Movie", 9.0));
        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewsList));

        webTestClient
                .get()
                .uri(MOVIE_REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(reviewsList.size(), reviews.size());
                    assertEquals(reviewsList, reviews);
                });
    }

    @Test
    void testShouldUpdateReviewByReviewId() {
        var reviewId = "abc123";
        var reviewUpdate = new Review(reviewId, "4", "Four Thumbs Up!", 4.4);
        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.just(new Review(reviewId, "3", "Excellent Movie", 7.0)));
        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(reviewUpdate));


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
    void testShouldDeleteReviewById() {
        var reviewId = "abc123";
        when(reviewReactiveRepository.findById(anyString()))
                .thenReturn(Mono.just(new Review(reviewId, "3", "Excellent Movie", 7.0)));
        when(reviewReactiveRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIE_REVIEW_URL +  "/{Id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

}
