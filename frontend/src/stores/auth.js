import { defineStore } from 'pinia';
import { api } from '../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('campus_token') || '',
    user: JSON.parse(localStorage.getItem('campus_user') || 'null')
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token && state.user),
    isAdmin: (state) => state.user?.role === 'admin',
    homePath: (state) => (state.user?.role === 'admin' ? '/admin' : '/student')
  },
  actions: {
    async login(payload) {
      const result = await api.post('/auth/login', payload);
      this.token = result.data.token;
      this.user = result.data.user;
      localStorage.setItem('campus_token', this.token);
      localStorage.setItem('campus_user', JSON.stringify(this.user));
      return result.data.user;
    },
    logout(redirect = true) {
      this.token = '';
      this.user = null;
      localStorage.removeItem('campus_token');
      localStorage.removeItem('campus_user');
      if (redirect) {
        window.location.href = '/login';
      }
    }
  }
});
