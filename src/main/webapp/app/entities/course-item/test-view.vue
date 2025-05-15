<!-- src/main/webapp/app/entities/course-item/test-view.vue -->
<template>
  <div class="test-view-container">
    <h2 class="test-title">{{ test?.title }}</h2>
    <div v-if="isFetching" class="alert alert-info text-center">Yuklanmoqda...</div>
    <div v-else-if="test" class="test-content">
      <!-- Test Description -->
      <div v-if="test.content" class="test-description">
        <div v-if="test.contentType === 'YOUTUBE_VIDEO'" class="video-wrapper">
          <iframe
            v-if="getYouTubeEmbedUrl(test.content)"
            :src="getYouTubeEmbedUrl(test.content)"
            frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen
            class="youtube-video"
          ></iframe>
          <p v-else class="invalid-video-url">YouTube video URL noto'g'ri: {{ test.content }}</p>
        </div>
        <div v-else class="text-content">{{ test.content }}</div>
      </div>
      <div v-if="test.passingScore" class="passing-score">O'tish balli: {{ test.passingScore }}</div>

      <!-- Questions -->
      <div v-if="questions.length > 0" class="questions-section">
        <h3>Savollar</h3>
        <form @submit.prevent="submitTest">
          <div v-for="(question, index) in questions" :key="question.id" class="question-card">
            <p class="question-text">{{ index + 1 }}. {{ question.text }}</p>
            <div v-if="question.type === 'MULTIPLE_CHOICE'" class="options">
              <div v-for="(option, optIndex) in question.options" :key="optIndex" class="form-check">
                <input
                  type="radio"
                  :name="'question-' + question.id"
                  :value="option"
                  v-model="answers[question.id]"
                  class="form-check-input"
                  :id="'option-' + question.id + '-' + optIndex"
                />
                <label class="form-check-label" :for="'option-' + question.id + '-' + optIndex">{{ option }}</label>
              </div>
            </div>
          </div>
          <button type="submit" class="btn btn-primary mt-3" :disabled="isSubmitting">Testni yuborish</button>
        </form>
      </div>
      <div v-else class="alert alert-warning">Bu test uchun savollar topilmadi.</div>

      <!-- Result -->
      <div v-if="result" class="result-section mt-4">
        <h3>Natija</h3>
        <p :class="{ 'text-success': result.passed, 'text-danger': !result.passed }">
          Ball: {{ result.score }} / {{ questions.length }} | {{ result.passed ? "O'tdi" : "O'tmadi" }}
        </p>
      </div>
    </div>
    <div v-else class="alert alert-warning text-center">Test topilmadi.</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

export default defineComponent({
  name: 'TestView',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const test = ref<any>(null);
    const questions = ref<any[]>([]);
    const answers = ref<{ [key: number]: string }>({});
    const result = ref<any>(null);
    const isFetching = ref(false);
    const isSubmitting = ref(false);
    const courseId = ref(route.params.courseId);
    const itemId = ref(route.params.itemId);

    const fetchTest = async () => {
      isFetching.value = true;
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        if (!token) {
          throw new Error('No JWT token found');
        }
        const res = await axios.get(`/api/course-items/${itemId.value}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        test.value = res.data;
      } catch (err) {
        console.error('Error fetching test:', err);
      } finally {
        isFetching.value = false;
      }
    };

    const fetchQuestions = async () => {
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        const res = await axios.get(`/api/questions/by-course-item/${itemId.value}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        questions.value = res.data;
      } catch (err) {
        console.error('Error fetching questions:', err);
      }
    };

    const submitTest = async () => {
      isSubmitting.value = true;
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        const attempt = {
          courseItemId: itemId.value,
          answers: Object.entries(answers.value).map(([questionId, answer]) => ({
            questionId: parseInt(questionId),
            selectedAnswer: answer,
          })),
        };
        const res = await axios.post(`/api/test-attempts`, attempt, {
          headers: { Authorization: `Bearer ${token}` },
        });
        result.value = res.data;
      } catch (err) {
        console.error('Error submitting test:', err);
      } finally {
        isSubmitting.value = false;
      }
    };

    const getYouTubeEmbedUrl = (url: string): string => {
      try {
        const videoIdMatch = url.match(/(?:v=)([^&]+)/) || url.match(/(?:youtu\.be\/)([^?]+)/);
        const videoId = videoIdMatch ? videoIdMatch[1] : null;
        if (!videoId) return '';
        return `https://www.youtube.com/embed/${videoId}`;
      } catch (error) {
        console.error('Error parsing YouTube URL:', error);
        return '';
      }
    };

    onMounted(() => {
      fetchTest();
      fetchQuestions();
    });

    return {
      test,
      questions,
      answers,
      result,
      isFetching,
      isSubmitting,
      getYouTubeEmbedUrl,
      submitTest,
    };
  },
});
</script>

<style scoped>
.test-view-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.test-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 20px;
}

.test-description {
  margin-bottom: 20px;
}

.video-wrapper {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
}

.youtube-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.invalid-video-url {
  font-size: 0.9rem;
  color: #dc3545;
  font-style: italic;
}

.text-content {
  font-size: 1rem;
  color: #333;
}

.passing-score {
  font-size: 1rem;
  font-weight: bold;
  color: #007bff;
}

.questions-section {
  margin-top: 20px;
}

.question-card {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.question-text {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 10px;
}

.options {
  margin-left: 20px;
}

.form-check {
  margin-bottom: 10px;
}

.result-section {
  background: #e9ecef;
  padding: 15px;
  border-radius: 8px;
}

.text-center {
  text-align: center;
}
</style>
