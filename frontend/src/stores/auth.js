import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: null, // JWT 토큰
    user: null, // 사용자 정보
  }),
  getters: {
    // 사용자 로그인 확인 용도
    isAuthenticated: (state) => !!state.token, // token이 있으면 로그인된 상태
  },
  actions: {
    // 인증 데이터 설정 (토큰과 사용자 정보)
    setUser(token, user) {
      this.token = token;
      this.user = user;
    },
    clearAuthData() {
      this.token = null;
      this.user = null;
      localStorage.removeItem("auth");
    },
    // 인증 헤더 가져오기 (API 요청 시 사용)
    getAuthHeader() {
      if (!this.token) {
        throw new Error("인증 토큰이 없습니다.");
      }
      return { Authorization: `Bearer ${this.token}` };
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: "auth", // 로컬 스토리지에 저장될 키 이름
        storage: localStorage, // 저장 위치: 로컬 스토리지
        paths: ["token", "user"], // 유지하고 싶은 상태 지정
      },
    ],
  },
});
