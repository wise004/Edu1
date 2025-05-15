<!-- src/main/webapp/app/entities/course/course-items.vue -->
<template>
  <div class="course-items-container">
    <h2 id="course-items-heading" data-cy="CourseItemsHeading">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.title')"></span>
    </h2>

    <!-- Loading State -->
    <div v-if="isFetching" class="alert alert-info text-center">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.loading')"></span>
    </div>

    <!-- Course Items List -->
    <div v-else-if="sortedItems && sortedItems.length > 0" class="course-items-list">
      <div v-for="item in sortedItems" :key="item.id" class="course-item-card" data-cy="entityTable">
        <div class="course-item-header">
          <h3 class="course-item-title">{{ item.title }}</h3>
          <span class="course-item-type">{{ item.itemType }}</span>
        </div>
        <div class="course-item-content">
          <!-- YouTube Video -->
          <div v-if="item.contentType === 'YOUTUBE_VIDEO'" class="video-wrapper">
            <iframe
              v-if="getYouTubeEmbedUrl(item.content)"
              :src="getYouTubeEmbedUrl(item.content)"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
              class="youtube-video"
            ></iframe>
            <p v-else class="invalid-video-url">YouTube video URL noto'g'ri: {{ item.content }}</p>
          </div>
          <!-- Text Content -->
          <div v-else class="text-content">
            {{ item.content }}
          </div>
        </div>
        <div class="course-item-footer">
          <button @click="startItem(item)" class="start-button">
            <font-awesome-icon icon="play" class="mr-2"></font-awesome-icon>
            <span v-text="$t('entity.action.start')"></span>
          </button>
        </div>
      </div>
    </div>

    <!-- Fallback if no items -->
    <div class="alert alert-warning text-center" v-if="!isFetching && sortedItems && sortedItems.length === 0">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.notFound')"></span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useI18n } from 'vue-i18n';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  name: 'CourseItems',
  setup() {
    const { t: t$ } = useI18n();
    const route = useRoute();
    const router = useRouter();
    const alertService = useAlertService();
    const items = ref<any[]>([]);
    const isFetching = ref(false);
    const courseId = ref(route.params.courseId);

    // Fetch course items
    const retrieveCourseItems = async () => {
      isFetching.value = true;
      try {
        console.log('Fetching course items for courseId:', courseId.value);
        const token = window.localStorage.getItem('jhi-authenticationToken')
        console.log('JWT Token:', token);
        if (!token) {
          throw new Error('No JWT token found in localStorage or sessionStorage');
        }
        const res = await axios.get(`/api/courses/${courseId.value}/items`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        console.log('Course items response:', res);
        items.value = res.data;
        console.log('Items set to:', items.value);
      } catch (err) {
        console.error('Error fetching course items:', err);
        console.error('Error response:', err.response);
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    // Sort items by ID in ascending order
    const sortedItems = computed(() => {
      return [...items.value].sort((a, b) => a.id - b.id);
    });

    // Navigate to lesson or test page
    const startItem = (item: any) => {
      console.log('Starting item:', item);
      console.log('Course ID:', courseId.value);
      if (item.itemType === 'LESSON') {
        console.log('Navigating to lesson:', `/course/${courseId.value}/lesson/${item.id}`);
        router.push(`/course/${courseId.value}/lesson/${item.id}`);
      } else if (item.itemType === 'TEST') {
        console.log('Navigating to test:', `/course/${courseId.value}/test/${item.id}`);
        router.push(`/course/${courseId.value}/test/${item.id}`);
      }
    };

    // Convert YouTube URL to embed URL
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

    onMounted(() => {
      console.log('CourseItems mounted');
      retrieveCourseItems();
    });

    return {
      items,
      sortedItems,
      isFetching,
      startItem,
      getYouTubeEmbedUrl,
      t$,
    };
  },
});
</script>

<style scoped>
.course-items-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.course-items-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.course-item-card {
  width: 300px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.course-item-header {
  padding: 16px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-item-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.course-item-type {
  font-size: 0.9rem;
  color: #666;
  background-color: #e9ecef;
  padding: 4px 8px;
  border-radius: 12px;
}

.course-item-content {
  padding: 16px;
  flex-grow: 1;
}

.video-wrapper {
  position: relative;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
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
  color: #dc3545; /* Red color for error */
  font-style: italic;
}

.text-content {
  font-size: 0.95rem;
  color: #666;
}

.course-item-footer {
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  text-align: right;
}

.start-button {
  border: none;
  background-color: #007bff;
  color: #fff;
  padding: 8px 16px;
  font-size: 1rem;
  cursor: pointer;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  transition: background-color 0.3s ease;
}

.start-button:hover {
  background-color: #0056b3;
}

.start-button .mr-2 {
  margin-right: 8px;
}

.text-center {
  text-align: center;
}
</style>
