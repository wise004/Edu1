import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CourseItemService from './course-item.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CourseService from '@/entities/course/course.service';
import { type ICourse } from '@/shared/model/course.model';
import { CourseItem, type ICourseItem } from '@/shared/model/course-item.model';
import { ItemType } from '@/shared/model/enumerations/item-type.model';
import { ContentType } from '@/shared/model/enumerations/content-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CourseItemUpdate',
  setup() {
    const courseItemService = inject('courseItemService', () => new CourseItemService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const courseService = inject('courseService', () => new CourseService());

    const courseItem: Ref<ICourseItem> = ref(new CourseItem());
    const courses: Ref<ICourse[]> = ref([]);
    const itemTypeValues: Ref<string[]> = ref(Object.keys(ItemType));
    const contentTypeValues: Ref<string[]> = ref(Object.keys(ContentType));
    const isSaving = ref(false);
    const file: Ref<File | null> = ref(null);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCourseItem = async (courseItemId: number) => {
      try {
        const res = await courseItemService().find(courseItemId);
        courseItem.value = res;
      } catch (error: any) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.courseItemId) {
      retrieveCourseItem(Number(route.params.courseItemId));
    }

    const initRelationships = () => {
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
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      itemType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      contentType: {},
      content: {},
      passingScore: {},
      course: {},
    };
    const v$ = useVuelidate(validationRules, courseItem as any);
    v$.value.$validate();

    // Fayl inputdan tanlash funksiyasi
    const handleFileUpload = (event: Event): void => {
      const target = event.target as HTMLInputElement;
      if (target.files && target.files.length > 0) {
        file.value = target.files[0];
      }
    };

    return {
      courseItemService,
      alertService,
      courseItem,
      previousState,
      itemTypeValues,
      contentTypeValues,
      isSaving,
      currentLanguage,
      courses,
      v$,
      t$,
      file,
      handleFileUpload,
    };
  },
  methods: {
    save(): void {
      this.isSaving = true;
      // Agar contentType "UPLOADED_VIDEO" bo'lsa va fayl tanlangan bo'lsa, maxsus endpoint chaqiriladi
      if (this.courseItem.contentType === 'UPLOADED_VIDEO' && this.file) {
        const formData = new FormData();
        formData.append('title', this.courseItem.title || '');
        formData.append('itemType', this.courseItem.itemType || 'LESSON');
        formData.append('contentType', this.courseItem.contentType || '');
        formData.append('file', this.file);
        // Agar course tanlangan bo'lsa, course id ni ham jo'natish
        if (this.courseItem.course && this.courseItem.course.id) {
          formData.append('courseId', this.courseItem.course.id.toString());
        }
        this.courseItemService()
          .uploadLesson(this.courseItem.course?.id || 0, formData)
          .then(() => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.courseItem.created', { param: this.courseItem.id }).toString());
          })
          .catch((err: any) => {
            this.isSaving = false;
            this.alertService.showHttpError(err.response);
          });
      } else {
        // Agar oddiy JSON so'rov yuborilsa (YOUTUBE_VIDEO yoki TEXT) yoki update bo'lsa:
        if (this.courseItem.id) {
          this.courseItemService()
            .update(this.courseItem)
            .then(param => {
              this.isSaving = false;
              this.previousState();
              this.alertService.showInfo(this.t$('onlineCoursePlatformApp.courseItem.updated', { param: param.id }));
            })
            .catch((error: any) => {
              this.isSaving = false;
              this.alertService.showHttpError(error.response);
            });
        } else {
          this.courseItemService()
            .create(this.courseItem)
            .then(param => {
              this.isSaving = false;
              this.previousState();
              this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.courseItem.created', { param: param.id }).toString());
            })
            .catch((error: any) => {
              this.isSaving = false;
              this.alertService.showHttpError(error.response);
            });
        }
      }
    },
  },
});
