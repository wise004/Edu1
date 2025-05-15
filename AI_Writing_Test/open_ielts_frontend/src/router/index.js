import { createRouter, createWebHistory } from 'vue-router';
import SubmitWriting from '../views/SubmitWriting.vue';
import History from '../views/History.vue';

const routes = [
  { path: '/', redirect: '/submit' },
  { path: '/submit', component: SubmitWriting },
  { path: '/history', component: History },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
