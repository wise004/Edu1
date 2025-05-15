import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import LessonProgressService from './lesson-progress.service';
import { type ILessonProgress } from '@/shared/model/lesson-progress.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LessonProgress',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const lessonProgressService = inject('lessonProgressService', () => new LessonProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const lessonProgresses: Ref<ILessonProgress[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveLessonProgresss = async () => {
      isFetching.value = true;
      try {
        const res = await lessonProgressService().retrieve();
        lessonProgresses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveLessonProgresss();
    };

    onMounted(async () => {
      await retrieveLessonProgresss();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ILessonProgress) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeLessonProgress = async () => {
      try {
        await lessonProgressService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.lessonProgress.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveLessonProgresss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      lessonProgresses,
      handleSyncList,
      isFetching,
      retrieveLessonProgresss,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeLessonProgress,
      t$,
    };
  },
});
