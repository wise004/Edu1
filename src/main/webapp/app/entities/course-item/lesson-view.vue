<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

export default defineComponent({
  name: 'LessonView',
  setup() {
    const route = useRoute();
    const lesson = ref<any>(null);
    const lessonProgress = ref<any>(null);
    const isFetching = ref(false);
    const isMarking = ref(false);
    const errorMessage = ref<string | null>(null);
    const courseId = ref(route.params.courseId);
    const itemId = ref(route.params.itemId);
    const player = ref<any>(null);

    const fetchLesson = async () => {
      isFetching.value = true;
      errorMessage.value = null;
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        if (!token) throw new Error('No JWT token found');
        const res = await axios.get(`/api/course-items/${itemId.value}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.data) throw new Error('Lesson data not found');
        lesson.value = res.data;
      } catch (err) {
        console.error('Error fetching lesson:', err);
        errorMessage.value = err.response?.status === 401 ? 'Please log in to view this lesson.' : err.message || 'Failed to load lesson.';
      } finally {
        isFetching.value = false;
      }
    };

    const fetchLessonProgress = async () => {
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        const res = await axios.get(`/api/lesson-progresses?courseItemId=${itemId.value}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        lessonProgress.value = res.data[0] || { viewed: false };
      } catch (err) {
        console.error('Error fetching lesson progress:', err);
      }
    };

    const markAsViewed = async () => {
      isMarking.value = true;
      try {
        const token = window.localStorage.getItem('jhi-authenticationToken')
        let progressId = lessonProgress.value?.id;
        if (!progressId) {
          const res = await axios.post(
            `/api/lesson-progresses`,
            { courseItemId: itemId.value, viewed: true, viewedDate: new Date().toISOString() },
            { headers: { Authorization: `Bearer ${token}` } },
          );
          lessonProgress.value = res.data;
        } else {
          await axios.patch(`/api/lesson-progresses/${progressId}/mark-viewed`, {}, { headers: { Authorization: `Bearer ${token}` } });
          lessonProgress.value.viewed = true;
        }
      } catch (err) {
        console.error('Error marking lesson as viewed:', err);
      } finally {
        isMarking.value = false;
      }
    };

    const getYouTubeEmbedUrl = (url: string): string => {
      try {
        const videoIdMatch = url.match(/(?:v=)([^&]+)/) || url.match(/(?:youtu\.be\/)([^?]+)/);
        const videoId = videoIdMatch ? videoIdMatch[1] : null;
        if (!videoId) {
          console.error('Invalid YouTube URL:', url);
          return '';
        }
        return videoId; // Return just the video ID for the player
      } catch (error) {
        console.error('Error parsing YouTube URL:', error);
        return '';
      }
    };

    const onVideoLoad = () => {
      const videoId = getYouTubeEmbedUrl(lesson.value.content);
      if (videoId) {
        player.value = new window.YT.Player('youtube-player', {
          videoId: videoId,
          events: {
            onStateChange: (event: any) => {
              if (event.data === window.YT.PlayerState.ENDED && !lessonProgress.value?.viewed) {
                markAsViewed(); // Automatically mark as viewed when the video ends
              }
            },
          },
        });
      }
    };

    onMounted(() => {
      fetchLesson();
      fetchLessonProgress();
    });

    return {
      lesson,
      lessonProgress,
      isFetching,
      isMarking,
      errorMessage,
      getYouTubeEmbedUrl,
      markAsViewed,
      onVideoLoad,
    };
  },
});
</script>

<template>
  <div class="lesson-view-container">
    <h2 class="lesson-title">{{ lesson?.title }}</h2>
    <div v-if="isFetching" class="alert alert-info text-center">Yuklanmoqda...</div>
    <div v-else-if="errorMessage" class="alert alert-danger text-center">
      {{ errorMessage }}
    </div>
    <div v-else-if="lesson" class="lesson-content">
      <div v-if="lesson.contentType === 'YOUTUBE_VIDEO'" class="video-wrapper">
        <div id="youtube-player" class="youtube-video"></div>
        <p v-if="!getYouTubeEmbedUrl(lesson.content)" class="invalid-video-url">YouTube video URL noto'g'ri: {{ lesson.content }}</p>
      </div>
      <div v-else class="text-content">{{ lesson.content }}</div>
      <button v-if="!lessonProgress?.viewed" @click="markAsViewed" class="btn btn-primary mt-3" :disabled="isMarking">
        Darsni tugatish
      </button>
      <p v-else class="text-success mt-3">Dars tugallangan!</p>
    </div>
    <div v-else class="alert alert-warning text-center">Dars topilmadi.</div>
  </div>
</template>
