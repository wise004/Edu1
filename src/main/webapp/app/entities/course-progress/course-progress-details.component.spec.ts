import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CourseProgressDetails from './course-progress-details.vue';
import CourseProgressService from './course-progress.service';
import AlertService from '@/shared/alert/alert.service';

type CourseProgressDetailsComponentType = InstanceType<typeof CourseProgressDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseProgressSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CourseProgress Management Detail Component', () => {
    let courseProgressServiceStub: SinonStubbedInstance<CourseProgressService>;
    let mountOptions: MountingOptions<CourseProgressDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      courseProgressServiceStub = sinon.createStubInstance<CourseProgressService>(CourseProgressService);

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
          courseProgressService: () => courseProgressServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseProgressServiceStub.find.resolves(courseProgressSample);
        route = {
          params: {
            courseProgressId: `${123}`,
          },
        };
        const wrapper = shallowMount(CourseProgressDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.courseProgress).toMatchObject(courseProgressSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseProgressServiceStub.find.resolves(courseProgressSample);
        const wrapper = shallowMount(CourseProgressDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
