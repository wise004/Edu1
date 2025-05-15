package com.example.course.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseProgressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CourseProgress getCourseProgressSample1() {
        return new CourseProgress().id(1L).completedItems(1);
    }

    public static CourseProgress getCourseProgressSample2() {
        return new CourseProgress().id(2L).completedItems(2);
    }

    public static CourseProgress getCourseProgressRandomSampleGenerator() {
        return new CourseProgress().id(longCount.incrementAndGet()).completedItems(intCount.incrementAndGet());
    }
}
