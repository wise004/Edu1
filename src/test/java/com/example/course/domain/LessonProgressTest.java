package com.example.course.domain;

import static com.example.course.domain.CourseItemTestSamples.*;
import static com.example.course.domain.LessonProgressTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonProgress.class);
        LessonProgress lessonProgress1 = getLessonProgressSample1();
        LessonProgress lessonProgress2 = new LessonProgress();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);

        lessonProgress2.setId(lessonProgress1.getId());
        assertThat(lessonProgress1).isEqualTo(lessonProgress2);

        lessonProgress2 = getLessonProgressSample2();
        assertThat(lessonProgress1).isNotEqualTo(lessonProgress2);
    }

    @Test
    void courseItemTest() {
        LessonProgress lessonProgress = getLessonProgressRandomSampleGenerator();
        CourseItem courseItemBack = getCourseItemRandomSampleGenerator();

        lessonProgress.setCourseItem(courseItemBack);
        assertThat(lessonProgress.getCourseItem()).isEqualTo(courseItemBack);

        lessonProgress.courseItem(null);
        assertThat(lessonProgress.getCourseItem()).isNull();
    }
}
