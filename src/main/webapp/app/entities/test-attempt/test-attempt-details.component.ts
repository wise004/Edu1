import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TestAttemptService from './test-attempt.service';
import { useDateFormat } from '@/shared/composables';
import { type ITestAttempt } from '@/shared/model/test-attempt.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestAttemptDetails',
  setup() {
    const dateFormat = useDateFormat();
    const testAttemptService = inject('testAttemptService', () => new TestAttemptService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const testAttempt: Ref<ITestAttempt> = ref({});

    const retrieveTestAttempt = async testAttemptId => {
      try {
        const res = await testAttemptService().find(testAttemptId);
        testAttempt.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testAttemptId) {
      retrieveTestAttempt(route.params.testAttemptId);
    }

    return {
      ...dateFormat,
      alertService,
      testAttempt,

      previousState,
      t$: useI18n().t,
    };
  },
});
