import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import CertificateService from './certificate.service';
import { useDateFormat } from '@/shared/composables';
import { type ICertificate } from '@/shared/model/certificate.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CertificateDetails',
  setup() {
    const dateFormat = useDateFormat();
    const certificateService = inject('certificateService', () => new CertificateService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const certificate: Ref<ICertificate> = ref({});

    const retrieveCertificate = async certificateId => {
      try {
        const res = await certificateService().find(certificateId);
        certificate.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.certificateId) {
      retrieveCertificate(route.params.certificateId);
    }

    return {
      ...dateFormat,
      alertService,
      certificate,

      previousState,
      t$: useI18n().t,
    };
  },
});
