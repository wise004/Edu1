import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import LessonProgressUpdate from './lesson-progress-update.vue';
import LessonProgressService from './lesson-progress.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CourseItemService from '@/entities/course-item/course-item.service';

type LessonProgressUpdateComponentType = InstanceType<typeof LessonProgressUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const lessonProgressSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<LessonProgressUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('LessonProgress Management Update Component', () => {
    let comp: LessonProgressUpdateComponentType;
    let lessonProgressServiceStub: SinonStubbedInstance<LessonProgressService>;

    beforeEach(() => {
      route = {};
      lessonProgressServiceStub = sinon.createStubInstance<LessonProgressService>(LessonProgressService);
      lessonProgressServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          lessonProgressService: () => lessonProgressServiceStub,

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
        const wrapper = shallowMount(LessonProgressUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(LessonProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.lessonProgress = lessonProgressSample;
        lessonProgressServiceStub.update.resolves(lessonProgressSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(lessonProgressServiceStub.update.calledWith(lessonProgressSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        lessonProgressServiceStub.create.resolves(entity);
        const wrapper = shallowMount(LessonProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.lessonProgress = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(lessonProgressServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        lessonProgressServiceStub.find.resolves(lessonProgressSample);
        lessonProgressServiceStub.retrieve.resolves([lessonProgressSample]);

        // WHEN
        route = {
          params: {
            lessonProgressId: `${lessonProgressSample.id}`,
          },
        };
        const wrapper = shallowMount(LessonProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.lessonProgress).toMatchObject(lessonProgressSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        lessonProgressServiceStub.find.resolves(lessonProgressSample);
        const wrapper = shallowMount(LessonProgressUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
