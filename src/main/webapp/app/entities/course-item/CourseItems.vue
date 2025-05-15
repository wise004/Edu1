<!-- src/main/webapp/app/entities/course/course-items.vue -->
<template>
  <div>
    <h2 id="course-items-heading" data-cy="CourseItemsHeading" style="display: flex; justify-content: space-between">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.title')"></span>
      <a v-if="shouldShowIeltsButton" href="http://13.53.216.88/submit" target="_blank" class="ielts-button">
        <i class="fas fa-pen"></i>
        <span v-text="$t('home.ieltsWriting.button')"></span>
      </a>
    </h2>

    <div v-if="isFetching" class="alert alert-info">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.loading')"></span>
    </div>

    <div v-else-if="items && items.length > 0" class="table-responsive">
      <table class="table table-striped" aria-describedby="courseItems">
        <thead>
          <tr>
            <th scope="col">
              <span v-text="$t('onlineCoursePlatformApp.courseItem.title')"></span>
            </th>
            <th scope="col">
              <span v-text="$t('onlineCoursePlatformApp.courseItem.itemType')"></span>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in items"
            :key="item.id"
            data-cy="entityTable"
            @click="openVideoModal(item)"
            :class="{ 'cursor-pointer': item.contentType === 'YOUTUBE_VIDEO' }"
          >
            <td>{{ item.title }}</td>
            <td>{{ item.itemType }}</td>
            <td class="text-right">
              <div class="btn-group">
                <button @click="startItem(item)" class="btn btn-primary btn-sm">
                  <font-awesome-icon icon="play"></font-awesome-icon>
                  <span v-text="$t('entity.action.start')"></span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="alert alert-warning" v-if="!isFetching && items && items.length === 0">
      <span v-text="$t('onlineCoursePlatformApp.courseItem.home.notFound')"></span>
    </div>
  </div>

  <div v-if="showModal" class="modal-backdrop" @click="showModal = false"></div>
  <div v-if="showModal" class="modal-container">
    <div class="modal-content">
      <div class="modal-header">
        <h3>{{ selectedItem?.title }}</h3>
        <button class="close-button" @click="showModal = false">&times;</button>
      </div>
      <div class="modal-body">
        <div class="video-wrapper" v-if="selectedItem?.content && getYouTubeEmbedUrl(selectedItem.content)">
          <iframe
            :src="getYouTubeEmbedUrl(selectedItem.content)"
            frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen
            class="youtube-video"
          ></iframe>
        </div>
        <p v-else class="invalid-video-url">{{ $t('home.course.invalidVideoUrl', { url: selectedItem?.content }) }}</p>
      </div>
    </div>
  </div>
</template>

<!-- src/main/webapp/app/entities/course-item/course-items.vue -->
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
    const route = useRoute();
    const router = useRouter();
    const alertService = useAlertService();
    const items = ref<any[]>([]);
    const course = ref<any>(null);

    const showModal = ref(false);
    const selectedItem = ref<any>(null);

    const isFetching = ref(false);
    const courseId = ref(route.params.courseId);

    const retrieveCourseItems = async () => {
      isFetching.value = true;
      try {
        console.log('Fetching course items for courseId:', courseId.value);
        const token = window.localStorage.getItem('jhi-authenticationToken');
        console.log('JWT Token:', token || 'No token found'); // Debug: Log token presence
        if (!token) {
          console.error('No JWT token found, redirecting to login...');
          router.push('/login');
          return;
        }
        const res = await axios.get(`/api/courses/${courseId.value}/items`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log('Response data:', res.data);
        if (!Array.isArray(res.data)) {
          console.error('Response data is not an array:', res.data);
          items.value = [];
        } else {
          items.value = res.data;
        }
      } catch (err) {
        console.error('Error fetching course items:', err);
        if (err.response) {
          console.error('Error response status:', err.response.status);
          console.error('Error response data:', err.response.data);
          if (err.response.status === 401 || err.response.status === 403) {
            console.error('Unauthorized or Forbidden, redirecting to login...');
            localStorage.removeItem('jhi-authenticationToken');
            sessionStorage.removeItem('jhi-authenticationToken');
            router.push('/login');
            return;
          }
          alertService.showHttpError(err.response);
        } else {
          alertService.showError(t$('error.noResponse', { message: 'No response from server' }));
        }
      } finally {
        isFetching.value = false;
      }
    };

    const getCourse = async () => {
      isFetching.value = true;

      try {
        const id = courseId?.value;

        const token = window.localStorage.getItem('jhi-authenticationToken');

        if (!token) {
          console.error('No JWT token found, redirecting to login...');
          router.push('/login');
          return;
        }

        const res = await axios.get(`/api/courses/${courseId.value}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log('Course data:', res.data);

        if (res.data === null) {
          course.value = [];
        } else {
          course.value = res.data;
        }
      } catch (err) {
        console.error('Error fetching course items:', err);
      } finally {
        isFetching.value = false;
      }
    };

    const sortedItems = computed(() => {
      if (!Array.isArray(items.value)) return [];
      return [...items.value].sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0));
    });

    const startItem = (item: any) => {
      if (item.itemType === 'LESSON') {
        router.push(`/course/${courseId.value}/lesson/${item.id}`);
      } else if (item.itemType === 'TEST') {
        router.push(`/course/${courseId.value}/test/${item.id}`);
      }
    };

    const getYouTubeThumbnail = (url: string): string => {
      try {
        const videoIdMatch = url.match(/(?:v=)([^&]+)/) || url.match(/(?:youtu\.be\/)([^?]+)/);
        const videoId = videoIdMatch ? videoIdMatch[1] : null;
        if (!videoId) return '';
        return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`;
      } catch (error) {
        console.error('Error parsing YouTube URL for thumbnail:', error);
        return '';
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

    const openVideoModal = (item: any) => {
      if (item.contentType === 'YOUTUBE_VIDEO') {
        selectedItem.value = item;
        showModal.value = true;
      }
    };

    onMounted(() => {
      console.log('CourseItems mounted');
      retrieveCourseItems();
      getCourse();
    });

    const shouldShowIeltsButton = computed(() => {
      return course.value?.title?.toLowerCase().includes('english') || false;
    });

    return {
      items,
      sortedItems,
      isFetching,
      startItem,
      getYouTubeThumbnail,
      shouldShowIeltsButton,
      t$,
      showModal,
      selectedItem,
      getYouTubeEmbedUrl,
      openVideoModal,
    };
  },
});
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.modal-container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1001;
  width: 90%;
  max-width: 900px;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #dee2e6;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.5rem;
}

.modal-body {
  padding: 1rem;
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
</style>
