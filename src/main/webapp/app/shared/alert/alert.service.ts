// src/main/webapp/app/shared/alert/alert.service.ts
import type { BvToast } from 'bootstrap-vue';
import { getCurrentInstance } from 'vue';
import { type Composer, useI18n } from 'vue-i18n';

export const useAlertService = () => {
  const bvToast = getCurrentInstance().root.proxy._bv__toast;
  if (!bvToast) {
    throw new Error('BootstrapVue toast component was not found');
  }
  const i18n = useI18n();
  return new AlertService({
    bvToast,
    i18n,
  });
};

export default class AlertService {
  private bvToast: BvToast;
  private i18n: Composer;

  constructor({ bvToast, i18n }: { bvToast: BvToast; i18n: Composer }) {
    this.bvToast = bvToast;
    this.i18n = i18n;
  }

  public showInfo(toastMessage: string, toastOptions?: any) {
    this.bvToast.toast(toastMessage, {
      toaster: 'b-toaster-top-center',
      title: 'Info',
      variant: 'info',
      solid: true,
      autoHideDelay: 5000,
      ...toastOptions,
    });
  }

  public showSuccess(toastMessage: string) {
    this.bvToast.toast(toastMessage, {
      toaster: 'b-toaster-top-center',
      title: 'Success',
      variant: 'success',
      solid: true,
      autoHideDelay: 5000,
    });
  }

  public showError(toastMessage: string) {
    this.bvToast.toast(toastMessage, {
      toaster: 'b-toaster-top-center',
      title: 'Error',
      variant: 'danger',
      solid: true,
      autoHideDelay: 5000,
    });
  }

  public showHttpError(httpErrorResponse: any) {
    let errorMessage: string;

    // Check if httpErrorResponse is defined and has a status property
    if (!httpErrorResponse || typeof httpErrorResponse !== 'object' || !('status' in httpErrorResponse)) {
      console.error('Invalid HTTP error response:', httpErrorResponse); // Debug: Log the error response
      errorMessage = this.i18n.t('error.unknown').toString(); // Fallback error message
      this.showError(errorMessage);
      return;
    }

    switch (httpErrorResponse.status) {
      case 0:
        errorMessage = this.i18n.t('error.server.not.reachable').toString();
        break;

      case 400: {
        const arr = Object.keys(httpErrorResponse.headers || {});
        let entityKey: string | null = null;
        let appError: string | null = null;
        for (const entry of arr) {
          if (entry.toLowerCase().endsWith('app-error')) {
            appError = httpErrorResponse.headers[entry];
          } else if (entry.toLowerCase().endsWith('app-params')) {
            entityKey = httpErrorResponse.headers[entry];
          }
        }
        if (appError && entityKey) {
          errorMessage = this.i18n.t(appError, { entityName: this.i18n.t(`global.menu.entities.${entityKey}`) }).toString();
        } else if (appError) {
          errorMessage = this.i18n.t(appError).toString();
        } else if (httpErrorResponse.data && httpErrorResponse.data.message) {
          errorMessage = this.i18n.t(httpErrorResponse.data.message).toString();
        } else {
          errorMessage = this.i18n.t('error.http.400').toString();
        }
        break;
      }

      case 401:
        errorMessage = this.i18n.t('error.http.401').toString();
        break;

      case 403:
        errorMessage = this.i18n.t('error.http.403').toString();
        break;

      case 404:
        errorMessage = this.i18n.t('error.http.404').toString();
        break;

      case 500:
        errorMessage = this.i18n.t('error.http.500').toString();
        break;

      default:
        if (httpErrorResponse.data && httpErrorResponse.data.message) {
          errorMessage = this.i18n.t(httpErrorResponse.data.message).toString();
        } else {
          errorMessage = this.i18n.t('error.http.default', { status: httpErrorResponse.status }).toString();
        }
    }
    this.showError(errorMessage);
  }
}
