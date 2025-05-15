import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CourseProgressUpdate from './course-progress-update.vue';
import CourseProgressService from './course-progress.service';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CourseService from '@/entities/course/course.service';

type CourseProgressUpdateComponentType = InstanceType<typeof CourseProgressUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const courseProgressSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CourseProgressUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('CourseProgress Management Update Component', () => {
    let comp: CourseProgressUpdateComponentType;
    let courseProgressServiceStub: SinonStubbedInstance<CourseProgressService>;

    beforeEach(() => {
      route = {};
      courseProgressServiceStub = sinon.createStubInstance<CourseProgressService>(CourseProgressService);
      courseProgressServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          courseProgressService: () => courseProgressServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
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
        const wrapper = shallowMount(CourseProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.courseProgress = courseProgressSample;
        courseProgressServiceStub.update.resolves(courseProgressSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseProgressServiceStub.update.calledWith(courseProgressSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        courseProgressServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CourseProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.courseProgress = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(courseProgressServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        courseProgressServiceStub.find.resolves(courseProgressSample);
        courseProgressServiceStub.retrieve.resolves([courseProgressSample]);

        // WHEN
        route = {
          params: {
            courseProgressId: `${courseProgressSample.id}`,
          },
        };
        const wrapper = shallowMount(CourseProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.courseProgress).toMatchObject(courseProgressSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        courseProgressServiceStub.find.resolves(courseProgressSample);
        const wrapper = shallowMount(CourseProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
