package com.example.course.domain;

import static com.example.course.domain.CertificateTestSamples.*;
import static com.example.course.domain.CourseProgressTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.course.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Certificate.class);
        Certificate certificate1 = getCertificateSample1();
        Certificate certificate2 = new Certificate();
        assertThat(certificate1).isNotEqualTo(certificate2);

        certificate2.setId(certificate1.getId());
        assertThat(certificate1).isEqualTo(certificate2);

        certificate2 = getCertificateSample2();
        assertThat(certificate1).isNotEqualTo(certificate2);
    }

    @Test
    void courseProgressTest() {
        Certificate certificate = getCertificateRandomSampleGenerator();
        CourseProgress courseProgressBack = getCourseProgressRandomSampleGenerator();

        certificate.setCourseProgress(courseProgressBack);
        assertThat(certificate.getCourseProgress()).isEqualTo(courseProgressBack);

        certificate.courseProgress(null);
        assertThat(certificate.getCourseProgress()).isNull();
    }
}
