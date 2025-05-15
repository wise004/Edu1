import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CourseProgressService from './course-progress.service';
import { type ICourseProgress } from '@/shared/model/course-progress.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseProgress',
  setup() {
    const { t: t$ } = useI18n();
    const courseProgressService = inject('courseProgressService', () => new CourseProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const courseProgresses: Ref<ICourseProgress[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCourseProgresss = async () => {
      isFetching.value = true;
      try {
        const res = await courseProgressService().retrieve();
        courseProgresses.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCourseProgresss();
    };

    onMounted(async () => {
      await retrieveCourseProgresss();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICourseProgress) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCourseProgress = async () => {
      try {
        await courseProgressService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.courseProgress.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCourseProgresss();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      courseProgresses,
      handleSyncList,
      isFetching,
      retrieveCourseProgresss,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCourseProgress,
      t$,
    };
  },
});
