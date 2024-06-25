package com.example.demo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

//https://medium.com/javarevisited/unlocking-precision-metrics-in-spring-boot-with-micrometer-a-comprehensive-guide-6d72d6eaaf00#:~:text=Micrometer%20in%20Spring%20Boot%20is,tool%20in%20your%20developer%20toolkit.

@RestController
@RequestMapping("/api")
public class BookController {

    private final AtomicInteger myGauge = new AtomicInteger();
    List<String> books = new ArrayList<>();
    final MeterRegistry meterRegistry;

    final Counter getBooksCounter;
    final Counter addBookCounter;
    final Timer getBookTimer;

    public BookController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        books.add("Book : " + UUID.randomUUID());
        books.add("Book : " + UUID.randomUUID());
        books.add("Book : " + UUID.randomUUID());

        this.getBooksCounter = Counter.builder("get.book.counter")
                .description("Counts number of GET : /api/book.")
                .tags("region", "india")
                .register(meterRegistry);
        this.addBookCounter = Counter.builder("add.book.counter")
                .description("Counts number of POST : /api/book.")
                .tags("region", "india")
                .register(meterRegistry);
        this.getBookTimer = Timer.builder("get.book.latency")
                .description("Calculate the latency of GET : /api/book.")
                .tags("region", "india")
                .register(meterRegistry);
        Gauge.builder("available.books", myGauge, AtomicInteger::get)
                .description("Total number of available books.")
                .tags("region", "india")
                .register(meterRegistry);
    }

    @PostMapping("/book")
    public List<String> addBook() {
        addBookCounter.increment();
        books.add("Book : " + UUID.randomUUID());
        myGauge.set(books.size());
        return books;
    }

    @GetMapping("/book")
    public List<String> getBooks() {
        getBooksCounter.increment();
        getBookTimer.record(()-> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return books;
    }
}
