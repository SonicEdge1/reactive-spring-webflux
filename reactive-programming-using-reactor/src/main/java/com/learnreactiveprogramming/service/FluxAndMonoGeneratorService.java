package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.time.Duration;
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


    ////////////////// COMBINING PUBLISHERS //////////////////////
    /*
    Ordering of items when concatenating is important.
    The concat will happen in sequential order according to
    the order they are placed in the method parameters list.
     */
    public Flux<String> concatFluxStreams() {
        return Flux.concat(namesFlux(), Flux.just("No More"), Flux.just("End of Line"));
    }

    public Flux<String> concatFluxAndMono() {
        return namesFlux().concatWith(namesMono());
    }

    //Ordering of items when concatenating is important
    public Flux<String> concatFluxAndMono2() {
        return namesMono().concatWith(namesFlux());
    }

    Flux<String> abc = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(50));
    Flux<String> def = Flux.just("D", "E", "F");
    Flux<String> ghi = Flux.just("G", "H", "I");
    Flux<String> flux123 = Flux.just("1", "2", "3");
    Flux<String> flux234 = Flux.just("4", "5", "6");

    //merge and mergeWith will subscribe to each flux concurrently, not sequentially.
    public Flux<String> mergeExample() {
        return Flux.merge(abc, def, ghi).log();
    }
    public Flux<String> mergeExample2() {
        var abc = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var def = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));
        return abc.mergeWith(def).log();
    }

    //DEF will occupy the new flux before ABC because of the delay
    public Flux<String> mergeWithExample() {
        return abc.mergeWith(def).log();
    }

    public Flux<String> mergeSequentialExample() {
        return Flux.mergeSequential(def, abc, ghi);
    }

    /*
    Zip functions can merge 2-8 publishers
    They will wait for all publishers to emit 1 element before continuing on to the next.
    The elements from each publisher will be combined into a new element.
     */
    public Flux<String> zipExample() {
        return Flux.zip(def, ghi, (first, second) -> first+second);
    }
    public Flux<String> zipExampleWtuple() {
        return Flux.zip(def, ghi, flux123, flux234).map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4()).log();
    }
    public Flux<String> zipWithExample() {
        return def.zipWith(ghi, (first, second) -> first+second);
    }
    public Mono<String> zipWithMonoExample() {
        return namesMono().zipWith(Mono.just("~~~")).map(t1 -> t1.getT1() + t1.getT2());
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
