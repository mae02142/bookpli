<template>
    <div class="rec-app">
        <section class="recommendations">
            <div class="book-list">
                <div 
                    v-for="book in newBooks" 
                    :key="book.id" class="book-item" 
                    @click="bookclick(book)">
                    <div class="recommend-image-container">
                        <img :src="book.cover" :alt="book.title" class="recommend-book-image" />
                    </div>
                    <p class="recommend-book-title">{{ book.title }}</p>
                    <p class="recommend-book-author">{{ book.author.replace(/\(.*\)/, '') }}</p>
                </div>
            </div>
        </section>
    </div>
</template>
<script>
import { ref, onMounted } from "vue";

export default {
    emits:["recomBook"], //부모 컴포넌트로 이벤트 전달

    setup(_, {emit}) {
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
                pubdate: book.pubdate,
                isbn13: book.isbn13,
                description: book.description,
                startindex: book.startindex,
                publisher: book.publisher,
            }));
        } catch (error) {
            console.error("Error fetching New Books data:", error);
        }
    };

    const bookclick = (book) => {
        emit("recomBook", book); //부모 컴포넌트로 선택된 책 정보 전달
    };

    
    onMounted(async () => {
        await fetchNewBooks();
    });
    return { newBooks, bookclick};
    },
};
</script>
<style scoped>
body {
    font-family: Arial, sans-serif;
    background-color: #ffff;
}

.rec-app {
    max-width: 1200px;
    margin: auto;
    padding: 0.5rem;
}
.book-list {
    display: flex;
    justify-content: center;
    gap: 60px;
}
.book-item{
    width: 150px;
    padding-top: 50px;
}
.recommend-image-container{
    width: 100%;
    padding-bottom: 10px;
    cursor: pointer;
}
    
.recommend-book-image {
    width: 130px;
    height: 180px;
    border: 0.1px solid #EAEAE1;
    object-fit: cover;
    border-radius: 8px;
}

.recommend-book-title {
    font-size: 15px;
    font-weight: 600;
    margin: 10px 0 5px;
    padding-bottom: 5px;
    line-height: 1.1;
}

.recommend-book-author {
    font-size: 14px;
    color: gray;
}
</style>