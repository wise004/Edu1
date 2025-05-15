package com.example.course.domain;

import static com.example.course.domain.CourseItemTestSamples.*;
import static com.example.course.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseItem.class);
        CourseItem courseItem1 = getCourseItemSample1();
        CourseItem courseItem2 = new CourseItem();
        assertThat(courseItem1).isNotEqualTo(courseItem2);

        courseItem2.setId(courseItem1.getId());
        assertThat(courseItem1).isEqualTo(courseItem2);

        courseItem2 = getCourseItemSample2();
        assertThat(courseItem1).isNotEqualTo(courseItem2);
    }

    @Test
    void courseTest() {
        CourseItem courseItem = getCourseItemRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseItem.setCourse(courseBack);
        assertThat(courseItem.getCourse()).isEqualTo(courseBack);

        courseItem.course(null);
        assertThat(courseItem.getCourse()).isNull();
    }
}
