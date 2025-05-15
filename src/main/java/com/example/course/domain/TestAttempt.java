package com.example.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A TestAttempt.
 */
@Entity
@Table(name = "test_attempt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TestAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "score")
    private Integer score;

    @Column(name = "passed")
    private Boolean passed;

    @Column(name = "attempt_date")
    private Instant attemptDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private CourseItem courseItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TestAttempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return this.score;
    }

    public TestAttempt score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return this.passed;
    }

    public TestAttempt passed(Boolean passed) {
        this.setPassed(passed);
        return this;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Instant getAttemptDate() {
        return this.attemptDate;
    }

    public TestAttempt attemptDate(Instant attemptDate) {
        this.setAttemptDate(attemptDate);
        return this;
    }

    public void setAttemptDate(Instant attemptDate) {
        this.attemptDate = attemptDate;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public TestAttempt student(User user) {
        this.setStudent(user);
        return this;
    }

    public CourseItem getCourseItem() {
        return this.courseItem;
    }

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public TestAttempt courseItem(CourseItem courseItem) {
        this.setCourseItem(courseItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestAttempt)) {
            return false;
        }
        return getId() != null && getId().equals(((TestAttempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TestAttempt{" +
            "id=" + getId() +
            ", score=" + getScore() +
            ", passed='" + getPassed() + "'" +
            ", attemptDate='" + getAttemptDate() + "'" +
            "}";
    }
}
