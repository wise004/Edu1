package com.example.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A LessonProgress.
 */
@Entity
@Table(name = "lesson_progress")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "viewed", nullable = false)
    private Boolean viewed;

    @Column(name = "viewed_date")
    private Instant viewedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private CourseItem courseItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LessonProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getViewed() {
        return this.viewed;
    }

    public LessonProgress viewed(Boolean viewed) {
        this.setViewed(viewed);
        return this;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public Instant getViewedDate() {
        return this.viewedDate;
    }

    public LessonProgress viewedDate(Instant viewedDate) {
        this.setViewedDate(viewedDate);
        return this;
    }

    public void setViewedDate(Instant viewedDate) {
        this.viewedDate = viewedDate;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public LessonProgress student(User user) {
        this.setStudent(user);
        return this;
    }

    public CourseItem getCourseItem() {
        return this.courseItem;
    }

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public LessonProgress courseItem(CourseItem courseItem) {
        this.setCourseItem(courseItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((LessonProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonProgress{" +
            "id=" + getId() +
            ", viewed='" + getViewed() + "'" +
            ", viewedDate='" + getViewedDate() + "'" +
            "}";
    }
}
