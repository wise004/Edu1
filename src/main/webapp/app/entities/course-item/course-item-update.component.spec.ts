import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CourseItemUpdate from './course-item-update.vue';
import CourseItemService from './course-item.service';
import AlertService from '@/shared/alert/alert.service';

import CourseService from '@/entities/course/course.service';

type CourseItemUpdateComponentType = InstanceType<typeof CourseItemUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseItemSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CourseItemUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CourseItem Management Update Component', () => {
    let comp: CourseItemUpdateComponentType;
    let courseItemServiceStub: SinonStubbedInstance<CourseItemService>;

    beforeEach(() => {
      route = {};
      courseItemServiceStub = sinon.createStubInstance<CourseItemService>(CourseItemService);
      courseItemServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          courseItemService: () => courseItemServiceStub,
          courseService: () =>
            sinon.createStubInstance<CourseService>(CourseService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CourseItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.courseItem = courseItemSample;
        courseItemServiceStub.update.resolves(courseItemSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseItemServiceStub.update.calledWith(courseItemSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        courseItemServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CourseItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.courseItem = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        courseItemServiceStub.find.resolves(courseItemSample);
        courseItemServiceStub.retrieve.resolves([courseItemSample]);

        // WHEN
        route = {
          params: {
            courseItemId: `${courseItemSample.id}`,
          },
        };
        const wrapper = shallowMount(CourseItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.courseItem).toMatchObject(courseItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseItemServiceStub.find.resolves(courseItemSample);
        const wrapper = shallowMount(CourseItemUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
