<template>
    <div class="music-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 플레이리스트</h1>
        <hr />
        <div class="music-list">
            <div v-for="pli in recommendedPli.slice(0,5)" :key="pli.id" class="music-item" style="padding-top: 50px;">
                <div class="image-container">
                <img :src="pli.image" :alt="pli.title" class="music-image" />
                <button class="add-btn">+</button>
                </div>
                <p class="pli-title" style="font-size: 20px; padding-bottom: 5px;">{{ pli.title }}</p>
                <p class="pli-producer">{{ pli.artist }}</p>
            </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings" style="padding-top: 50px;">
        <h2 class="section-title">음악 순위</h2>
        <hr>
        <div class="rankings-container" style="width: 90%;">
        <!-- Domestic Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">국내</h3>
            <table class="ranking-table">
            <tr class="ranking-tr" v-for="(song, index) in domesticRankingPli.slice(0, 5)">
                <td style="width: 30px;">{{ index + 1 }}</td>
                <td><img :src="song.image" class="album-cover"></td>
                <td class="song-title">{{ song.title }}</td>
                <td>{{ song.artist }}</td>
            </tr>
            </table>
        </div>
        <!-- International Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">해외</h3>
            <table class="ranking-table">
            <tr class="ranking-tr" v-for="(song, index) in internationalRankingPli.slice(0, 5)">
                <td style="width: 30px;">{{ index + 1 }}</td>
                <td><img :src="song.image" class="album-cover"></td>
                <td class="song-title">{{ song.title }}</td>
                <td>{{ song.artist }}</td>
            </tr>
            </table>
        </div>
        </div>
    </section>
    </div>
</template>

<script>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useAuthStore } from '@/stores/auth';

export default {
    setup() {
        const authStore = useAuthStore();
        const recommendedPli = ref([]);
        const domesticRankingPli = ref([]);
        const internationalRankingPli = ref([]);
        
        const token = authStore.token; // Spotify API token from auth store
        const recommendPliApiUrl = "https://api.spotify.com/v1/search?q=book&type=playlist&include_external=audio";
        const domesticRankingsApiUrl = "https://api.spotify.com/v1/search?q=241205+top100+melon&type=playlist&include_external=audio";
        const internationalRankingsApiUrl = "https://api.spotify.com/v1/search?q=BillBoard+Hot+100&type=playlist";

        // Function to fetch recommended playlists
        const fetchRecommendedPlaylists = async () => {
            try {
                const response = await axios.get(recommendPliApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const playlists = response.data.playlists.items;

                // Filter out null or undefined playlists
                const filteredPlaylists = playlists.filter((playlist) => playlist !== null && playlist !== undefined);

                // Map the filtered playlists
                recommendedPli.value = filteredPlaylists.map((playlist) => ({
                    id: playlist.id,
                    title: playlist.name || "No Title", // Fallback for missing title
                    artist: playlist.owner.display_name || "Unknown Artist", // Fallback for missing artist
                    image: playlist.images[0]?.url || "default-image-url", // Fallback for missing image
                }));
            } catch (error) {
                console.error("Error fetching playlists:", error);
            }
        };

        // Domestic rankings data
        const fetchDomesticRanking = async () => {
            try {
                const response = await axios.get(domesticRankingsApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Get the first playlist
                const playlists = response.data.playlists.items;
                const firstPlaylistId = playlists[0]?.id;

                if (!firstPlaylistId) {
                    console.error("No playlist found.");
                    return;
                }

                // Fetch playlist details to get tracks
                const playlistDetailsUrl = `https://api.spotify.com/v1/playlists/${firstPlaylistId}`;
                const playlistResponse = await axios.get(playlistDetailsUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const tracks = playlistResponse.data.tracks.items;

                // Map the top 10 tracks to domesticRankings
                domesticRankingPli.value = tracks.slice(0, 10).map((track) => ({
                    title: track.track.name || "Unknown Title",
                    artist: track.track.artists.map(artist => artist.name).join(", ") || "Unknown Artist",
                    album: track.track.album.name || "Unknown Album",
                    image: track.track.album.images[0]?.url || "default-image-url", // Fallback for missing image
                }));

                console.log("Top 10 Songs for Domestic Rankings:", domesticRankingPli.value);
            } catch (error) {
                console.error("Error fetching playlist or songs:", error);
            }
        };

        // Get the InternationalRanking from Spotify
        const fetchInternationalRanking = async () => {
            try {
                const responseint = await axios.get(internationalRankingsApiUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                // Get the first playlist
                const intplaylists = responseint.data.playlists.items;
                const intfirstPlaylistId = intplaylists[0]?.id;

                if (!intfirstPlaylistId) {
                    console.error("No playlist found.");
                    return;
                }

                // Fetch playlist details to get tracks
                const intplaylistDetailsUrl = `https://api.spotify.com/v1/playlists/${intfirstPlaylistId}`;
                const playlistResponse = await axios.get(intplaylistDetailsUrl, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const tracks = playlistResponse.data.tracks.items;

                // Map the top 10 tracks to internationalRanking
                internationalRankingPli.value = tracks.slice(0, 10).map((track) => ({
                    title: track.track.name || "Unknown Title",
                    artist: track.track.artists.map(artist => artist.name).join(", ") || "Unknown Artist",
                    album: track.track.album.name || "Unknown Album",
                    image: track.track.album.images[0]?.url || "default-image-url", // Fallback for missing image
                }));

                console.log("Top 10 Songs for International Rankings:", internationalRankingPli.value);
            } catch (error) {
                console.error("Error fetching playlist or songs:", error);
            }
        };

        // Fetch playlists on component mount
        onMounted(() => {
            console.log("회원 아이디 확인 : ", token);
            fetchRecommendedPlaylists();
            fetchDomesticRanking();
            fetchInternationalRanking();
        });

        return { recommendedPli, domesticRankingPli, internationalRankingPli };
    },
};

</script>

<style scoped>
/* General Styles */
body {
    font-family: Arial, sans-serif;

    background-color: #ffff;
}

.music-app {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0.5rem;
}

/* Recommendations Section */
.recommendations {
    margin: 2rem 0;
}

.section-title {
    font-size: 3rem;
    margin-bottom: 1rem;
}

.pli-title{
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    display: block;
}

.music-list {
    display: flex;
    gap: 1rem;
}

.music-item {
    margin: auto;
}

.music-image {
    width: 200px;
    height: 200px;
    object-fit: cover;
    margin-bottom: 0.5rem;
}

.image-container {
    position: relative;
}

.add-btn {
    background-color: #ffff;
    border: none;
    padding: 0.5rem;
    width: 32px;
    height: 32px;
    font-size: 18px;
    border-radius: 50%;
    cursor: pointer;
    position: absolute;
    bottom: 20px;
    right: 10px;
    z-index: 10;
}

/* Rankings Section */
.rankings-container {
    margin: auto;
    display: flex;
    justify-content: space-between;
}

.ranking {
    width: 45%;
}

.ranking-title {
    font-size: 1.7rem;
    margin-bottom: 1rem;
    vertical-align: middle;
}

.ranking-table {
    width: 100%;
    margin: auto;
    vertical-align: middle;
}

.ranking-tr {
    border-bottom: 1px solid black;
    height: 50px;
    vertical-align: middle;
}

.song-title {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    vertical-align: middle;
}
.album-cover {
    width: 60px;
    height: 60px;
}
</style>
