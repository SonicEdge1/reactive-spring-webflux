package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    List<String> allNames = List.of("Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie", "Joe");

    @Test
    void namesFluxUpperCase() {
        var fluxUpperCaseNames = fluxAndMonoGeneratorService.namesFluxUpperCase();

        StepVerifier.create(fluxUpperCaseNames)
                .expectNext("JOE", "JENNIFER", "ALEXA", "WYATT", "KYLE", "SADIE")
                .verifyComplete();
    }

    @Test
    void namesFlux() {
        var fluxNames = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(fluxNames)
//                .expectNextSequence(allNames) //would expect an entire list to come through so won't work with 1 item
//                .expectNextCount(6)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle")
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFluxStreamsAreImmutable() {
        var fluxNames = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(fluxNames)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie")
                .verifyComplete();
    }

    @Test
    void namesMono() {
        var monoNames = fluxAndMonoGeneratorService.namesMono();

        StepVerifier.create(monoNames)
                .expectNext("Zeus")
                .verifyComplete();
    }
}