package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public class FluxAndMonoGeneratorService {

    Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"))
                .log();
    }
    Flux<String> namesFluxUpperCase() {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"))
                .map(String::toUpperCase);
//                .map(s -> s.toUpperCase()) //same functionality as line above
//                .log(); //.log shows all subscribe, onNext, and onComplete events in console
    }

    Flux<String> namesFluxStreamsAreImmutable() {
        var reactiveStream = Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"));
        reactiveStream.map(String::toUpperCase);
        return reactiveStream;
//                .log(); //.log shows all subscribe, onNext, and onComplete events in console
    }

    Mono<String> namesMono() {
        return Mono.just("Zeus");
    }
    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        var famNames = fluxAndMonoGeneratorService.namesFlux();
        System.out.println("Family Names: ");
        famNames.subscribe(name -> {
            System.out.println(name);
        });
        fluxAndMonoGeneratorService.namesMono().subscribe(name -> {
            System.out.println("the Mono name is: " + name);
        });

    }
}
