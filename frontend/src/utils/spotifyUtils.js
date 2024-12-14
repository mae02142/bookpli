// Spotify API: 특정 플레이리스트의 트랙 가져오기
export const getPlaylistTracks = async (apiClient, playlistId) => {
  try {
    const response = await apiClient.get(`/api/mypli/playlist/${playlistId}`);
    return response.data.data.items.map((item) => {
      const track = item.track;
      return {
        id: track.id,
        name: track.name,
        album: track.album.name,
        albumId: track.album.id,
        artists: track.artists.map((artist) => artist.name).join(", "),
        duration: track.duration_ms,
        albumCover: track.album.images[0]?.url || "",
      };
    });
  } catch (error) {
    console.error("트랙 가져오기 실패:", error);
    return [];
  }
};

// 노래 삭제
export const deleteSongFromPlaylist = async (apiClient, playlistId, songId) => {
  const songUri = `spotify:track:${songId}`; // Spotify 트랙 URI 형식
  try {
    await apiClient.delete(`/api/mypli/playlist/${playlistId}/tracks`, {
      data: { uri: songUri },
    });
  } catch (error) {
    console.error("플레이리스트에서 노래 삭제 실패:", error);
    throw error;
  }
};

// 유틸 함수: 곡 길이 포맷
export const formatDuration = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000)
    .toString()
    .padStart(2, "0");
  return `${minutes}:${seconds}`;
};
