package com.example.course.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestAttemptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TestAttempt getTestAttemptSample1() {
        return new TestAttempt().id(1L).score(1);
    }

    public static TestAttempt getTestAttemptSample2() {
        return new TestAttempt().id(2L).score(2);
    }

    public static TestAttempt getTestAttemptRandomSampleGenerator() {
        return new TestAttempt().id(longCount.incrementAndGet()).score(intCount.incrementAndGet());
    }
}
