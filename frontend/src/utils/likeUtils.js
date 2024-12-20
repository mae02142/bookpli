import apiClient from "@/api/axiosInstance";

// 책 좋아요 기능
export const addBookLike = async (userId, book) => {
  try {
    const response = await apiClient.post(`/api/library/${userId}/book`, book);
    return response.data.data;
  } catch (error) {
    console.error("좋아요 실패:", error);
    throw new Error("좋아요 추가 요청 중 오류가 발생했습니다.");
  }
};

// 책 좋아요 해제 기능
export const removeBookLike = async (bookLikeId) => {
  try {
    await apiClient.delete(`/api/library/book-like/${bookLikeId}`);
    return true; // 성공 시 true 반환
  } catch (error) {
    console.error("좋아요 해제 실패:", error);
    throw new Error("좋아요 해제 요청 중 오류가 발생했습니다.");
  }
};
