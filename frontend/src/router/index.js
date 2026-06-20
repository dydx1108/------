import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import AdminAudit from '../views/admin/AdminAudit.vue';
import AdminCategories from '../views/admin/AdminCategories.vue';
import AdminDashboard from '../views/admin/AdminDashboard.vue';
import AdminItems from '../views/admin/AdminItems.vue';
import AdminStats from '../views/admin/AdminStats.vue';
import AdminUsers from '../views/admin/AdminUsers.vue';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';
import ClaimRecords from '../views/student/ClaimRecords.vue';
import ItemDetail from '../views/student/ItemDetail.vue';
import LostForm from '../views/student/LostForm.vue';
import MessagesView from '../views/student/MessagesView.vue';
import MyPosts from '../views/student/MyPosts.vue';
import StudentDashboard from '../views/student/StudentDashboard.vue';

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
  {
    path: '/student',
    component: StudentDashboard,
    meta: { requiresAuth: true, roles: ['student'], boardType: 'lost' }
  },
  {
    path: '/student/found-board',
    component: StudentDashboard,
    meta: { requiresAuth: true, roles: ['student'], boardType: 'found' }
  },
  {
    path: '/student/lost',
    component: LostForm,
    meta: { requiresAuth: true, roles: ['student'], itemType: 'lost' }
  },
  {
    path: '/student/found',
    component: LostForm,
    meta: { requiresAuth: true, roles: ['student'], itemType: 'found' }
  },
  {
    path: '/student/posts',
    component: MyPosts,
    meta: { requiresAuth: true, roles: ['student'] }
  },
  {
    path: '/student/claims',
    component: ClaimRecords,
    meta: { requiresAuth: true, roles: ['student'] }
  },
  {
    path: '/student/messages',
    component: MessagesView,
    meta: { requiresAuth: true, roles: ['student'] }
  },
  {
    path: '/student/items/:id',
    component: ItemDetail,
    meta: { requiresAuth: true, roles: ['student', 'admin'] }
  },
  {
    path: '/admin',
    component: AdminDashboard,
    meta: { requiresAuth: true, roles: ['admin'] }
  },
  {
    path: '/admin/items',
    component: AdminItems,
    meta: { requiresAuth: true, roles: ['admin'] }
  },
  {
    path: '/admin/audit',
    component: AdminAudit,
    meta: { requiresAuth: true, roles: ['admin'] }
  },
  {
    path: '/admin/users',
    component: AdminUsers,
    meta: { requiresAuth: true, roles: ['admin'] }
  },
  {
    path: '/admin/categories',
    component: AdminCategories,
    meta: { requiresAuth: true, roles: ['admin'] }
  },
  {
    path: '/admin/stats',
    component: AdminStats,
    meta: { requiresAuth: true, roles: ['admin'] }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0, behavior: 'smooth' };
  }
});

router.beforeEach((to) => {
  const auth = useAuthStore();

  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return '/login';
  }

  if (to.meta.roles?.length && !to.meta.roles.includes(auth.user?.role)) {
    return auth.isLoggedIn ? auth.homePath : '/login';
  }

  if ((to.path === '/login' || to.path === '/register') && auth.isLoggedIn) {
    return auth.homePath;
  }

  return true;
});

export default router;
