import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import QuestionService from './question.service';
import { type IQuestion } from '@/shared/model/question.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Question',
  setup() {
    const { t: t$ } = useI18n();
    const questionService = inject('questionService', () => new QuestionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const questions: Ref<IQuestion[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveQuestions = async () => {
      isFetching.value = true;
      try {
        const res = await questionService().retrieve();
        questions.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveQuestions();
    };

    onMounted(async () => {
      await retrieveQuestions();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IQuestion) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeQuestion = async () => {
      try {
        await questionService().delete(removeId.value);
        const message = t$('onlineCoursePlatformApp.question.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveQuestions();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      questions,
      handleSyncList,
      isFetching,
      retrieveQuestions,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeQuestion,
      t$,
    };
  },
});
