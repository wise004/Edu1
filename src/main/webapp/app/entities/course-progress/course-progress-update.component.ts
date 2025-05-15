import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CourseProgressService from './course-progress.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import UserService from '@/entities/user/user.service';
import CourseService from '@/entities/course/course.service';
import { type ICourse } from '@/shared/model/course.model';
import { CourseProgress, type ICourseProgress } from '@/shared/model/course-progress.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseProgressUpdate',
  setup() {
    const courseProgressService = inject('courseProgressService', () => new CourseProgressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const courseProgress: Ref<ICourseProgress> = ref(new CourseProgress());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const courseService = inject('courseService', () => new CourseService());

    const courses: Ref<ICourse[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      courseService()
        .retrieve()
        .then(res => {
          courses.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      completedItems: {},
      isCompleted: {},
      student: {},
      course: {},
      certificate: {},
    };
    const v$ = useVuelidate(validationRules, courseProgress as any);
    v$.value.$validate();

    return {
      courseProgressService,
      alertService,
      courseProgress,
      previousState,
      isSaving,
      currentLanguage,
      users,
      courses,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.courseProgress.id) {
        this.courseProgressService()
          .update(this.courseProgress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.courseProgress.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.courseProgressService()
          .create(this.courseProgress)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.courseProgress.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
