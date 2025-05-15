import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CourseService from './course.service';
import { type ICourse } from '@/shared/model/course.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseDetails',
  setup() {
    const courseService = inject('courseService', () => new CourseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const course: Ref<ICourse> = ref({});

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

    return {
      alertService,
      course,

      previousState,
      t$: useI18n().t,
    };
  },
});
