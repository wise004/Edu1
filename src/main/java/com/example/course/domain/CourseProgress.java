package com.example.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A CourseProgress.
 */
@Entity
@Table(name = "course_progress")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "completed_items")
    private Integer completedItems;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Course course;

    @JsonIgnoreProperties(value = { "courseProgress" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "courseProgress")
    private Certificate certificate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseProgress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCompletedItems() {
        return this.completedItems;
    }

    public CourseProgress completedItems(Integer completedItems) {
        this.setCompletedItems(completedItems);
        return this;
    }

    public void setCompletedItems(Integer completedItems) {
        this.completedItems = completedItems;
    }

    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    public CourseProgress isCompleted(Boolean isCompleted) {
        this.setIsCompleted(isCompleted);
        return this;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public CourseProgress student(User user) {
        this.setStudent(user);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseProgress course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public void setCertificate(Certificate certificate) {
        if (this.certificate != null) {
            this.certificate.setCourseProgress(null);
        }
        if (certificate != null) {
            certificate.setCourseProgress(this);
        }
        this.certificate = certificate;
    }

    public CourseProgress certificate(Certificate certificate) {
        this.setCertificate(certificate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseProgress)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseProgress) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseProgress{" +
            "id=" + getId() +
            ", completedItems=" + getCompletedItems() +
            ", isCompleted='" + getIsCompleted() + "'" +
            "}";
    }
}
