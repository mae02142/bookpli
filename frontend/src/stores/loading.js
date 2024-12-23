import { defineStore } from "pinia";

export const useLoadingStore = defineStore('loading', {
    state : () => ({
        isLoading: false, // 로딩 상태
    }),
    actions : {
        startLoading() {
            this.isLoading = true;
        },
        stopLoading() {
            this.isLoading = false;
        },
    },
});