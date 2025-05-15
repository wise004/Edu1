package com.example.course.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CourseItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CourseItem getCourseItemSample1() {
        return new CourseItem().id(1L).title("title1").content("content1").passingScore(1);
    }

    public static CourseItem getCourseItemSample2() {
        return new CourseItem().id(2L).title("title2").content("content2").passingScore(2);
    }

    public static CourseItem getCourseItemRandomSampleGenerator() {
        return new CourseItem()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .passingScore(intCount.incrementAndGet());
    }
}
