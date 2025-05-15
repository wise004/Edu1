import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import LessonProgressService from './lesson-progress.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CourseItemService from '@/entities/course-item/course-item.service';
import { type ICourseItem } from '@/shared/model/course-item.model';
import { type ILessonProgress, LessonProgress } from '@/shared/model/lesson-progress.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'LessonProgressUpdate',
  setup() {
    const lessonProgressService = inject('lessonProgressService', () => new LessonProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const lessonProgress: Ref<ILessonProgress> = ref(new LessonProgress());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const courseItemService = inject('courseItemService', () => new CourseItemService());

    const courseItems: Ref<ICourseItem[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveLessonProgress = async lessonProgressId => {
      try {
        const res = await lessonProgressService().find(lessonProgressId);
        res.viewedDate = new Date(res.viewedDate);
        lessonProgress.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.lessonProgressId) {
      retrieveLessonProgress(route.params.lessonProgressId);
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
      viewed: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      viewedDate: {},
      student: {},
      courseItem: {},
    };
    const v$ = useVuelidate(validationRules, lessonProgress as any);
    v$.value.$validate();

    return {
      lessonProgressService,
      alertService,
      lessonProgress,
      previousState,
      isSaving,
      currentLanguage,
      users,
      courseItems,
      v$,
      ...useDateFormat({ entityRef: lessonProgress }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.lessonProgress.id) {
        this.lessonProgressService()
          .update(this.lessonProgress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.lessonProgress.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.lessonProgressService()
          .create(this.lessonProgress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.lessonProgress.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
