import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CourseProgress from './course-progress.vue';
import CourseProgressService from './course-progress.service';
import AlertService from '@/shared/alert/alert.service';

type CourseProgressComponentType = InstanceType<typeof CourseProgress>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('CourseProgress Management Component', () => {
    let courseProgressServiceStub: SinonStubbedInstance<CourseProgressService>;
    let mountOptions: MountingOptions<CourseProgressComponentType>['global'];

    beforeEach(() => {
      courseProgressServiceStub = sinon.createStubInstance<CourseProgressService>(CourseProgressService);
      courseProgressServiceStub.retrieve.resolves({ headers: {} });

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
          courseProgressService: () => courseProgressServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseProgressServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(CourseProgress, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(courseProgressServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.courseProgresses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CourseProgressComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(CourseProgress, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        courseProgressServiceStub.retrieve.reset();
        courseProgressServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        courseProgressServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCourseProgress();
        await comp.$nextTick(); // clear components

        // THEN
        expect(courseProgressServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(courseProgressServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
