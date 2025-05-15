package com.example.course.service.dto;

import java.util.List;

public class TestAttemptDTO {

    private Long courseItemId;
    private List<AnswerDTO> answers;

    public static class AnswerDTO {

        private Long questionId;
        private String selectedAnswer;

        // Getters and setters

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public String getSelectedAnswer() {
            return selectedAnswer;
        }

        public void setSelectedAnswer(String selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }
    }

    public Long getCourseItemId() {
        return courseItemId;
    }

    public void setCourseItemId(Long courseItemId) {
        this.courseItemId = courseItemId;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
    // Getters and setters
}
