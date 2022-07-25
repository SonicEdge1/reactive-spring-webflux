package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> family = Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"));

    public Flux<String> namesFlux() {
        return Flux.fromIterable(family.toIterable())
                .log();
    }
    public Flux<String> namesFluxUpperCase() {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"))
                .map(String::toUpperCase);
//                .map(s -> s.toUpperCase()) //same functionality as line above
//                .log(); //.log shows all subscribe, onNext, and onComplete events in console
    }

    public Flux<String> filterAndMapNames(int stringLength) {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"))
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s);
    }

    public Flux<String> namesFluxStreamsAreImmutable() {
        var reactiveStream = Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa", "Wyatt", "Kyle", "Sadie"));
        reactiveStream.map(String::toUpperCase);
        return reactiveStream;
//                .log(); //.log shows all subscribe, onNext, and onComplete events in console
    }

    private Function<Flux<String>,Flux<String>> function = string -> string.map(String::toUpperCase).filter(s -> s.length() > 5).map(String::toUpperCase);

    public Flux<String> transformExample(int stringLength) {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa"))
                .transform(function)  //transform takes a Function object
                .flatMap(s -> getChars(s));
    }

    private Function<Flux<String>,Flux<String>> function2 = string -> string.map(String::toUpperCase).filter(s -> s.length() > 10);
    private Function<Flux<String>,Flux<String>> function3 = string -> string.map(String::toUpperCase).filter(s -> s.length() > 1);

    public Flux<String> defaultIfEmptyExample() {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa"))
                .transform(function2)  //transform takes a Function object
                .defaultIfEmpty("default").transform(function3)
                .flatMap(s -> getChars(s));
    }

    public Flux<String> switchIfEmptyExample() {
        var defaultFlux = Flux.just("default");

        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa"))
                .transform(function2)  //transform takes a Function object
                .switchIfEmpty(defaultFlux).transform(function3)
                .flatMap(s -> getChars(s));
    }

    public Flux<String> charsOfNames(int stringLength) {
        return Flux.fromIterable(List.of("Joe", "Jennifer", "Alexa"))
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> getChars(s));
    }

    private Flux<String> getChars(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Mono<String> namesMono() {
        return Mono.just("Zeus");
    }

    public Mono<String> namesMono_map_filter(int stringLength)
    {
        var name = Mono.just("Zeus");
        return name.map(String::toUpperCase)
                .filter(s -> s.length() < stringLength).log();
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength)
    {
        var name = Mono.just("Zeus");
        return name.map(String::toUpperCase)
                .filter(s -> s.length() < stringLength)
                .flatMap(this::getCharsMono).log();
    }

    public Flux<String> namesMono_flatMapMany(int stringLength)
    {
        var name = Mono.just("Zeus");
        return name.map(String::toUpperCase)
                .filter(s -> s.length() < stringLength)
                .flatMapMany(this::getChars).log();
    }

    private Mono<List<String>> getCharsMono(String name) {
        var charArray = name.split("");
        var list = List.of(charArray);
        return Mono.just(list);
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
