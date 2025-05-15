import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import CertificateService from './certificate.service';
import { type ICertificate } from '@/shared/model/certificate.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Certificate',
  setup() {
    const { t: t$ } = useI18n();
    const dateFormat = useDateFormat();
    const certificateService = inject('certificateService', () => new CertificateService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const certificates: Ref<ICertificate[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveCertificates = async () => {
      isFetching.value = true;
      try {
        const res = await certificateService().retrieve();
        certificates.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveCertificates();
    };

    onMounted(async () => {
      await retrieveCertificates();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: ICertificate) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeCertificate = async () => {
      try {
        await certificateService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.certificate.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveCertificates();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      certificates,
      handleSyncList,
      isFetching,
      retrieveCertificates,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeCertificate,
      t$,
    };
  },
});
