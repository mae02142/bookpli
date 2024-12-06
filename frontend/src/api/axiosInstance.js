import axios from "axios";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();

// Spotify API 전용 Axios 인스턴스 생성
const spotifyApi = axios.create({
  baseURL: "https://api.spotify.com/v1", // Spotify API URL
  headers: {
    "Content-Type": "application/json",
  },
});

// 요청 인터셉터: Spotify API 요청에 토큰 추가
spotifyApi.interceptors.request.use(
  (config) => {
    const token = authStore.token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터: 401 발생 시 토큰 갱신 요청
spotifyApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        // 백엔드에 토큰 갱신 요청
        const refreshResponse = await axios.post(
          `${import.meta.env.VITE_API_URL}/api/auth/${authStore.user.spotifyId}` // 백엔드 API로 갱신 요청
        );

        // 새 엑세스 토큰 저장
        const newAccessToken = refreshResponse.data.data;
        authStore.token = newAccessToken;

        // 실패한 요청 다시 시도
        error.config.headers.Authorization = `Bearer ${newAccessToken}`;
        return spotifyApi(error.config); // 같은 인스턴스로 다시 요청
      } catch (refreshError) {
        console.error("토큰 갱신 실패:", refreshError);
        authStore.clearAuthData(); // 로그아웃 처리
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default spotifyApi;
