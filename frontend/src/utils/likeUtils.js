// 책 좋아요 기능
export const addBookLike = async (apiClient, userId, isbn13) => {
  try {
    const response = await apiClient.post(
      `/api/library/${userId}/book/${isbn13}`
    );
    return response.data.data;
  } catch (error) {
    console.error("좋아요 실패:", error);
    throw new Error("좋아요 추가 요청 중 오류가 발생했습니다."); // 호출자에게 에러 전달
  }
};

// 책 좋아요 해제 기능
export const removeBookLike = async (apiClient, bookLikeId) => {
  try {
    await apiClient.delete(`/api/library/book-like/${bookLikeId}`);
    return true; // 성공 시 true 반환
  } catch (error) {
    console.error("좋아요 해제 실패:", error);
    throw new Error("좋아요 해제 요청 중 오류가 발생했습니다.");
  }
};
