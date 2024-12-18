import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null, // 사용자 정보
    isAuthenticated: false, // 인증 여부
  }),
  actions: {
    setUser(user) {
      this.user = user;
      this.isAuthenticated = !!user; // user가 존재하면 true
    },
    clearAuthData() {
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem("auth");
    },
    loadAuthData() {
      const storedData = localStorage.getItem("auth");
      if (storedData) {
        this.user = JSON.parse(storedData);
        this.isAuthenticated = !!this.user;
      }
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: "auth",
        storage: localStorage,
        paths: ["user"],
      },
    ],
  },
});
