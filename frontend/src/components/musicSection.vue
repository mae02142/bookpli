<template>
    <div class="music-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 플레이리스트</h1>
        <hr />
        <div class="music-list">
        <div v-for="pli in recommendedPli" :key="pli.id" class="music-item" style="padding-top: 50px;">
            <div class="image-container">
            <img :src="pli.image" :alt="pli.title" class="music-image" />
            <button class="add-btn">+</button>
            </div>
            <p class="music-title" style="font-size: 20px; padding-bottom: 5px;">{{ pli.title }}</p>
            <p class="music-artist">{{ pli.artist }}</p>
        </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings" style="padding-top: 50px;">
        <h2 class="section-title">음악 순위</h2>
        <div class="rankings-container" style="width: 90%;">
        <!-- Domestic Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">국내</h3>
            <table class="ranking-table">
            <tr
                class="ranking-tr"
                v-for="song in domesticRankings"
                :key="song.rank"
                style="vertical-align: middle;"
            >
                <td>{{ song.rank }}</td>
                <td>{{ song.title }}</td>
                <td>{{ song.artist }}</td>
            </tr>
            </table>
        </div>
        <!-- International Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">해외</h3>
            <table class="ranking-table">
            <tr
                class="ranking-tr"
                v-for="song in internationalRankings"
                :key="song.rank"
                style="vertical-align: middle;"
            >
                <td>{{ song.rank }}</td>
                <td>{{ song.title }}</td>
                <td>{{ song.artist }}</td>
            </tr>
            </table>
        </div>
        </div>
    </section>
    </div>
</template>

<script>
import { ref } from "vue";
import Cjamm from "@/assets/Cjamm.webp";

export default {
    setup() {
    // Recommended playlists data
    const recommendedPli = ref([
        { id: 1, title: "Hip Hop", artist: "Various Artist", image: Cjamm },
        { id: 2, title: "Jazz Vibes", artist: "Various Artist", image: Cjamm },
        { id: 3, title: "Chill Beats", artist: "Various Artist", image: Cjamm },
        { id: 4, title: "Rock Classics", artist: "Various Artist", image: Cjamm },
        { id: 5, title: "Pop Essentials", artist: "Various Artist", image: Cjamm },
    ]);

    // Domestic rankings data
    const domesticRankings = ref([
        { rank: 1, title: "LOVE LOVE LOVE", artist: "Epik High" },
        { rank: 2, title: "Spring Day", artist: "BTS" },
        { rank: 3, title: "Blueming", artist: "IU" },
        { rank: 4, title: "Celebrity", artist: "IU" },
        { rank: 5, title: "Permission to Dance", artist: "BTS" },
    ]);

    // International rankings data
    const internationalRankings = ref([
        { rank: 1, title: "Blinding Lights", artist: "The Weeknd" },
        { rank: 2, title: "As It Was", artist: "Harry Styles" },
        { rank: 3, title: "Drivers License", artist: "Olivia Rodrigo" },
        { rank: 4, title: "Levitating", artist: "Dua Lipa" },
        { rank: 5, title: "Save Your Tears", artist: "The Weeknd" },
    ]);

    return { recommendedPli, domesticRankings, internationalRankings };
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
}

.ranking-table {
    width: 100%;
    margin: auto;
    vertical-align: middle;
}

.ranking-tr {
    border-bottom: 1px solid black;
    height: 50px;
}
</style>
