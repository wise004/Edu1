<template>
  <!-- Centered Card -->
  <div class="bg-white text-gray-800 rounded-lg shadow-md p-6 max-w-xl mx-auto relative">
    <h2 class="text-2xl font-bold mb-4 text-center">Submit Your Writing</h2>

    <!-- Error Alert (Fade Transition) -->
    <transition name="fade">
      <div v-if="error" class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
        {{ error }}
      </div>
    </transition>

    <!-- Submission Form -->
    <form @submit.prevent="submitWriting" class="space-y-4">
      <div>
        <label class="font-semibold block mb-1">Task Question</label>
        <textarea
          v-model="form.task_question"
          rows="3"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:border-indigo-500"
          placeholder="Enter the question..."
        ></textarea>
      </div>
      <div>
        <label class="font-semibold block mb-1">Your Answer</label>
        <textarea
          v-model="form.user_answer"
          rows="6"
          class="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:border-indigo-500"
          placeholder="Write your answer..."
        ></textarea>
      </div>

      <!-- Submit Button with Loading State -->
      <button
        type="submit"
        :disabled="loading"
        class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded w-full focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2 transition-colors"
      >
        <span v-if="loading" class="flex items-center justify-center gap-2">
          <!-- Simple Spinner -->
          <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
          </svg>
          Submitting...
        </span>
        <span v-else>Submit</span>
      </button>
    </form>

    <!-- Result (Fade Transition) -->
    <transition name="fade">
      <div v-if="result" class="mt-6 p-4 bg-green-50 border border-green-200 rounded">
        <h3 class="text-xl font-bold text-green-700 mb-2">Evaluation Result</h3>

        <p class="mb-2 text-sm text-gray-700"><strong>Question:</strong> {{ form.task_question }}</p>
        <p class="mb-4 text-sm text-gray-700"><strong>Answer:</strong> {{ form.user_answer }}</p>
        <p class="text-gray-800"><strong>Band Score:</strong> {{ result.band_score }}</p>
        <p class="text-gray-800"><strong>Feedback:</strong> {{ result.ai_feedback }}</p>
      </div>
    </transition>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SubmitWriting',
  data() {
    return {
      form: {
        task_question: '',
        user_answer: '',
      },
      result: null,
      error: null,
      loading: false,
    };
  },
  methods: {
    async submitWriting() {
      this.error = null;
      this.loading = true;
      try {
        // Use your actual endpoint (127.0.0.1 vs. localhost, etc.)
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
/* Fade transition for error & result sections */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
