package com.example.oms.config;

import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TracingSanityCheck {

    private final Tracer tracer;

    public TracingSanityCheck(Tracer tracer) {
        this.tracer = tracer;
    }

    @PostConstruct
    public void testSpan() {
        tracer.nextSpan().name("sanity-check-span").start().end();
        System.out.println(">>> Tracer bean is ACTIVE");
    }
}

