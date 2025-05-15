import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Certificate from './certificate.vue';
import CertificateService from './certificate.service';
import AlertService from '@/shared/alert/alert.service';

type CertificateComponentType = InstanceType<typeof Certificate>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Certificate Management Component', () => {
    let certificateServiceStub: SinonStubbedInstance<CertificateService>;
    let mountOptions: MountingOptions<CertificateComponentType>['global'];

    beforeEach(() => {
      certificateServiceStub = sinon.createStubInstance<CertificateService>(CertificateService);
      certificateServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          certificateService: () => certificateServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        certificateServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Certificate, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(certificateServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.certificates[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CertificateComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Certificate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        certificateServiceStub.retrieve.reset();
        certificateServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        certificateServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCertificate();
        await comp.$nextTick(); // clear components

        // THEN
        expect(certificateServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(certificateServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
