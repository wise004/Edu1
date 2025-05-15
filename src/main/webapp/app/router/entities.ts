import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Certificate = () => import('@/entities/certificate/certificate.vue');
const CertificateUpdate = () => import('@/entities/certificate/certificate-update.vue');
const CertificateDetails = () => import('@/entities/certificate/certificate-details.vue');

const Course = () => import('@/entities/course/course.vue');
const CourseUpdate = () => import('@/entities/course/course-update.vue');
const CourseDetails = () => import('@/entities/course/course-details.vue');
const CourseItems = () => import('@/entities/course-item/CourseItems.vue'); // Add this

const CourseItem = () => import('@/entities/course-item/course-item.vue');
const CourseItemUpdate = () => import('@/entities/course-item/course-item-update.vue');
const CourseItemDetails = () => import('@/entities/course-item/course-item-details.vue');

const CourseProgress = () => import('@/entities/course-progress/course-progress.vue');
const CourseProgressUpdate = () => import('@/entities/course-progress/course-progress-update.vue');
const CourseProgressDetails = () => import('@/entities/course-progress/course-progress-details.vue');

const LessonProgress = () => import('@/entities/lesson-progress/lesson-progress.vue');
const LessonProgressUpdate = () => import('@/entities/lesson-progress/lesson-progress-update.vue');
const LessonProgressDetails = () => import('@/entities/lesson-progress/lesson-progress-details.vue');

const Question = () => import('@/entities/question/question.vue');
const QuestionUpdate = () => import('@/entities/question/question-update.vue');
const QuestionDetails = () => import('@/entities/question/question-details.vue');

const TestAttempt = () => import('@/entities/test-attempt/test-attempt.vue');
const TestAttemptUpdate = () => import('@/entities/test-attempt/test-attempt-update.vue');
const TestAttemptDetails = () => import('@/entities/test-attempt/test-attempt-details.vue');

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'certificate',
      name: 'Certificate',
      component: Certificate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'certificate/new',
      name: 'CertificateCreate',
      component: CertificateUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'certificate/:certificateId/edit',
      name: 'CertificateEdit',
      component: CertificateUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'certificate/:certificateId/view',
      name: 'CertificateView',
      component: CertificateDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course',
      name: 'Course',
      component: Course,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/new',
      name: 'CourseCreate',
      component: CourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/:courseId/edit',
      name: 'CourseEdit',
      component: CourseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/:courseId/view',
      name: 'CourseView',
      component: CourseDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course/:courseId/items', // Add this route
      name: 'CourseItems',
      component: CourseItems,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-item',
      name: 'CourseItem',
      component: CourseItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-item/new',
      name: 'CourseItemCreate',
      component: CourseItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-item/:courseItemId/edit',
      name: 'CourseItemEdit',
      component: CourseItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-item/:courseItemId/view',
      name: 'CourseItemView',
      component: CourseItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-progress',
      name: 'CourseProgress',
      component: CourseProgress,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-progress/new',
      name: 'CourseProgressCreate',
      component: CourseProgressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-progress/:courseProgressId/edit',
      name: 'CourseProgressEdit',
      component: CourseProgressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'course-progress/:courseProgressId/view',
      name: 'CourseProgressView',
      component: CourseProgressDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'lesson-progress',
      name: 'LessonProgress',
      component: LessonProgress,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'lesson-progress/new',
      name: 'LessonProgressCreate',
      component: LessonProgressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'lesson-progress/:lessonProgressId/edit',
      name: 'LessonProgressEdit',
      component: LessonProgressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'lesson-progress/:lessonProgressId/view',
      name: 'LessonProgressView',
      component: LessonProgressDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question',
      name: 'Question',
      component: Question,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/new',
      name: 'QuestionCreate',
      component: QuestionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/:questionId/edit',
      name: 'QuestionEdit',
      component: QuestionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/:questionId/view',
      name: 'QuestionView',
      component: QuestionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-attempt',
      name: 'TestAttempt',
      component: TestAttempt,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-attempt/new',
      name: 'TestAttemptCreate',
      component: TestAttemptUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-attempt/:testAttemptId/edit',
      name: 'TestAttemptEdit',
      component: TestAttemptUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-attempt/:testAttemptId/view',
      name: 'TestAttemptView',
      component: TestAttemptDetails,
      meta: { authorities: [Authority.USER] },
    },
  ],
};
