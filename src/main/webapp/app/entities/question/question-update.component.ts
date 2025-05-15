import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import QuestionService from './question.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CourseItemService from '@/entities/course-item/course-item.service';
import { type ICourseItem } from '@/shared/model/course-item.model';
import { type IQuestion, Question } from '@/shared/model/question.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'QuestionUpdate',
  setup() {
    const questionService = inject('questionService', () => new QuestionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const question: Ref<IQuestion> = ref(new Question());

    const courseItemService = inject('courseItemService', () => new CourseItemService());

    const courseItems: Ref<ICourseItem[]> = ref([]);
    const questionTypeValues: Ref<string[]> = ref(Object.keys(QuestionType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'uz-Latn-uz'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveQuestion = async questionId => {
      try {
        const res = await questionService().find(questionId);
        question.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.questionId) {
      retrieveQuestion(route.params.questionId);
    }

    const initRelationships = () => {
      courseItemService()
        .retrieve()
        .then(res => {
          courseItems.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      text: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      type: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      options: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      courseItem: {},
    };
    const v$ = useVuelidate(validationRules, question as any);
    v$.value.$validate();

    return {
      questionService,
      alertService,
      question,
      previousState,
      questionTypeValues,
      isSaving,
      currentLanguage,
      courseItems,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.question.id) {
        this.questionService()
          .update(this.question)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('onlineCoursePlatformApp.question.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.questionService()
          .create(this.question)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('onlineCoursePlatformApp.question.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
