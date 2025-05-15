package com.example.course.domain;

import com.example.course.domain.enumeration.ContentType;
import com.example.course.domain.enumeration.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A CourseItem.
 */
@Entity
@Table(name = "course_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ContentType contentType;

    @Column(name = "content")
    private String content;

    @Column(name = "passing_score")
    private Integer passingScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public CourseItem title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public CourseItem itemType(ItemType itemType) {
        this.setItemType(itemType);
        return this;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public CourseItem contentType(ContentType contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return this.content;
    }

    public CourseItem content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPassingScore() {
        return this.passingScore;
    }

    public CourseItem passingScore(Integer passingScore) {
        this.setPassingScore(passingScore);
        return this;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseItem course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseItem)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseItem{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", itemType='" + getItemType() + "'" +
            ", contentType='" + getContentType() + "'" +
            ", content='" + getContent() + "'" +
            ", passingScore=" + getPassingScore() +
            "}";
    }
}
