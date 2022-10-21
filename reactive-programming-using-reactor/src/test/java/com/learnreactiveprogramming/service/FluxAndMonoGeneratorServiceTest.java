package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    List<String> allNames = List.of("Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie", "Joe");

    @Test
    void testJustPrint() {
        var fluxUpperCaseNames = fluxAndMonoGeneratorService.namesFluxUpperCase();

        fluxAndMonoGeneratorService.namesFlux().subscribe(n -> {
            System.out.println(n);
        });
    }

    @Test
    void testNamesFluxUpperCase() {
        var fluxUpperCaseNames = fluxAndMonoGeneratorService.namesFluxUpperCase();

        StepVerifier.create(fluxUpperCaseNames)
                .expectNext("JOE", "JENNIFER", "ALEXA", "WYATT", "KYLE", "SADIE")
                .verifyComplete();
    }

    @Test
    void testNamesFlux() {
        var fluxNames = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(fluxNames)
//                .expectNextSequence(allNames) //would expect an entire list to come through so won't work with 1 item
//                .expectNextCount(6)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle")
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testNamesFluxStreamsAreImmutable() {
        var fluxNames = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(fluxNames)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie")
                .verifyComplete();
    }

    @Test
    void testFilterAndMapNames() {
        var fluxNames = fluxAndMonoGeneratorService.filterAndMapNames(4);

        StepVerifier.create(fluxNames)
                .expectNext("8-Jennifer", "5-Alexa", "5-Wyatt", "5-Sadie")
                .verifyComplete();
    }

    @Test
    void testCharsOfNames() {
        var fluxNames = fluxAndMonoGeneratorService.charsOfNames(4);

        StepVerifier.create(fluxNames)
                .expectNext("J", "e", "n", "n", "i", "f", "e", "r", "A", "l", "e", "x", "a")
                .verifyComplete();
    }

    @org.testng.annotations.Test
    void testTransformExample() {
        var fluxNames = fluxAndMonoGeneratorService.transformExample(5);

        StepVerifier.create(fluxNames)
                .expectNext("J", "E", "N", "N", "I", "F", "E", "R")
                .verifyComplete();
    }

    @Test
    void testNamesMono_map_filter() {
        var monoNames = fluxAndMonoGeneratorService.namesMono_map_filter(5);

        StepVerifier.create(monoNames)
                .expectNext("ZEUS") //works for stringLength 5 and above
                .verifyComplete();
    }


    @Test
    void testNamesMono() {
        var monoNames = fluxAndMonoGeneratorService.namesMono();

        StepVerifier.create(monoNames)
                .expectNext("Zeus")
                .verifyComplete();
    }

    @Test
    void testNamesMono_flatMap() {
        var stringLength = 10;
        var monoNames = fluxAndMonoGeneratorService.namesMono_flatMap(stringLength);

        StepVerifier.create(monoNames)
                .expectNext(List.of("Z", "E", "U", "S"))
                .verifyComplete();
    }

    @Test
    void testNamesMono_flatMapMany() {
        var stringLength = 10;
        var monoNames = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        StepVerifier.create(monoNames)
                .expectNext("Z", "E", "U", "S")
                .verifyComplete();
    }

    @Test
    void testDefaultIfEmptyExample() {
        var stringLength = 10;
        var monoNames = fluxAndMonoGeneratorService.defaultIfEmptyExample();

        StepVerifier.create(monoNames)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void testSwitchIfEmptyExample() {
        var stringLength = 10;
        var monoNames = fluxAndMonoGeneratorService.switchIfEmptyExample();

        StepVerifier.create(monoNames)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void testConcatFluxStreams() {
        var combinedStreams = fluxAndMonoGeneratorService.concatFluxStreams();

        StepVerifier.create(combinedStreams)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie", "No More", "End of Line")
                .verifyComplete();
    }

    @Test
    void testConcatFluxAndMono() {
        var combinedStreams = fluxAndMonoGeneratorService.concatFluxAndMono();
        StepVerifier.create(combinedStreams)
                .expectNext("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie", "Zeus")
                .verifyComplete();
    }

    @Test
    void testConcatFluxAndMono2() {
        var combinedStreams = fluxAndMonoGeneratorService.concatFluxAndMono2();
        StepVerifier.create(combinedStreams)
                .expectNext("Zeus", "Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie")
                .verifyComplete();
    }

    @Test
    void testMergeExample() {
        var merged = fluxAndMonoGeneratorService.mergeExample();
        StepVerifier.create(merged)
                .expectNextCount(9)
                .verifyComplete();
    }
    @Test
    void testMergeExample2() {
        var merged = fluxAndMonoGeneratorService.mergeExample2();
        StepVerifier.create(merged)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void testMergeWithExample() {
        var merged = fluxAndMonoGeneratorService.mergeWithExample();
        StepVerifier.create(merged)
                .expectNext("D", "E", "F", "A", "B", "C")
                .verifyComplete();
    }
    @Test
    void testMergeSequentialExample() {
        var merged = fluxAndMonoGeneratorService.mergeSequentialExample();
        StepVerifier.create(merged)
                .expectNext("D", "E", "F", "A", "B", "C", "G", "H", "I")
                .verifyComplete();
    }
    @Test
    void testZipExample() {
        var merged = fluxAndMonoGeneratorService.zipExample();
        StepVerifier.create(merged)
                .expectNext("DG", "EH", "FI")
                .verifyComplete();
    }
    @Test
    void testZipExampleWtuple() {
        var merged = fluxAndMonoGeneratorService.zipExampleWtuple();
        StepVerifier.create(merged)
                .expectNext("DG14", "EH25", "FI36")
                .verifyComplete();
    }

    @Test
    void testZipWithExample() {
        var merged = fluxAndMonoGeneratorService.zipWithExample();
        StepVerifier.create(merged)
                .expectNext("DG", "EH", "FI")
                .verifyComplete();
    }

    @Test
    void testZipWithMonoExample() {
        var merged = fluxAndMonoGeneratorService.zipWithMonoExample();
        StepVerifier.create(merged)
                .expectNext("Zeus~~~")
                .verifyComplete();
    }

}