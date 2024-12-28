import { defineStore } from "pinia";

export const useProgressStore = defineStore("progress", {
    state: () => ({
        progressData: {}, // ISBN별 진행 데이터를 저장
    }),
    actions: {
        saveProgress(isbn13, data) {
            this.progressData[isbn13] = data; // 진행 데이터 저장
        
        },
        getProgress(isbn13) {
            return this.progressData[isbn13] || { progressPercentage: 0, currentPage: 0 }; // 저장된 진행 데이터 반환
        },
        clearProgress(isbn13){
            delete this.rogressData[isbn13];
        },
        updateProgress(isbn13, percentage, currentPage) {
            if (this.progressData[isbn13]) {
                this.progressData[isbn13].progressPercentage = percentage;
                this.progressData[isbn13].currentPage = currentPage;
            }
        },
    },
    persist: {
        key: "progressData", // localStorage에 저장될 키
        storage: localStorage, // 데이터를 저장할 스토리지 지정
    },
});
