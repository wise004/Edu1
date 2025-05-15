import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TestAttemptUpdate from './test-attempt-update.vue';
import TestAttemptService from './test-attempt.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CourseItemService from '@/entities/course-item/course-item.service';

type TestAttemptUpdateComponentType = InstanceType<typeof TestAttemptUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testAttemptSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TestAttemptUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TestAttempt Management Update Component', () => {
    let comp: TestAttemptUpdateComponentType;
    let testAttemptServiceStub: SinonStubbedInstance<TestAttemptService>;

    beforeEach(() => {
      route = {};
      testAttemptServiceStub = sinon.createStubInstance<TestAttemptService>(TestAttemptService);
      testAttemptServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          testAttemptService: () => testAttemptServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          courseItemService: () =>
            sinon.createStubInstance<CourseItemService>(CourseItemService, {
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
        const wrapper = shallowMount(TestAttemptUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TestAttemptUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testAttempt = testAttemptSample;
        testAttemptServiceStub.update.resolves(testAttemptSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testAttemptServiceStub.update.calledWith(testAttemptSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        testAttemptServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TestAttemptUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testAttempt = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testAttemptServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        testAttemptServiceStub.find.resolves(testAttemptSample);
        testAttemptServiceStub.retrieve.resolves([testAttemptSample]);

        // WHEN
        route = {
          params: {
            testAttemptId: `${testAttemptSample.id}`,
          },
        };
        const wrapper = shallowMount(TestAttemptUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.testAttempt).toMatchObject(testAttemptSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testAttemptServiceStub.find.resolves(testAttemptSample);
        const wrapper = shallowMount(TestAttemptUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
