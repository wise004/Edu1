import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CourseService from './course.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { Course, type ICourse } from '@/shared/model/course.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseUpdate',
  setup() {
    const courseService = inject('courseService', () => new CourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const course: Ref<ICourse> = ref(new Course());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCourse = async courseId => {
      try {
        const res = await courseService().find(courseId);
        course.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseId) {
      retrieveCourse(route.params.courseId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      author: {},
    };
    const v$ = useVuelidate(validationRules, course as any);
    v$.value.$validate();

    return {
      courseService,
      alertService,
      course,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.course.id) {
        this.courseService()
          .update(this.course)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.course.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.courseService()
          .create(this.course)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.course.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
