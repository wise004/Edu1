import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TestAttemptService from './test-attempt.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CourseItemService from '@/entities/course-item/course-item.service';
import { type ICourseItem } from '@/shared/model/course-item.model';
import { type ITestAttempt, TestAttempt } from '@/shared/model/test-attempt.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestAttemptUpdate',
  setup() {
    const testAttemptService = inject('testAttemptService', () => new TestAttemptService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const testAttempt: Ref<ITestAttempt> = ref(new TestAttempt());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const courseItemService = inject('courseItemService', () => new CourseItemService());

    const courseItems: Ref<ICourseItem[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTestAttempt = async testAttemptId => {
      try {
        const res = await testAttemptService().find(testAttemptId);
        res.attemptDate = new Date(res.attemptDate);
        testAttempt.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testAttemptId) {
      retrieveTestAttempt(route.params.testAttemptId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      courseItemService()
        .retrieve()
        .then(res => {
          courseItems.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      score: {},
      passed: {},
      attemptDate: {},
      student: {},
      courseItem: {},
    };
    const v$ = useVuelidate(validationRules, testAttempt as any);
    v$.value.$validate();

    return {
      testAttemptService,
      alertService,
      testAttempt,
      previousState,
      isSaving,
      currentLanguage,
      users,
      courseItems,
      v$,
      ...useDateFormat({ entityRef: testAttempt }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.testAttempt.id) {
        this.testAttemptService()
          .update(this.testAttempt)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.testAttempt.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.testAttemptService()
          .create(this.testAttempt)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.testAttempt.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
