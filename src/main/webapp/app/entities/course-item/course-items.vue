<!-- src/main/webapp/app/entities/course-item/course-items.vue -->
<template>
  <div class="course-items-container">
    <!-- Header -->
    <h2 id="course-items-heading" data-cy="CourseItemsHeading" class="course-items-title">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.title')"></span>
      <button class="btn btn-info ml-3" @click="retrieveCourseItems" :disabled="isFetching">
        <font-awesome-icon icon="sync" :spin="isFetching" />
        Yangilash
      </button>
    </h2>

    <!-- Loading State -->
    <div v-if="isFetching" class="loading-container">
      <div class="spinner"></div>
      <span class="loading-text" v-text="$t('onlineCoursePlatformApp.courseItem.home.loading')"></span>
    </div>

    <!-- Course Items List -->
    <div v-else-if="sortedItems && sortedItems.length > 0" class="course-items-grid">
      <div v-for="item in sortedItems" :key="item.id" class="course-item-card" data-cy="entityTable">
        <div class="course-item-content">
          <!-- YouTube Video Thumbnail -->
          <div v-if="item.contentType === 'YOUTUBE_VIDEO'" class="video-wrapper">
            <div class="thumbnail-container" @click="startItem(item)">
              <img :src="getYouTubeThumbnail(item.content)" class="thumbnail" :alt="item.title" />
              <div class="play-button"></div>
            </div>
          </div>
          <!-- Text Content -->
          <div v-else class="text-content">
            {{ item.content }}
          </div>
        </div>
        <div class="course-item-details">
          <div class="course-item-header">
            <h3 class="course-item-title">{{ item.title }}</h3>
            <span class="course-item-type">{{ item.itemType }}</span>
          </div>
          <button @click="startItem(item)" class="start-button">
            <font-awesome-icon icon="play" class="mr-2"></font-awesome-icon>
            <span v-text="$t('entity.action.start')"></span>
          </button>
        </div>
      </div>
    </div>

    <!-- Fallback if no items -->
    <div v-else class="no-items-container">
      <font-awesome-icon icon="exclamation-circle" class="no-items-icon" />
      <span class="no-items-text" v-text="$t('onlineCoursePlatformApp.courseItem.home.notFound')"></span>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, computed, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useAlertService } from '@/shared/alert/alert.service';
import axios from '@/shared/config/axios';

export default defineComponent({
  name: 'CourseItems',
  setup() {
    const { t: t$ } = useI18n();
    const instance = getCurrentInstance();
    const i18n = instance?.proxy?.$i18n;
    console.log('Translation for entity.action.start:', t$('entity.action.start'));
    console.log('Current locale:', i18n?.locale);

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
        const token = window.localStorage.getItem('jhi-authenticationToken');
        console.log('JWT Token:', token);
        if (!token) {
          throw new Error('No JWT token found in localStorage or sessionStorage');
        }

        const res = await axios.get(`http://13.53.190.127:7777/api/courses/${courseId.value}/items`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log('Raw response:', res);
        console.log('Response status:', res.status);
        console.log('Response data:', res.data);
        if (!Array.isArray(res.data)) {
          console.error('Response data is not an array:', res.data);
          items.value = [];
        } else {
          items.value = res.data;
          console.log('Items set to:', items.value);
        }
      } catch (err) {
        console.error('Error fetching course items:', err);
        if (err.response && err.response.status === 401) {
          console.error('Token expired or invalid, redirecting to login...');
          localStorage.removeItem('jhi-authenticationToken');
          sessionStorage.removeItem('jhi-authenticationToken');
          router.push('/login');
        } else if (err.response) {
          console.error('Error response status:', err.response.status);
          console.error('Error response data:', err.response.data);
          alertService.showHttpError(err.response);
        } else if (err.request) {
          console.error('No response received:', err.request);
          alertService.showError(t$('error.noResponse', { message: 'No response from server' }));
        } else {
          console.error('Error setting up request:', err.message);
          alertService.showError(t$('error.requestSetup', { message: err.message }));
        }
      } finally {
        console.log('Fetch completed, isFetching:', isFetching.value);
        isFetching.value = false;
      }
    };

    // Sort items by orderIndex
    const sortedItems = computed(() => {
      console.log('Computing sortedItems, items:', items.value);
      if (!Array.isArray(items.value)) {
        console.error('Items is not an array:', items.value);
        return [];
      }
      const sorted = [...items.value].sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
      console.log('Sorted items:', sorted);
      return sorted;
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

    // Get YouTube thumbnail URL
    const getYouTubeThumbnail = (url: string): string => {
      try {
        const videoIdMatch = url.match(/(?:v=)([^&]+)/) || url.match(/(?:youtu\.be\/)([^?]+)/);
        const videoId = videoIdMatch ? videoIdMatch[1] : null;
        if (!videoId) {
          console.error('Invalid YouTube URL:', url);
          return '';
        }
        return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`;
      } catch (error) {
        console.error('Error parsing YouTube URL for thumbnail:', error);
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
      getYouTubeThumbnail,
      t$,
    };
  },
});
</script>

<style scoped>
/* General Container */
.course-items-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* Title */
.course-items-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 40px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Loading State */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e0e0e0;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 15px;
  font-size: 1.2rem;
  color: #666;
}

/* Course Items Grid */
.course-items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

/* Course Item Card */
.course-item-card {
  background-color: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
}

.course-item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* Video Thumbnail */
.video-wrapper {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 aspect ratio */
}

.thumbnail-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}

.play-button::before {
  content: '';
  display: block;
  width: 0;
  height: 0;
  border-left: 20px solid #fff;
  border-top: 12px solid transparent;
  border-bottom: 12px solid transparent;
}

.play-button:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

/* Text Content */
.text-content {
  padding: 20px;
  font-size: 1rem;
  color: #666;
  line-height: 1.6;
}

/* Course Item Details */
.course-item-details {
  padding: 20px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.course-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.course-item-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  line-height: 1.4;
}

.course-item-type {
  font-size: 0.9rem;
  color: #fff;
  background-color: #007bff;
  padding: 5px 10px;
  border-radius: 12px;
  text-transform: uppercase;
  font-weight: 500;
}

/* Start Button */
.start-button {
  margin-top: auto;
  border: none;
  background-color: #007bff;
  color: #fff;
  padding: 12px 20px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}

.start-button:hover {
  background-color: #0056b3;
}

.start-button .mr-2 {
  margin-right: 8px;
}

/* No Items State */
.no-items-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin: 20px 0;
}

.no-items-icon {
  font-size: 3rem;
  color: #f39c12;
  margin-bottom: 15px;
}

.no-items-text {
  font-size: 1.2rem;
  color: #666;
}

/* Responsive Design */
@media (max-width: 768px) {
  .course-items-title {
    font-size: 2rem;
  }

  .course-items-grid {
    grid-template-columns: 1fr;
  }

  .course-item-card {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .course-items-title {
    font-size: 1.8rem;
  }

  .course-item-title {
    font-size: 1.1rem;
  }

  .start-button {
    padding: 10px 15px;
    font-size: 0.9rem;
  }
}
</style>
