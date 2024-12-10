import axios from "axios";

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // API 기본 URL 설정
  withCredentials: true, // 쿠키 자동 포함
});

// 예외 처리 및 공통 헤더 추가 (선택)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

export default apiClient;
