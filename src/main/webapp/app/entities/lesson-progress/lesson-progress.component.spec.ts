import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import LessonProgress from './lesson-progress.vue';
import LessonProgressService from './lesson-progress.service';
import AlertService from '@/shared/alert/alert.service';

type LessonProgressComponentType = InstanceType<typeof LessonProgress>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('LessonProgress Management Component', () => {
    let lessonProgressServiceStub: SinonStubbedInstance<LessonProgressService>;
    let mountOptions: MountingOptions<LessonProgressComponentType>['global'];

    beforeEach(() => {
      lessonProgressServiceStub = sinon.createStubInstance<LessonProgressService>(LessonProgressService);
      lessonProgressServiceStub.retrieve.resolves({ headers: {} });

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
          lessonProgressService: () => lessonProgressServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        lessonProgressServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(LessonProgress, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(lessonProgressServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.lessonProgresses[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: LessonProgressComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(LessonProgress, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        lessonProgressServiceStub.retrieve.reset();
        lessonProgressServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        lessonProgressServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeLessonProgress();
        await comp.$nextTick(); // clear components

        // THEN
        expect(lessonProgressServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(lessonProgressServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
