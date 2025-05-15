import { createRouter as createVueRouter, createWebHistory } from 'vue-router';
import LessonView from '@/entities/course-item/lesson-view.vue';
import TestView from '@/entities/course-item/test-view.vue';
const Home = () => import('@/core/home/home.vue');
const Error = () => import('@/core/error/error.vue');
import account from '@/router/account';
import admin from '@/router/admin';
import entities from '@/router/entities';
import pages from '@/router/pages';

export const createRouter = () =>
  createVueRouter({
    history: createWebHistory(),
    routes: [
      { path: '/', name: 'Home', component: Home },
      {
        path: '/course/:courseId/items',
        name: 'CourseItems',
        component: () => import('@/entities/course-item/course-items.vue'),
      },
      {
        path: '/course/:courseId/lesson/:itemId',
        name: 'LessonView',
        component: LessonView,
      },
      {
        path: '/course/:courseId/test/:itemId',
        name: 'TestView',
        component: TestView,
      },
      {
        path: '/forbidden',
        name: 'Forbidden',
        component: Error,
        meta: { error403: true },
      },
      {
        path: '/not-found',
        name: 'NotFound',
        component: Error,
        meta: { error404: true },
      },
      ...account,
      ...admin,
      entities,
      ...pages,
    ],
  });

const router = createRouter();

router.beforeResolve(async (to, from, next) => {
  if (!to.matched.length) {
    next({ path: '/not-found' });
    return;
  }
  next();
});

export default router;
