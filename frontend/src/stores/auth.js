import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null,
    user: null,
  }),
  actions: {
    setToken(token) {
      this.token = token;
    },
    setUser(user) {
      this.user = user;
    },
    clearAuth() {
      this.token = null;
      this.user = null;
    },
  },
  getters: {
    isAuthenticated: (state) => !!state.user, // 사용자 정보가 있으면 로그인 상태
  },
});
