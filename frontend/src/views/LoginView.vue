<template>
  <main class="login-page auth-transition-page" :class="{ 'is-routing': isRouting }">
    <div class="login-card glass-card">
      <div class="login-art">
        <h1 class="art-title">拾光校园</h1>
      </div>

      <form class="login-form login-form--with-link" @submit.prevent="submitLogin">
        <h2>登录平台</h2>

        <label>
          用户名
          <input
            v-model.trim="form.username"
            autocomplete="username"
            placeholder="请输入用户名"
            required
          />
        </label>

        <label>
          密码
          <input
            v-model="form.password"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
            required
          />
        </label>

        <p v-if="error" class="form-error">{{ error }}</p>
        <button class="submit-button" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '立即登录' }}
        </button>

        <button type="button" class="corner-link link-button" @click="goRegister">去注册</button>
      </form>
    </div>
    <div class="auth-burst" aria-hidden="true"></div>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { playPageTransition } from '../utils/pageTransition';

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const error = ref('');
const isRouting = ref(false);
const form = reactive({ username: '', password: '' });

function goRegister() {
  if (isRouting.value) {
    return;
  }
  isRouting.value = true;
  playPageTransition(() => router.push('/register'));
}

async function submitLogin() {
  error.value = '';

  if (form.username.length < 3 || form.password.length < 6) {
    error.value = '用户名至少 3 位，密码至少 6 位。';
    return;
  }

  try {
    loading.value = true;
    const user = await auth.login(form);
    isRouting.value = true;
    playPageTransition(() => router.push(user.role === 'admin' ? '/admin' : '/student'));
  } catch (err) {
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-art {
  justify-content: flex-end;
}

.art-title {
  margin: 0;
  font-size: clamp(3rem, 6vw, 4.6rem);
  line-height: 1.08;
  letter-spacing: 0.06em;
}

.login-form--with-link {
  position: relative;
  align-content: center;
  padding-bottom: 76px;
  transition: transform 0.52s ease, filter 0.52s ease, opacity 0.52s ease;
}

.corner-link {
  position: absolute;
  right: clamp(28px, 6vw, 64px);
  bottom: 28px;
  font-size: 1rem;
  font-weight: 600;
  color: #f6e6a8;
  text-decoration: none;
}

.link-button {
  border: 0;
  cursor: pointer;
  background: transparent;
}

.corner-link:hover {
  text-decoration: underline;
}

.auth-transition-page.is-routing .login-card {
  animation: authCardBurst 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.auth-transition-page.is-routing .login-art,
.auth-transition-page.is-routing .login-form {
  animation: authHalfCollapse 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.auth-transition-page.is-routing .auth-burst {
  animation: authBurst 0.72s cubic-bezier(0.18, 0.9, 0.22, 1.02) both;
}

.auth-burst {
  position: fixed;
  inset: 0;
  pointer-events: none;
  opacity: 0;
  background: radial-gradient(circle at center, rgba(246, 196, 83, 0.28), rgba(31, 157, 136, 0.24) 34%, transparent 70%);
}

@keyframes authCardBurst {
  from {
    opacity: 1;
    transform: scale(1) rotateX(0);
    filter: blur(0);
  }
  to {
    opacity: 0;
    transform: scale(1.08) rotateX(10deg);
    filter: blur(12px);
  }
}

@keyframes authHalfCollapse {
  from {
    opacity: 1;
    transform: translate3d(0, 0, 0) scale(1);
  }
  to {
    opacity: 0;
    transform: translate3d(0, 0, 0) scale(0.86);
  }
}

@keyframes authBurst {
  from {
    opacity: 0;
    transform: scale(0.4);
  }
  30% {
    opacity: 1;
  }
  to {
    opacity: 0;
    transform: scale(1.4);
  }
}
</style>
