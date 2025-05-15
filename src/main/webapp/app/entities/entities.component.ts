import { defineComponent, provide } from 'vue';

import CertificateService from './certificate/certificate.service';
import CourseService from './course/course.service';
import CourseItemService from './course-item/course-item.service';
import CourseProgressService from './course-progress/course-progress.service';
import LessonProgressService from './lesson-progress/lesson-progress.service';
import QuestionService from './question/question.service';
import TestAttemptService from './test-attempt/test-attempt.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('certificateService', () => new CertificateService());
    provide('courseService', () => new CourseService());
    provide('courseItemService', () => new CourseItemService());
    provide('courseProgressService', () => new CourseProgressService());
    provide('lessonProgressService', () => new LessonProgressService());
    provide('questionService', () => new QuestionService());
    provide('testAttemptService', () => new TestAttemptService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
