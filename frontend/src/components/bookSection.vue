<template>
    <div class="book-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 도서</h1>
        <hr>
        <div class="book-list">
            <div v-for="book in recommendedBooks" :key="book.id" class="book-item" style="padding-top: 50px;">
                <div class="image-container">
                    <img :src="book.image" :alt="book.title" class="book-image" />
                    <button class="add-btn" style="">+</button>
                </div>
                <p class="book-title" style="font-size: 20px; padding-bottom: 5px;">{{ book.title }}</p>
                <p class="book-author">{{ book.author }}</p>
            </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings" style="padding-top: 50px;">
        <h2 class="section-title">도서 순위</h2>

        <div class="rankings-container" style="width: 90%;">
        <!-- Domestic Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">국내</h3>
            <table class="ranking-table">
                <tr class="ranking-tr" v-for="book in domesticRankings" :key="book.rank" 
                style="vertical-align: middle;">
                    <td>{{ book.rank }}</td>
                    <td>{{ book.title }}</td>
                    <td>{{ book.author }}</td>
                </tr>
            </table>
        </div>
        <!-- International Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">해외</h3>
            <table class="ranking-table">
                <tr class="ranking-tr" v-for="book in internationalRankings" :key="book.rank" 
                style="vertical-align: middle;">
                    <td>{{ book.rank }}</td>
                    <td>{{ book.title }}</td>
                    <td>{{ book.author }}</td>
                </tr>
            </table>
        </div>
        </div>
    </section>
    </div>
</template>

<script>
import { ref } from "vue";
import 소년이온다 from "@/assets/소년이 온다.jpg"

export default {
    setup() {
    const coverEX = 소년이온다;

    // Recommended books data
    const recommendedBooks = ref([
        { id: 1, title: "소년이 온다", author: "한강", image: coverEX },
        { id: 2, title: "소년이 온다", author: "한강", image: coverEX },
        { id: 3, title: "소년이 온다", author: "한강", image: coverEX },
        { id: 4, title: "소년이 온다", author: "한강", image: coverEX },
        { id: 5, title: "소년이 온다", author: "한강", image: coverEX },
    ]);

    // Domestic rankings data
    const domesticRankings = ref([
        { rank: 1, title: "소년이 온다", author: "한강" },
        { rank: 2, title: "소년이 온다", author: "한강" },
        { rank: 3, title: "소년이 온다", author: "한강" },
        { rank: 4, title: "소년이 온다", author: "한강" },
        { rank: 5, title: "소년이 온다", author: "한강" },
    ]);

    // International rankings data
    const internationalRankings = ref([
        { rank: 1, title: "The Wild Robot", author: "Peter Brown" },
        { rank: 2, title: "The Wild Robot", author: "Peter Brown" },
        { rank: 3, title: "The Wild Robot", author: "Peter Brown" },
        { rank: 4, title: "The Wild Robot", author: "Peter Brown" },
        { rank: 5, title: "The Wild Robot", author: "Peter Brown" },
    ]);

    return { recommendedBooks, domesticRankings, internationalRankings };
    },
};
</script>

<style scoped>
/* General Styles */
body {
    font-family: Arial, sans-serif;
    background-color: #ffff;
}

.book-app {
    max-width: 1200px;
    margin: auto;
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

.book-list {
    display: flex;
    gap: 1rem;
}

.book-item {
    text-align: center;
    margin: auto;
}

.book-image {
    width: 150px;
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
    position: relative;
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
    width:100%;
    margin: auto;
    vertical-align: middle;

}

.ranking-tr {
    border-bottom: 1px solid black;
    height: 50px;
    table-layout: auto;
}

.ranking-list {
    table-layout: auto;
    padding: 0;
}

.ranking-list li {
    border-bottom: 1px solid #ffff;
    padding: 1rem 0;
}
</style>
