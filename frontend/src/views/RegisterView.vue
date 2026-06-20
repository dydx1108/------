<template>
  <main class="login-page auth-transition-page" :class="{ 'is-routing': isRouting }">
    <div class="login-card glass-card">
      <div class="login-art">
        <h1 class="art-title">拾光校园</h1>
      </div>

      <form class="login-form login-form--with-link" @submit.prevent="submitRegister">
        <h2>注册账号</h2>

        <label>
          用户名
          <input
            v-model.trim="form.username"
            autocomplete="username"
            placeholder="不少于 3 位"
            required
          />
        </label>

        <label>
          真实姓名
          <input
            v-model.trim="form.realName"
            autocomplete="name"
            placeholder="请输入姓名"
            required
          />
        </label>

        <label>
          手机号
          <input
            v-model.trim="form.phone"
            autocomplete="tel"
            placeholder="选填"
          />
        </label>

        <label>
          学院
          <input v-model.trim="form.college" placeholder="选填" />
        </label>

        <label>
          密码
          <input
            v-model="form.password"
            type="password"
            autocomplete="new-password"
            placeholder="不少于 6 位"
            required
          />
        </label>

        <label>
          确认密码
          <input
            v-model="confirmPassword"
            type="password"
            autocomplete="new-password"
            placeholder="请再次输入密码"
            required
          />
        </label>

        <p v-if="error" class="form-error">{{ error }}</p>
        <p v-if="success" class="form-success">{{ success }}</p>

        <button class="submit-button" type="submit" :disabled="loading">
          {{ loading ? '注册中...' : '立即注册' }}
        </button>

        <button type="button" class="corner-link link-button" @click="goLogin">返回登录</button>
      </form>
    </div>
    <div class="auth-burst" aria-hidden="true"></div>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { api } from '../services/api';
import { playPageTransition } from '../utils/pageTransition';

const router = useRouter();
const loading = ref(false);
const error = ref('');
const success = ref('');
const confirmPassword = ref('');
const isRouting = ref(false);
const form = reactive({
  username: '',
  realName: '',
  phone: '',
  college: '',
  password: ''
});

function goLogin() {
  if (isRouting.value) {
    return;
  }
  isRouting.value = true;
  playPageTransition(() => router.push('/login'));
}

async function submitRegister() {
  error.value = '';
  success.value = '';

  if (form.username.length < 3) {
    error.value = '用户名至少 3 位。';
    return;
  }

  if (form.realName.trim().length < 2) {
    error.value = '请输入正确的姓名。';
    return;
  }

  if (form.password.length < 6) {
    error.value = '密码至少 6 位。';
    return;
  }

  if (form.password !== confirmPassword.value) {
    error.value = '两次输入的密码不一致。';
    return;
  }

  try {
    loading.value = true;
    await api.post('/auth/register', form);
    success.value = '注册成功，正在跳转到登录页...';
    isRouting.value = true;
    window.setTimeout(() => {
      playPageTransition(() => router.push('/login'));
    }, 800);
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

.form-success {
  margin: 0;
  color: #d6ffd1;
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
