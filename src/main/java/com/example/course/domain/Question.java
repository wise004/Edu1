package com.example.course.domain;

import com.example.course.domain.enumeration.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private QuestionType type;

    @NotNull
    @Column(name = "options", nullable = false)
    private String options;

    @NotNull
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course" }, allowSetters = true)
    private CourseItem courseItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public Question text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public QuestionType getType() {
        return this.type;
    }

    public Question type(QuestionType type) {
        this.setType(type);
        return this;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getOptions() {
        return this.options;
    }

    public Question options(String options) {
        this.setOptions(options);
        return this;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public CourseItem getCourseItem() {
        return this.courseItem;
    }

    public void setCourseItem(CourseItem courseItem) {
        this.courseItem = courseItem;
    }

    public Question courseItem(CourseItem courseItem) {
        this.setCourseItem(courseItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", type='" + getType() + "'" +
            ", options='" + getOptions() + "'" +
            "}";
    }
}
