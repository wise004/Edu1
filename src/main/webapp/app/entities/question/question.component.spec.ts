import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Question from './question.vue';
import QuestionService from './question.service';
import AlertService from '@/shared/alert/alert.service';

type QuestionComponentType = InstanceType<typeof Question>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Question Management Component', () => {
    let questionServiceStub: SinonStubbedInstance<QuestionService>;
    let mountOptions: MountingOptions<QuestionComponentType>['global'];

    beforeEach(() => {
      questionServiceStub = sinon.createStubInstance<QuestionService>(QuestionService);
      questionServiceStub.retrieve.resolves({ headers: {} });

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
          questionService: () => questionServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        questionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Question, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(questionServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.questions[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: QuestionComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Question, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        questionServiceStub.retrieve.reset();
        questionServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        questionServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeQuestion();
        await comp.$nextTick(); // clear components

        // THEN
        expect(questionServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(questionServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
