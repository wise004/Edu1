import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CertificateDetails from './certificate-details.vue';
import CertificateService from './certificate.service';
import AlertService from '@/shared/alert/alert.service';

type CertificateDetailsComponentType = InstanceType<typeof CertificateDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const certificateSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Certificate Management Detail Component', () => {
    let certificateServiceStub: SinonStubbedInstance<CertificateService>;
    let mountOptions: MountingOptions<CertificateDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      certificateServiceStub = sinon.createStubInstance<CertificateService>(CertificateService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          certificateService: () => certificateServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        certificateServiceStub.find.resolves(certificateSample);
        route = {
          params: {
            certificateId: `${123}`,
          },
        };
        const wrapper = shallowMount(CertificateDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.certificate).toMatchObject(certificateSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        certificateServiceStub.find.resolves(certificateSample);
        const wrapper = shallowMount(CertificateDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
