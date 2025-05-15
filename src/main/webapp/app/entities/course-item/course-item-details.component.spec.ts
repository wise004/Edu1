import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CourseItemDetails from './course-item-details.vue';
import CourseItemService from './course-item.service';
import AlertService from '@/shared/alert/alert.service';

type CourseItemDetailsComponentType = InstanceType<typeof CourseItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseItemSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CourseItem Management Detail Component', () => {
    let courseItemServiceStub: SinonStubbedInstance<CourseItemService>;
    let mountOptions: MountingOptions<CourseItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      courseItemServiceStub = sinon.createStubInstance<CourseItemService>(CourseItemService);

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
          courseItemService: () => courseItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseItemServiceStub.find.resolves(courseItemSample);
        route = {
          params: {
            courseItemId: `${123}`,
          },
        };
        const wrapper = shallowMount(CourseItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.courseItem).toMatchObject(courseItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseItemServiceStub.find.resolves(courseItemSample);
        const wrapper = shallowMount(CourseItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
