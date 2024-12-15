<template>
    <div class="book-app">
    <!-- Recommendations Section -->
    <section class="book-recommendations">
            <h1 class="section-title">추천 신간</h1>
            <hr>
            <div id="carouselExampleControlsNoTouching" class="carousel slide" data-bs-touch="true" data-bs-interval="false">
                <div class="carousel-inner">
                    <div 
                        v-for="(group, groupIndex) in groupedBooks" 
                        :key="groupIndex" 
                        class="carousel-item"
                        :class="{ active: groupIndex === 0 }"
                    >
                        <div class="book-group">
                            <div 
                                v-for="book in group" 
                                :key="book.id" 
                                class="book-item"
                            >
                                <div class="image-container">
                                    <img
                                        :src="book.cover"
                                        :alt="book.title"
                                        class="book-image"
                                        @click="gotoDetail(book.isbn13)"
                                    />
                                </div>
                                <p class="book-title">
                                    {{ book.title.length > 20 ? book.title.slice(0, 20) + '...' : book.title }}
                                </p>
                                <p class="book-author" style="font-size: 14px;">
                                    {{ book.author.replace(/\(.*\)/, '').length > 15 
                                        ? book.author.replace(/\(.*\)/, '').slice(0, 15) + '...' 
                                        : book.author.replace(/\(.*\)/, '') }}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <button 
                    class="carousel-control-prev" 
                    type="button" 
                    data-bs-target="#carouselExampleControlsNoTouching"
                    data-bs-slide="prev"
                >
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Previous</span>
                </button>
                <button 
                    class="carousel-control-next" 
                    type="button" 
                    data-bs-target="#carouselExampleControlsNoTouching"
                    data-bs-slide="next"
                >
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Next</span>
                </button>
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
                <tr 
                    class="ranking-tr" 
                    v-for="(book, index) in bestBooks" 
                    :key="book.rank"
                >
                    <td style="width: 30px;">{{ index + 1 }}</td>
                    <td>{{ book.title.replace(/\(.*\)|\s*[-–].*/g, '') }}</td>
                    <td>{{ book.author.replace(/\(.*\)|\s*[-–].*|,.*$/g, '').replace(/,/g, '...') }}</td>
                    <td 
                        class="book-details"
                        @click="toggleOptionMenu1(index)"
                    >
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
                    <td 
                        class="book-details"
                        @click="toggleOptionMenu2(index)"
                    >
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
import MusicPlayer from "@/components/layouts/musicPlayer.vue"; // Corrected casing
import { ref, onMounted, computed } from "vue";
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

            script.src = `${apiUrl}?ttbkey=${ttbKey}&QueryType=ItemNewSpecial&MaxResults=10&start=1&SearchTarget=Book&output=js&Version=20131101&callback=${callbackName}`;

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

    const groupedBooks = computed(() => {
    const groups = [];
    for (let i = 0; i < newBooks.value.length; i += 5) {
        groups.push(newBooks.value.slice(i, i + 5)); // 5권씩 묶기
    }
    console.log("Grouped Books:", groups); // 디버깅용 출력
    return groups;
    });

    onMounted(async () => {
        await fetchNewBooks();
        await fetchBestBooks();
        await fetchBlogBooks();
    });


    return { newBooks, bestBooks, blogBooks, showOptionMenu1, toggleOptionMenu1, closeAllMenus1, showOptionMenu2, toggleOptionMenu2, closeAllMenus2, gotoDetail,
        groupedBooks
     };
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
.book-recommendations {
    margin-bottom: 2rem;
    margin-top: 1rem;
}

.section-title {
    font-size: 28px;
    margin-bottom: 1rem;
    font-weight: bold;
}

.book-title {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    display: block;
    font-size: 16px; 
    margin-bottom: 0px;
    font-weight: bold;
    margin-top: 5px;
    margin-bottom: 5px;
}

.book-author {
    max-width: 170px;
    white-space: nowrap;
    overflow: hidden; 
    text-overflow: ellipsis; 
    display: block;
    font-size: 16px; 
    margin-bottom: 0px;
}

.book-list {
    gap: 1rem;
    display: flex;
  transition: transform 0.5s ease-in-out; /* 부드러운 애니메이션 */
  will-change: transform; /* 성능 최적화 */
}

.book-image {
    width: 150px;
    height: 200px;
    cursor: pointer;
    max-width: 200px;
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


/* 신간 도서 캐러셀 */

.book-group {
    display: flex;
    justify-content: center;
    gap: 25px;
    margin-left: 20px;
}

.book-item {
    display: flex;
    flex-direction: column;
    margin: 0 10px;
    align-items: flex-start;
    min-width: 170px;
}

.image-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 5px;
}

.carousel {
    position: relative;
    height: 300px;
    display: flex;
    align-items: center;
}

.carousel-control-prev-icon,
.carousel-control-next-icon {
    background-color: rgba(255, 255, 255, 1); /* 배경 흰색 */
    border-radius: 50%;
    width: 40px;
    height: 40px;
    box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2); 
    transition: all 0.3s ease-in-out; /* 부드러운 애니메이션 */
    opacity: 1; /* hover 시 투명도 제거 */
    display: grid;
    align-items: center;
    justify-content: center;
}

/* 버튼 hover 시 스타일 */
.carousel-control-prev-icon:hover,
.carousel-control-next-icon:hover {
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.3); /* hover 시 그림자 강조 */
    transform: scale(1.1); /* hover 시 크기 약간 확대 */
}

/* 내부 화살표 크기 조정 */
.carousel-control-prev-icon::before {
    content: ''; /* 기본값 제거 */
    display: inline-block;
    border: solid rgba(0, 0, 0, 0.6); /* 화살표 색상 */
    border-width: 0 2px 2px 0; /* 화살표 스타일 */
    padding: 5px; /* 화살표 크기 */
    transform: rotate(135deg); /* 기본 방향 */
    margin-left: 5px;
}

.carousel-control-next-icon::before {
    content: ''; /* 기본값 제거 */
    display: inline-block;
    border: solid rgba(0, 0, 0, 0.6); /* 화살표 색상 */
    border-width: 0 2px 2px 0; /* 화살표 스타일 */
    padding: 5px; /* 화살표 크기 */
    transform: rotate(-45deg);
    margin-right: 5px;
}

.carousel-control-next {
    right: -70px;
}

.carousel-control-prev {
    left: -70px;
}

.carousel-inner {
  overflow: hidden; /* 숨겨진 요소가 보이지 않도록 설정 */
}

.carousel-item {
  display: none; /* 기본적으로 숨김 */
  transition: transform 0.8s ease-in-out; /* 부드러운 슬라이드 애니메이션 */
}

.carousel-item.active {
  display: flex; /* 활성화된 요소만 표시 */
  justify-content: center;
}


</style>
