import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CourseProgressService from './course-progress.service';
import { type ICourseProgress } from '@/shared/model/course-progress.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseProgressDetails',
  setup() {
    const courseProgressService = inject('courseProgressService', () => new CourseProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const courseProgress: Ref<ICourseProgress> = ref({});

    const retrieveCourseProgress = async courseProgressId => {
      try {
        const res = await courseProgressService().find(courseProgressId);
        courseProgress.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseProgressId) {
      retrieveCourseProgress(route.params.courseProgressId);
    }

    return {
      alertService,
      courseProgress,

      previousState,
      t$: useI18n().t,
    };
  },
});
