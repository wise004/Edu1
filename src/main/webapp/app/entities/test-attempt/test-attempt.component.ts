import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import TestAttemptService from './test-attempt.service';
import { type ITestAttempt } from '@/shared/model/test-attempt.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestAttempt',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const testAttemptService = inject('testAttemptService', () => new TestAttemptService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const testAttempts: Ref<ITestAttempt[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveTestAttempts = async () => {
      isFetching.value = true;
      try {
        const res = await testAttemptService().retrieve();
        testAttempts.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveTestAttempts();
    };

    onMounted(async () => {
      await retrieveTestAttempts();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ITestAttempt) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeTestAttempt = async () => {
      try {
        await testAttemptService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.testAttempt.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveTestAttempts();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      testAttempts,
      handleSyncList,
      isFetching,
      retrieveTestAttempts,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeTestAttempt,
      t$,
    };
  },
});
