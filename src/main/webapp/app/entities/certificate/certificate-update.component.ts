import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CertificateService from './certificate.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CourseProgressService from '@/entities/course-progress/course-progress.service';
import { type ICourseProgress } from '@/shared/model/course-progress.model';
import { Certificate, type ICertificate } from '@/shared/model/certificate.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CertificateUpdate',
  setup() {
    const certificateService = inject('certificateService', () => new CertificateService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const certificate: Ref<ICertificate> = ref(new Certificate());

    const courseProgressService = inject('courseProgressService', () => new CourseProgressService());

    const courseProgresses: Ref<ICourseProgress[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCertificate = async certificateId => {
      try {
        const res = await certificateService().find(certificateId);
        res.issueDate = new Date(res.issueDate);
        certificate.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.certificateId) {
      retrieveCertificate(route.params.certificateId);
    }

    const initRelationships = () => {
      courseProgressService()
        .retrieve()
        .then(res => {
          courseProgresses.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      issueDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      certificateUrl: {},
      courseProgress: {},
    };
    const v$ = useVuelidate(validationRules, certificate as any);
    v$.value.$validate();

    return {
      certificateService,
      alertService,
      certificate,
      previousState,
      isSaving,
      currentLanguage,
      courseProgresses,
      v$,
      ...useDateFormat({ entityRef: certificate }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.certificate.id) {
        this.certificateService()
          .update(this.certificate)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.certificate.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.certificateService()
          .create(this.certificate)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.certificate.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
