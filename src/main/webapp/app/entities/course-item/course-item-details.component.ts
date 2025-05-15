import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CourseItemService from './course-item.service';
import { type ICourseItem } from '@/shared/model/course-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseItemDetails',
  setup() {
    const courseItemService = inject('courseItemService', () => new CourseItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const courseItem: Ref<ICourseItem> = ref({});

    const retrieveCourseItem = async courseItemId => {
      try {
        const res = await courseItemService().find(courseItemId);
        courseItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseItemId) {
      retrieveCourseItem(route.params.courseItemId);
    }

    return {
      alertService,
      courseItem,

      previousState,
      t$: useI18n().t,
    };
  },
});
