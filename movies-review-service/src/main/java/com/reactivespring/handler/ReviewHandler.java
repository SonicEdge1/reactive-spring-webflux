package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ReviewHandler {
    
    @Autowired
    Validator validator;

    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

//    public Mono<ServerResponse> addReview(Mono<Review> reviewMono) {
//        return reviewMono.flatMap(review -> reviewReactiveRepository.save(review))
//                    .flatMap(savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview));
//    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                        .doOnNext(this::validate)
                        .flatMap(reviewReactiveRepository::save)
                        .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        if(constraintViolations.size() > 0) {
            log.info("constraintViolations : {}", constraintViolations);
            var errorMessage = constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMessage);
        }


    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {
        var movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            return buildOkServerResponse(reviewReactiveRepository.getReviewsByMovieInfoId(movieInfoId.get()));
        }
        return buildOkServerResponse(reviewReactiveRepository.findAll());
    }

    public Mono<ServerResponse> getReviewByMovieId(String movieInfoId) {
        System.out.println("\nMovieID: " + movieInfoId);
        var allReviewsByMovieId = reviewReactiveRepository.findAll().filter(review -> review.getMovieInfoId().equals(movieInfoId));
        //if empty return 404? or just empty flux
        return buildOkServerResponse(allReviewsByMovieId);
    };

    private Mono<ServerResponse> buildOkServerResponse(Flux<Review> reviewFlux) {
        return ServerResponse.ok().body(reviewFlux, Review.class);
    }
    public Mono<ServerResponse> updateReviewById(String reviewId, Mono<Review> updatedReview) {

        var existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for Review ID: " + reviewId)));

//        validate(updatedReview.block());

        return existingReview.flatMap(review -> updatedReview
                .map(requestReview -> {
                    review.setMovieInfoId(requestReview.getMovieInfoId());
                    review.setComment(requestReview.getComment());
                    review.setRating(requestReview.getRating());
                    return review;
                })
                .flatMap(reviewReactiveRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
        );
    }

    public Mono<ServerResponse> deleteReviewById(String reviewId) {
//        return ServerResponse.ok().body(reviewReactiveRepository.deleteById(reviewId), Review.class);  //returns a mono void
        return reviewReactiveRepository.findById(reviewId)
                .flatMap(review -> reviewReactiveRepository.deleteById(reviewId)
                        .then(ServerResponse.noContent().build()));
    }
}
