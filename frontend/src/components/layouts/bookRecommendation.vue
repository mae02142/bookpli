<template>
    <div class="rec-app">
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
    </div>
</template>

<script>
import { ref, onMounted } from "vue";

export default {
    setup() {
    const newBooks = ref([]);

    const apiUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    const ttbKey = "ttbyoungjae.bae1809001";

    // New Books
    const fetchNewBooksJSONP = () => {
        return new Promise((resolve, reject) => {
            const callbackName = `jsonpCallback_${Date.now()}`;
            const script = document.createElement("script");

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=BlogBest&MaxResults=5&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

            script.onerror = () => {
            reject(new Error("JSONP request failed"));
            document.body.removeChild(script);
            };

            window[callbackName] = (response) => {
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

    onMounted(async () => {
        await fetchNewBooks();
        await fetchBestBooks();
    });

    return { newBooks, };
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
</style>
