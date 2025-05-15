package com.example.course.domain;

import static com.example.course.domain.CertificateTestSamples.*;
import static com.example.course.domain.CourseProgressTestSamples.*;
import static com.example.course.domain.CourseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CourseProgressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseProgress.class);
        CourseProgress courseProgress1 = getCourseProgressSample1();
        CourseProgress courseProgress2 = new CourseProgress();
        assertThat(courseProgress1).isNotEqualTo(courseProgress2);

        courseProgress2.setId(courseProgress1.getId());
        assertThat(courseProgress1).isEqualTo(courseProgress2);

        courseProgress2 = getCourseProgressSample2();
        assertThat(courseProgress1).isNotEqualTo(courseProgress2);
    }

    @Test
    void courseTest() {
        CourseProgress courseProgress = getCourseProgressRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        courseProgress.setCourse(courseBack);
        assertThat(courseProgress.getCourse()).isEqualTo(courseBack);

        courseProgress.course(null);
        assertThat(courseProgress.getCourse()).isNull();
    }

    @Test
    void certificateTest() {
        CourseProgress courseProgress = getCourseProgressRandomSampleGenerator();
        Certificate certificateBack = getCertificateRandomSampleGenerator();

        courseProgress.setCertificate(certificateBack);
        assertThat(courseProgress.getCertificate()).isEqualTo(certificateBack);
        assertThat(certificateBack.getCourseProgress()).isEqualTo(courseProgress);

        courseProgress.certificate(null);
        assertThat(courseProgress.getCertificate()).isNull();
        assertThat(certificateBack.getCourseProgress()).isNull();
    }
}
