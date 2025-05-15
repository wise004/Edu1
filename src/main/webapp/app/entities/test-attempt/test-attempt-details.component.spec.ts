import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TestAttemptDetails from './test-attempt-details.vue';
import TestAttemptService from './test-attempt.service';
import AlertService from '@/shared/alert/alert.service';

type TestAttemptDetailsComponentType = InstanceType<typeof TestAttemptDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testAttemptSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TestAttempt Management Detail Component', () => {
    let testAttemptServiceStub: SinonStubbedInstance<TestAttemptService>;
    let mountOptions: MountingOptions<TestAttemptDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      testAttemptServiceStub = sinon.createStubInstance<TestAttemptService>(TestAttemptService);

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
          testAttemptService: () => testAttemptServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        testAttemptServiceStub.find.resolves(testAttemptSample);
        route = {
          params: {
            testAttemptId: `${123}`,
          },
        };
        const wrapper = shallowMount(TestAttemptDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.testAttempt).toMatchObject(testAttemptSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testAttemptServiceStub.find.resolves(testAttemptSample);
        const wrapper = shallowMount(TestAttemptDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
