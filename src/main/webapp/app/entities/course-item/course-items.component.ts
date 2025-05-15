import { defineComponent, ref, onMounted } from 'vue';
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

    // Get the courseId from the route params
    const courseId = route.params.courseId;

    // Fetch course items for the specific course
    const retrieveCourseItems = async () => {
      isFetching.value = true;
      try {
        const response = await axios.get(`/api/courses/${courseId}/items`);
        items.value = response.data;
      } catch (error) {
        alertService.showHttpError(error.response);
      } finally {
        isFetching.value = false;
      }
    };

    // Start an item (e.g., navigate to a lesson or test page)
    const startItem = (item: any) => {
      if (item.itemType === 'LESSON') {
        router.push(`/course/${courseId}/lesson/${item.id}`);
      } else if (item.itemType === 'TEST') {
        router.push(`/course/${courseId}/test/${item.id}`);
      }
    };

    onMounted(() => {
      retrieveCourseItems();
    });

    return {
      items,
      isFetching,
      startItem,
      t$,
    };
  },
});
