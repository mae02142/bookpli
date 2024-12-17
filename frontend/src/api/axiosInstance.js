import axios from "axios";
import router from "@/router";

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // API 기본 URL 설정
  withCredentials: true, // 쿠키 자동 포함
});

// Axios 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        console.error("접근 거부: 로그인 페이지로 이동합니다.");
        router.push({ path: "/login" });
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
