import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CourseItemService from './course-item.service';
import { type ICourseItem } from '@/shared/model/course-item.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseItem',
  setup() {
    const { t: t$ } = useI18n();
    const courseItemService = inject('courseItemService', () => new CourseItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const courseItems: Ref<ICourseItem[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCourseItems = async () => {
      isFetching.value = true;
      try {
        const res = await courseItemService().retrieve();
        courseItems.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCourseItems();
    };

    onMounted(async () => {
      await retrieveCourseItems();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICourseItem) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCourseItem = async () => {
      try {
        await courseItemService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.courseItem.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCourseItems();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      courseItems,
      handleSyncList,
      isFetching,
      retrieveCourseItems,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCourseItem,
      t$,
    };
  },
});
