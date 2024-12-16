<template>
    <div class="book-app">
    <!-- Recommendations Section -->
    <section class="recommendations">
        <h1 class="section-title">추천 신간</h1>
        <hr>
        <div class="book-list">
            <div v-for="book in newBooks" :key="book.id" class="book-item" style="padding-top: 50px;">
                <div class="image-container" style="padding-bottom: 10px;">
                    <img :src="book.cover" :alt="book.title" class="book-image" @click="gotoDetail(book.isbn13)"/>
                </div>
                <p class="book-title" style="font-size: 20px; padding-bottom: 5px;">{{ book.title }}</p>
                <p class="book-author">{{ book.author.replace(/\(.*\)/, '') }}</p>
            </div>
        </div>
    </section>

    <!-- Rankings Section -->
    <section class="rankings">
        <h2 class="section-title">도서 순위</h2>
        <hr>
        <div class="rankings-container" style="width: 90%;">
        <!-- Domestic Rankings -->
        <div class="ranking" style="padding-top: 30px;">
            <h3 class="ranking-title">베스트셀러</h3>
            <table class="ranking-table">
                <tr class="ranking-tr" v-for="(book, index) in bestBooks" :key="book.rank">
                    <td style="width: 30px;">{{ index + 1 }}</td>
                    <td>{{ book.title.replace(/\(.*\)|\s*[-–].*/g, '') }}</td>
                    <td>{{ book.author.replace(/\(.*\)|\s*[-–].*|,.*$/g, '').replace(/,/g, '...') }}</td>
                    <td class="book-details" @click="toggleOptionMenu1(index)">
                        <span class="toggle-button">⋮</span>
                        <div v-show="showOptionMenu1[index]" class="option-menu">
                            <span>재생하기</span>
                            <span>내 플리에 추가하기</span>
                            <span>앨범 정보 보기</span>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

        <!-- Blog Best Seller -->
        <div class="ranking">
            <h3 class="ranking-title">블로거 추천 도서</h3>
            <table class="ranking-table">
                <tr 
                    class="ranking-tr" 
                    v-for="(book, index) in blogBooks" 
                    :key="book.rank" 
                    style="vertical-align: middle;"
                >
                    <td style="width: 30px;">{{ index + 1 }}</td>
                    <td>{{ book.title.replace(/\(.*\)|\s*[-–].*/g, '') }}</td>
                    <td>{{ book.author.replace(/\(.*\)|\s*[-–].*|,.*$/g, '').replace(/,/g, '...') }}</td>
                    <td class="book-details"@click="toggleOptionMenu2(index)">
                        <span class="toggle-button">⋮</span>
                        <div v-show="showOptionMenu2[index]" class="option-menu">
                            <span>재생하기</span>
                            <span>내 플리에 추가하기</span>
                            <span>앨범 정보 보기</span>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        </div>
    </section>
    </div>
    <MusicPlayer/>
</template>

<script>
import MusicPlayer from "@/components/layouts/musicPlayer.vue";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

export default {
    components: {
        MusicPlayer,
    },
    setup() {
    const newBooks = ref([]);
    const bestBooks = ref([]);
    const blogBooks = ref([]);

    const apiUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    const ttbKey = "ttbyoungjae.bae1809001";

    const showOptionMenu1 = ref([]);

    const toggleOptionMenu1 = (index) => {
        showOptionMenu1.value[index] = !showOptionMenu1.value[index];
    };

    const closeAllMenus1 = () => {
        // Close all menus
        showOptionMenu1.value = [];
    };

    const showOptionMenu2 = ref([]);

    const toggleOptionMenu2 = (index) => {
        showOptionMenu2.value[index] = !showOptionMenu2.value[index];
    };

    const closeAllMenus2 = () => {
        // Close all menus
        showOptionMenu2.value = [];
    };

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
                isbn13: book.isbn13
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
                isbn13: book.isbn13
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
                isbn13: book.isbn13
            }));
        } catch (error) {
            console.error("Error fetching Blog Books data:", error);
        }
    };
    

    const router = useRouter();
    const gotoDetail = (isbn13) => {
        router.push({
            path: `/main/book/${isbn13}`,
        }).catch((err) => {
        console.error("Navigation Error:", err);
    });
    };

    onMounted(async () => {
        await fetchNewBooks();
        await fetchBestBooks();
        await fetchBlogBooks();
    });


    return { newBooks, bestBooks, blogBooks, showOptionMenu1, toggleOptionMenu1, closeAllMenus1, showOptionMenu2, toggleOptionMenu2, closeAllMenus2, gotoDetail };
    },
};
</script>

<style scoped>
/* General Styles */
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

.book-title, .book-author {
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
    cursor: pointer;
}

.image-container {
    position: relative;
}

.add-btn {
    background-color: #fff;
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
    z-index: 2;
}

/* Rankings Section */
.rankings-container {
    margin: auto;
    display: flex;
    position: relative;
    justify-content: space-between;
}

.rankings {
    padding-top: 30px;
}

.ranking {
    width: 45%;
    padding-top: 30px;
}

.ranking-title {
    font-size: 1.7rem;
    margin-bottom: 1rem;
}

.ranking-table {
    width: 100%;
    margin: auto;
    overflow: visible;
}

.ranking-tr {
    border-bottom: 1px solid #ccc;
    height: 50px;
    vertical-align: middle;
    position: relative;
}

.book-details {
    cursor: pointer;
    text-align: center;
    position: relative;
}

.option-menu {
    position: absolute;
    background-color: #fff;
    border: 1px solid #ddd;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    z-index: 1000;
    flex-direction: column;
    width: max-content;
    top: 20%;
    left: 20px;
    display: flex;
}

/* Option Menu 항목 스타일 */
.option-menu span {
    padding: 10px;
    cursor: pointer;
    width: 100%;
    text-align: left;
}

.option-menu span:hover {
    color: #67de86;
}

.toggle-button {
    cursor: pointer;
}

</style>
