package com.example.course.domain;

import static com.example.course.domain.CourseItemTestSamples.*;
import static com.example.course.domain.TestAttemptTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TestAttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TestAttempt.class);
        TestAttempt testAttempt1 = getTestAttemptSample1();
        TestAttempt testAttempt2 = new TestAttempt();
        assertThat(testAttempt1).isNotEqualTo(testAttempt2);

        testAttempt2.setId(testAttempt1.getId());
        assertThat(testAttempt1).isEqualTo(testAttempt2);

        testAttempt2 = getTestAttemptSample2();
        assertThat(testAttempt1).isNotEqualTo(testAttempt2);
    }

    @Test
    void courseItemTest() {
        TestAttempt testAttempt = getTestAttemptRandomSampleGenerator();
        CourseItem courseItemBack = getCourseItemRandomSampleGenerator();

        testAttempt.setCourseItem(courseItemBack);
        assertThat(testAttempt.getCourseItem()).isEqualTo(courseItemBack);

        testAttempt.courseItem(null);
        assertThat(testAttempt.getCourseItem()).isNull();
    }
}
