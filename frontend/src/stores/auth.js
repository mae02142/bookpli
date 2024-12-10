import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null, // 사용자 정보
  }),
  getters: {
    // 사용자 로그인 확인 용도
    isAuthenticated: (state) => !!state.user,
  },
  actions: {
    // 인증 데이터 설정 (토큰과 사용자 정보)
    setUser(user) {
      this.user = user;
    },
    clearAuthData() {
      this.user = null;
      localStorage.removeItem("auth");
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: "auth", // 로컬 스토리지에 저장될 키 이름
        storage: localStorage, // 저장 위치: 로컬 스토리지
        paths: ["user"], // 유지하고 싶은 상태 지정
      },
    ],
  },
});
