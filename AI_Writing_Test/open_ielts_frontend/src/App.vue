<template>
  <div class="submit-page">
    <!-- Navigation Bar (Optional) -->
    <nav class="navbar">
      <h1>IELTS AI</h1>
      <div>
        <router-link to="/submit" class="nav-link">Submit Writing</router-link>
        <!-- <router-link to="/history" class="nav-link">All Submissions</router-link> -->
      </div>
    </nav>

    <!-- Main Container (centers content) -->
    <div class="submit-container">
      <!-- White Card -->
      <div class="card">
        <h2>Submit Your Writing</h2>

        <!-- Error Message (fade transition) -->
        <transition name="fade">
          <div v-if="error" class="error-message">
            {{ error }}
          </div>
        </transition>

        <!-- Submission Form -->
        <form @submit.prevent="submitWriting">
          <!-- Display the randomly chosen question (read-only) -->
          <div class="form-group">
            <label>Task Question</label>
            <p class="question-display">
              {{ form.task_question }}
            </p>
          </div>

          <div class="form-group">
            <label>Your Answer</label>
            <textarea v-model="form.user_answer" rows="6" placeholder="Write your answer..."></textarea>
          </div>

          <!-- Submit Button -->
          <button type="submit" class="button" :disabled="loading">
            <!-- Loading Spinner -->
            <span v-if="loading">
              <span class="spinner"></span>
              Submitting...
            </span>
            <span v-else>Submit</span>
          </button>
        </form>

        <!-- Evaluation Result (fade transition) -->
        <transition name="fade">
          <div v-if="result" class="result-box">
            <h3>Evaluation Result</h3>
            <p><strong>Question:</strong> {{ form.task_question }}</p>
            <p><strong>Answer:</strong> {{ form.user_answer }}</p>
            <p><strong>Band Score:</strong> {{ result.band_score }}</p>
            <p><strong>Feedback:</strong> {{ result.ai_feedback }}</p>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SubmitWriting',
  data() {
    return {
      /* We store both the question and user answer in 'form' */
      form: {
        task_question: '',
        user_answer: '',
      },
      /* List of possible random questions */
      questions: [
        'Describe a time you faced a challenge at work.',
        'What is your opinion on the impact of social media on society?',
        'Explain a situation where you had to solve a complex problem.',
        'Discuss the importance of maintaining a healthy lifestyle.',
        'How can technology improve education in developing countries?',
        'Describe a memorable travel experience you have had.',
        'Should governments prioritize public transport over private cars?',
        'Explain the benefits and drawbacks of remote work.',
        'Discuss the effects of climate change on global economies.',
        'What are the advantages of learning a second language early in life?',
      ],
      result: null,
      error: null,
      loading: false,
    };
  },
  created() {
    // Pick one question at random on component load
    this.form.task_question = this.getRandomQuestion();
  },
  methods: {
    getRandomQuestion() {
      const index = Math.floor(Math.random() * this.questions.length);
      return this.questions[index];
    },
    async submitWriting() {
      this.error = null;
      this.loading = true;
      try {
        // Adjust the URL to match your backend (127.0.0.1 vs. localhost)
        const response = await axios.post('http://127.0.0.1:8000/api/submit-writing/', this.form);
        this.result = response.data;
      } catch (err) {
        this.error = 'Error submitting writing. Please try again.';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
/* 
  Full-page gradient background 
  and a flex layout for easy vertical/horizontal centering 
*/
.submit-page {
  min-height: 100vh;
  background: linear-gradient(90deg, #667eea, #764ba2); /* Indigo to Purple */
  display: flex;
  flex-direction: column;
}

/* Translucent, frosted-glass style navbar */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
}
.navbar h1 {
  font-size: 1.5rem;
  color: #fff;
}
.nav-link {
  color: #fff;
  margin-left: 1rem;
  text-decoration: none;
  font-weight: 500;
}
.nav-link:hover {
  text-decoration: underline;
}

/* Center the card in the middle of the page */
.submit-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

/* White card with shadow */
.card {
  width: 100%;
  max-width: 600px;
  background-color: #fff;
  border-radius: 0.5rem;
  padding: 2rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  color: #333;
}

.card h2 {
  margin-bottom: 1rem;
  font-size: 1.5rem;
  text-align: center;
  color: #333;
}

/* Form styling */
.form-group {
  margin-bottom: 1rem;
}
.form-group label {
  font-weight: bold;
  display: block;
  margin-bottom: 0.5rem;
}
.form-group textarea {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 0.25rem;
  padding: 0.75rem;
  resize: vertical;
  font-family: inherit;
  font-size: 1rem;
}

/* Display the question in a read-only box */
.question-display {
  background-color: #f9fafb;
  border: 1px solid #ccc;
  border-radius: 0.25rem;
  padding: 0.75rem;
  margin-top: 0.25rem;
  min-height: 3rem; /* ensure some space even for short text */
}

/* Submit button styling */
.button {
  width: 100%;
  background-color: #667eea;
  color: #fff;
  border: none;
  border-radius: 0.25rem;
  padding: 0.75rem;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  margin-top: 0.5rem;
  transition: background-color 0.2s ease;
}
.button:hover {
  background-color: #5a67d8;
}
.button:disabled {
  background-color: #a0aec0;
  cursor: not-allowed;
}

/* Error message box */
.error-message {
  background-color: #fed7d7;
  color: #c53030;
  padding: 1rem;
  border-radius: 0.25rem;
  margin-bottom: 1rem;
}

/* Result box */
.result-box {
  background-color: #f0fff4;
  border: 1px solid #c6f6d5;
  color: #22543d;
  padding: 1rem;
  border-radius: 0.25rem;
  margin-top: 1rem;
}

/* Fade transition for error and result sections */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Spinner animation for loading state */
.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid white;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  margin-right: 0.5rem;
  vertical-align: middle;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
