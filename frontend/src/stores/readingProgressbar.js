import { defineStore } from "pinia";

export const useProgressStore = defineStore("progress", {
    state: () => ({
        progressData: {}, // ISBN별 진행 데이터를 저장
    }),
    actions: {
        saveProgress(isbn13, data) {
            this.progressData[isbn13] = data; // 진행 데이터 저장
            // this.syncWithStorage(); // 저장 시 localStorage와 동기화
        },
        getProgress(isbn13) {
            return this.progressData[isbn13] || null; // 저장된 진행 데이터 반환
        },
        
    },
    persist: {
        key: "progressData", // localStorage에 저장될 키
        storage: localStorage, // 데이터를 저장할 스토리지 지정
    },
});
