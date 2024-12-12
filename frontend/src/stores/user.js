import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        accessToken: null,
    }),
    actions: {
        setAccessToken(token) {
            this.accessToken = token
            console.log("토큰을 전달하다")
        }
    }
})
