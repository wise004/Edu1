import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import LessonProgressService from './lesson-progress.service';
import { useDateFormat } from '@/shared/composables';
import { type ILessonProgress } from '@/shared/model/lesson-progress.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LessonProgressDetails',
  setup() {
    const dateFormat = useDateFormat();
    const lessonProgressService = inject('lessonProgressService', () => new LessonProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const lessonProgress: Ref<ILessonProgress> = ref({});

    const retrieveLessonProgress = async lessonProgressId => {
      try {
        const res = await lessonProgressService().find(lessonProgressId);
        lessonProgress.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.lessonProgressId) {
      retrieveLessonProgress(route.params.lessonProgressId);
    }

    return {
      ...dateFormat,
      alertService,
      lessonProgress,

      previousState,
      t$: useI18n().t,
    };
  },
});
