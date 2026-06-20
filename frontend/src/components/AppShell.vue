<template>
  <div class="app-shell">
    <aside class="sidebar glass-card tilt-card">
      <RouterLink class="brand-mini" :to="homePath">
        <span class="brand-orb"></span>
        <strong>拾光校园</strong>
      </RouterLink>
      <nav aria-label="主导航">
        <RouterLink v-for="link in links" :key="link.to" :to="link.to">
          <span>{{ link.icon }}</span>
          {{ link.label }}
        </RouterLink>
      </nav>
      <button class="logout-button" type="button" @click="auth.logout()">退出登录</button>
    </aside>

    <main class="shell-main">
      <header class="shell-top glass-card tilt-card">
        <div>
          <p>{{ subtitle }}</p>
          <h1>{{ title }}</h1>
        </div>
        <div class="user-pill tilt-card">
          <span>{{ auth.user?.realName }}</span>
          <strong>{{ auth.user?.role === 'admin' ? '管理员' : '学生' }}</strong>
        </div>
      </header>

      <section class="page-stage">
        <slot />
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: 'Campus Lost & Found'
  },
  role: {
    type: String,
    required: true
  }
});

const auth = useAuthStore();

const studentLinks = [
  { to: '/student', label: '失物寻回', icon: '◌' },
  { to: '/student/found-board', label: '拾物招领', icon: '✦' },
  { to: '/student/lost', label: '发布失物', icon: '!' },
  { to: '/student/found', label: '发布拾物', icon: '+' },
  { to: '/student/posts', label: '我的发布', icon: '#' },
  { to: '/student/claims', label: '认领记录', icon: '✓' },
  { to: '/student/messages', label: '消息中心', icon: '•' }
];

const adminLinks = [
  { to: '/admin', label: '管理首页', icon: '✦' },
  { to: '/admin/items', label: '物品管理', icon: '#' },
  { to: '/admin/audit', label: '审核中心', icon: '✓' },
  { to: '/admin/users', label: '用户管理', icon: '+' },
  { to: '/admin/categories', label: '分类管理', icon: '◇' },
  { to: '/admin/stats', label: '数据统计', icon: '↗' }
];

const links = computed(() => (props.role === 'admin' ? adminLinks : studentLinks));
const homePath = computed(() => (props.role === 'admin' ? '/admin' : '/student'));
</script>
