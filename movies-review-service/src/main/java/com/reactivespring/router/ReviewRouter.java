package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


//Functional Web implementation of the controller
@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {

        return route()
                .nest(path("/v1/reviews"), builder -> {
                builder.POST("", request -> reviewHandler.addReview(request))
                        .PUT("/{reviewId}", request -> reviewHandler.updateReviewById(request.pathVariable("reviewId"), request.bodyToMono(Review.class)))
                        .DELETE("/{reviewId}", request -> reviewHandler.deleteReviewById(request.pathVariable("reviewId")))
                       .GET("", request -> reviewHandler.getAllReviews(request))
//                       .GET("/{movieInfoId}", request -> reviewHandler.getReviewByMovieId(request.pathVariable("movieInfoId")))  // a path variable implementation is more suited for a get by reviewId
                ;

                })
                .GET("v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello World!")))
                .build();
    }
}
