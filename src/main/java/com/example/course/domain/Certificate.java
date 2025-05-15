package com.example.course.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private Instant issueDate;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @JsonIgnoreProperties(value = { "student", "course", "certificate" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private CourseProgress courseProgress;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Certificate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getIssueDate() {
        return this.issueDate;
    }

    public Certificate issueDate(Instant issueDate) {
        this.setIssueDate(issueDate);
        return this;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
    }

    public String getCertificateUrl() {
        return this.certificateUrl;
    }

    public Certificate certificateUrl(String certificateUrl) {
        this.setCertificateUrl(certificateUrl);
        return this;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public CourseProgress getCourseProgress() {
        return this.courseProgress;
    }

    public void setCourseProgress(CourseProgress courseProgress) {
        this.courseProgress = courseProgress;
    }

    public Certificate courseProgress(CourseProgress courseProgress) {
        this.setCourseProgress(courseProgress);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        return getId() != null && getId().equals(((Certificate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + getId() +
            ", issueDate='" + getIssueDate() + "'" +
            ", certificateUrl='" + getCertificateUrl() + "'" +
            "}";
    }
}
