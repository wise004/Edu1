import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import CourseItem from './course-item.vue';
import CourseItemService from './course-item.service';
import AlertService from '@/shared/alert/alert.service';

type CourseItemComponentType = InstanceType<typeof CourseItem>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('CourseItem Management Component', () => {
    let courseItemServiceStub: SinonStubbedInstance<CourseItemService>;
    let mountOptions: MountingOptions<CourseItemComponentType>['global'];

    beforeEach(() => {
      courseItemServiceStub = sinon.createStubInstance<CourseItemService>(CourseItemService);
      courseItemServiceStub.retrieve.resolves({ headers: {} });

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
          courseItemService: () => courseItemServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        courseItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(CourseItem, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(courseItemServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.courseItems[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CourseItemComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(CourseItem, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        courseItemServiceStub.retrieve.reset();
        courseItemServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        courseItemServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCourseItem();
        await comp.$nextTick(); // clear components

        // THEN
        expect(courseItemServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(courseItemServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
