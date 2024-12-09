<template>
    <div class="book-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 신간</h1>
        <hr>
        <div class="book-list">
            <div v-for="book in newBooks" :key="book.id" class="book-item" style="padding-top: 50px;">
                <div class="image-container" style="padding-bottom: 10px;">
                    <img :src="book.cover" :alt="book.title" class="book-image" />
                    <button class="add-btn" style="">+</button>
                </div>
                <p class="book-title" style="font-size: 20px; padding-bottom: 5px;">{{ book.title }}</p>
                <p class="book-author">{{ book.author.replace(/\(.*\)/, '') }}</p>
            </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings" style="padding-top: 30px;">
        <h2 class="section-title">도서 순위</h2>
        <hr>
        <div class="rankings-container" style="width: 90%;">
        <!-- Domestic Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">베스트셀러</h3>
            <table class="ranking-table">
                <tr class="ranking-tr" v-for="(book, index) in bestBooks" :key="book.rank" 
                style="vertical-align: middle;">
                    <td style="width: 30px;">{{ index + 1 }}</td>
                    <td>{{ book.title.replace(/\(.*\)|\s*[-–].*/g, '') }}</td>
                    <td>{{ book.author.replace(/\(.*\)|\s*[-–].*|,.*$/g, '').replace(/,/g, '...') }}</td>
                </tr>
            </table>
        </div>
        <!-- International Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">블로거 추천 도서</h3>
            <table class="ranking-table">
                <tr class="ranking-tr" v-for="(book, index) in blogBooks" :key="book.rank"
                style="vertical-align: middle;">
                    <td style="width: 30px;">{{ index + 1 }}</td>
                    <td>{{ book.title.replace(/\(.*\)|\s*[-–].*/g, '') }}</td>
                    <td>{{ book.author.replace(/\(.*\)|\s*[-–].*|,.*$/g, '').replace(/,/g, '...') }}</td>
                </tr>
            </table>
        </div>
        </div>
    </section>
    </div>
</template>

<script>
import { ref, onMounted } from "vue";


export default {
    setup() {


    const newBooks = ref([]);
    const bestBooks = ref([]);
    const blogBooks = ref([]);

    const apiUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    const ttbKey = "ttbyoungjae.bae1809001";

    // New Books
    const fetchNewBooksJSONP = () => {
        return new Promise((resolve, reject) => {
            const callbackName = `jsonpCallback_${Date.now()}`;
            const script = document.createElement("script");

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=ItemNewSpecial&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

            script.onerror = () => {
            reject(new Error("JSONP request failed"));
            document.body.removeChild(script);
            };

            window[callbackName] = (response) => {
            console.log("API Response:", response); // 추가된 로그
            resolve(response);
            delete window[callbackName];
            document.body.removeChild(script);
            };

            document.body.appendChild(script);
        });
    };

    const fetchNewBooks = async () => {
        try {
            const newBooksData = await fetchNewBooksJSONP();
            newBooks.value = newBooksData.item.map((book) => ({
                title: book.title,
                author: book.author,
                cover: book.cover,
            }));
        } catch (error) {
            console.error("Error fetching New Books data:", error);
        }
    };

    // Best Seller
    const fetchBestBooksJSONP = () => {
        return new Promise((resolve, reject) => {
            const callbackName = `jsonpCallback_${Date.now()}`;
            const script = document.createElement("script");

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=BestSeller&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

            script.onerror = () => {
            reject(new Error("JSONP request failed"));
            document.body.removeChild(script);
            };

            window[callbackName] = (response) => {
            console.log("API Response:", response); // 추가된 로그
            resolve(response);
            delete window[callbackName];
            document.body.removeChild(script);
            };

            document.body.appendChild(script);
        });
    };

    const fetchBestBooks = async () => {
        try {
            const bestBooksData = await fetchBestBooksJSONP();
            bestBooks.value = bestBooksData.item.map((book) => ({
                title: book.title,
                author: book.author,
                cover: book.cover,
            }));
        } catch (error) {
            console.error("Error fetching Best Books data:", error);
        }
    };

    // Blog Best Seller
    const fetchBlogBooksJSONP = () => {
        return new Promise((resolve, reject) => {
            const callbackName = `jsonpCallback_${Date.now()}`;
            const script = document.createElement("script");

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=BlogBest&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

            script.onerror = () => {
            reject(new Error("JSONP request failed"));
            document.body.removeChild(script);
            };

            window[callbackName] = (response) => {
            console.log("API Response:", response); // 추가된 로그
            resolve(response);
            delete window[callbackName];
            document.body.removeChild(script);
            };

            document.body.appendChild(script);
        });
    };

    const fetchBlogBooks = async () => {
        try {
            const blogBooksData = await fetchBlogBooksJSONP();
            blogBooks.value = blogBooksData.item.map((book) => ({
                title: book.title,
                author: book.author,
                cover: book.cover,
            }));
        } catch (error) {
            console.error("Error fetching Blog Books data:", error);
        }
    };

    onMounted(async () => {
        await fetchNewBooks();
        await fetchBestBooks();
        await fetchBlogBooks();
        console.log("New Books after fetch:", newBooks.value);
        console.log("Best Books after fetch:", bestBooks.value);
        console.log("Blog Books after fetch:", blogBooks.value);
    });

    return { newBooks, bestBooks, blogBooks };
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
    margin-bottom: 2rem;
    margin-top: 1rem;
}

.section-title {
    font-size: 3rem;
    margin-bottom: 1rem;
}

.book-title {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    display: block;
}

.book-author {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    display: block; 
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
</style>
