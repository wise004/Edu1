import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import CertificateUpdate from './certificate-update.vue';
import CertificateService from './certificate.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import CourseProgressService from '@/entities/course-progress/course-progress.service';

type CertificateUpdateComponentType = InstanceType<typeof CertificateUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const certificateSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CertificateUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Certificate Management Update Component', () => {
    let comp: CertificateUpdateComponentType;
    let certificateServiceStub: SinonStubbedInstance<CertificateService>;

    beforeEach(() => {
      route = {};
      certificateServiceStub = sinon.createStubInstance<CertificateService>(CertificateService);
      certificateServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          certificateService: () => certificateServiceStub,
          courseProgressService: () =>
            sinon.createStubInstance<CourseProgressService>(CourseProgressService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(CertificateUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CertificateUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.certificate = certificateSample;
        certificateServiceStub.update.resolves(certificateSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(certificateServiceStub.update.calledWith(certificateSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        certificateServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CertificateUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.certificate = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(certificateServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        certificateServiceStub.find.resolves(certificateSample);
        certificateServiceStub.retrieve.resolves([certificateSample]);

        // WHEN
        route = {
          params: {
            certificateId: `${certificateSample.id}`,
          },
        };
        const wrapper = shallowMount(CertificateUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.certificate).toMatchObject(certificateSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        certificateServiceStub.find.resolves(certificateSample);
        const wrapper = shallowMount(CertificateUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
