<!-- src/main/webapp/app/core/home/home.vue -->
<template>
  <div class="home-container">
    <!-- Header Section -->
    <div class="row">
      <div class="col-md-12 text-center">
        <h1 class="display-4" v-text="$t('home.title')"></h1>
        <p class="lead" v-text="$t('home.subtitle')"></p>
      </div>
    </div>

    <!-- Career Quiz Banner -->
    <div class="career-quiz-banner text-center">
      <h3 class="quiz-title" v-text="$t('home.careerQuiz.title')"></h3>
      <p class="quiz-description" v-text="$t('home.careerQuiz.description')"></p>
      <a href="https://www.ambitiousimpact.com/quiz" target="_blank" class="quiz-button">
        <i class="fas fa-question-circle"></i>
        <span v-text="$t('home.careerQuiz.button')"></span>
      </a>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center">
      <p v-text="$t('home.loading')"></p>
    </div>

    <!-- Course List -->
    <div v-else class="course-card-list">
      <div v-for="course in courses" :key="course.id" class="course-card">
        <img v-if="course.imageUrl" :src="course.imageUrl" class="course-image" alt="Course Image" />
        <div class="course-body">
          <h3 class="course-title">{{ course.title }}</h3>
          <p class="course-description">{{ course.description }}</p>
          <p class="course-price">
            {{ course.price && course.price > 0 ? course.price + ' so‘m' : $t('home.course.free') }}
          </p>

          <!-- Course Items (YouTube Videos) -->
          <!--          <div v-if="course.items && course.items.length > 0" class="course-items">-->
          <!--            <h4 v-text="$t('home.course.youtubeVideos')"></h4>-->
          <!--            <div v-for="item in course.items.filter(item => item.contentType === 'YOUTUBE_VIDEO')" :key="item.id" class="course-item">-->
          <!--              <h5>{{ item.title }}</h5>-->
          <!--              <div v-if="getYouTubeEmbedUrl(item.content)" class="video-wrapper">-->
          <!--                <iframe-->
          <!--                  :src="getYouTubeEmbedUrl(item.content)"-->
          <!--                  frameborder="0"-->
          <!--                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"-->
          <!--                  allowfullscreen-->
          <!--                  class="youtube-video"-->
          <!--                ></iframe>-->
          <!--              </div>-->
          <!--              <p v-else class="invalid-video-url" v-text="$t('home.course.invalidVideoUrl', { url: item.content })"></p>-->
          <!--            </div>-->
          <!--            <p-->
          <!--              v-if="!course.items.some(item => item.contentType === 'YOUTUBE_VIDEO')"-->
          <!--              class="no-videos"-->
          <!--              v-text="$t('home.course.noVideos')"-->
          <!--            ></p>-->
          <!--          </div>-->
          <!--          <div v-else class="no-videos" v-text="$t('home.course.noItems')"></div>-->
        </div>
        <button class="course-start-button" @click="goToCourseItems(course.id)" v-text="$t('home.course.startButton')"></button>
      </div>

      <!-- Fallback if no courses -->
      <p v-if="!courses.length" class="text-center no-courses" v-text="$t('home.noCourses')"></p>
    </div>

    <!-- IELTS Writing Test Banner -->
    <!--    <div class="ielts-writing-banner text-center">-->
    <!--      <h3 class="ielts-title" v-text="$t('home.ieltsWriting.title')"></h3>-->
    <!--      <p class="ielts-description" v-text="$t('home.ieltsWriting.description')"></p>-->
    <!--      <a href="http://localhost:5173/" target="_blank" class="ielts-button">-->
    <!--        <i class="fas fa-pen"></i>-->
    <!--        <span v-text="$t('home.ieltsWriting.button')"></span>-->
    <!--      </a>-->
    <!--    </div>-->

    <!-- Chatbot komponenti -->
    <chatbot />
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import CourseService from '@/entities/course/course.service';
import axios from 'axios';
import Chatbot from '@/components/Chatbot.vue';

export default defineComponent({
  name: 'Home',
  components: {
    Chatbot,
  },
  setup() {
    const { t: $t } = useI18n();
    const courses = ref<any[]>([]);
    const loading = ref(true);
    const courseService = new CourseService();

    onMounted(async () => {
      try {
        const response = await courseService.retrieve();
        console.log('API Response:', response);
        console.log('API Response Data:', response.data);

        let fetchedCourses = [];
        if (Array.isArray(response.data)) {
          fetchedCourses = response.data;
        } else if (response.data && Array.isArray(response.data.courses)) {
          fetchedCourses = response.data.courses;
        } else {
          console.warn('Unexpected API response structure:', response.data);
        }

        const token = window.localStorage.getItem('jhi-authenticationToken');
        if (!token) {
          console.error('No JWT token found. Please log in.');
          loading.value = false;
          return;
        }

        for (const course of fetchedCourses) {
          try {
            const itemsResponse = await axios.get(`/api/courses/${course.id}/items`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            console.log(`Items for course ${course.id}:`, itemsResponse.data);
            course.items = itemsResponse.data;
          } catch (error) {
            console.error(`Error fetching items for course ${course.id}:`, error);
            course.items = [];
          }
        }

        courses.value = fetchedCourses.map(course => ({
          id: course.id,
          title: course.title || course.name || 'Untitled Course',
          description: course.description || 'No description available',
          price: course.price || 0,
          imageUrl: course.imageUrl || course.image || null,
          items: course.items || [],
        }));

        console.log('Mapped Courses with Items:', courses.value);
      } catch (error) {
        console.error('Error fetching courses:', error);
      } finally {
        loading.value = false;
      }
    });

    const goToCourseItems = (courseId: number) => {
      (window as any).location.href = `/course/${courseId}/items`;
    };

    const getYouTubeEmbedUrl = (url: string): string => {
      try {
        const videoIdMatch = url.match(/(?:v=)([^&]+)/) || url.match(/(?:youtu\.be\/)([^?]+)/);
        const videoId = videoIdMatch ? videoIdMatch[1] : null;
        if (!videoId) {
          console.error('Invalid YouTube URL:', url);
          return '';
        }
        return `https://www.youtube.com/embed/${videoId}`;
      } catch (error) {
        console.error('Error parsing YouTube URL:', error);
        return '';
      }
    };

    return {
      courses,
      loading,
      goToCourseItems,
      getYouTubeEmbedUrl,
      $t,
    };
  },
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap');

.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* Career Quiz Banner Styles */
.career-quiz-banner {
  background: linear-gradient(135deg, #007bff, #00d4ff);
  border-radius: 12px;
  padding: 30px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  color: #fff;
  transition: transform 0.3s ease;
}

.career-quiz-banner:hover {
  transform: translateY(-5px);
}

.quiz-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.quiz-description {
  font-size: 1.1rem;
  margin-bottom: 20px;
}

.quiz-button {
  display: inline-flex;
  align-items: center;
  background-color: #fff;
  color: #007bff;
  padding: 12px 24px;
  border-radius: 25px;
  text-decoration: none;
  font-size: 1rem;
  font-weight: bold;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.quiz-button:hover {
  background-color: #0056b3;
  color: #fff;
}

.quiz-button i {
  margin-right: 8px;
}

/* IELTS Writing Test Banner Styles */
.ielts-writing-banner {
  background: linear-gradient(135deg, #28a745, #20c997);
  border-radius: 12px;
  padding: 30px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  color: #fff;
  transition: transform 0.3s ease;
  font-family: 'Poppins', sans-serif;
}

.ielts-writing-banner:hover {
  transform: translateY(-5px);
}

.ielts-title {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.ielts-description {
  font-size: 1.1rem;
  margin-bottom: 20px;
}

.ielts-button {
  display: inline-flex;
  align-items: center;
  background-color: #fff;
  color: #28a745;
  padding: 12px 24px;
  border-radius: 25px;
  text-decoration: none;
  font-size: 1rem;
  font-weight: 600;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

.ielts-button:hover {
  background-color: #218838;
  color: #fff;
}

.ielts-button i {
  margin-right: 8px;
}

.course-card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.course-card {
  width: 280px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.course-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.course-body {
  padding: 16px;
}

.course-title {
  font-size: 1.2rem;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

.course-description {
  font-size: 0.95rem;
  color: #666;
  margin-bottom: 12px;
}

.course-price {
  font-weight: bold;
  margin-bottom: 12px;
  color: #007bff;
}

.course-items {
  margin-top: 12px;
}

.course-item {
  margin-bottom: 16px;
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

.no-videos {
  font-size: 0.9rem;
  color: #666;
  font-style: italic;
}

.course-start-button {
  border: none;
  background-color: #007bff;
  color: #fff;
  padding: 12px;
  font-size: 1rem;
  cursor: pointer;
  text-align: center;
  width: 100%;
  transition: background-color 0.3s ease;
}

.course-start-button:hover {
  background-color: #0056b3;
}

.text-center {
  text-align: center;
}

.no-courses {
  font-size: 1.1rem;
  color: #666;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .career-quiz-banner,
  .ielts-writing-banner {
    padding: 20px;
  }

  .quiz-title,
  .ielts-title {
    font-size: 1.5rem;
  }

  .quiz-description,
  .ielts-description {
    font-size: 1rem;
  }

  .quiz-button,
  .ielts-button {
    padding: 10px 20px;
    font-size: 0.9rem;
  }
}
</style>
