import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    accessToken: null,
  }),
  actions: {
    setAccessToken(token) {
      this.accessToken = token;
    },
    clearAccessToken() {
      this.accessToken = null;
    },
  },
});
