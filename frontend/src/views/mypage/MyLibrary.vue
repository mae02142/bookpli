<template>
  <div class="library-container">
    <LeftSidebar />

    <aside class="sidebar2">
      <nav>
        <ul>
          <li class="sidebar-item" v-for="menuItem in menuItems" :key="menuItem.title">
            <img
              :src="`/src/assets/sidebar/${menuItem.icon}`"
              :alt="menuItem.title"
              class="sidebar-icon"
            />
            <div class="menuitem-grid">
              <h4>{{ menuItem.title }}</h4>
              <p class="menuitem-count">{{ menuItem.count }}</p>
            </div>
          </li>
        </ul>
      </nav>
    </aside>

    <div class="main-content">
      <header class="header">
        <h1 class="library-title">내 서재</h1>
        <h2 class="booklist-title">독서중인 도서목록</h2>
      </header>

      <section class="book-list">
        <article class="book-item" v-for="(book, index) in books" :key="index">
          <img
            :src="`/src/assets/icons/${book.coverImage}`"
            alt="Book Cover"
            class="book-cover"
            @click="openModal(book)"
          />
          <div class="book-details">
            <div class="title-grid">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
            </div>
            <div class="progress-grid">
              <p class="book-progress">{{ book.progress }}%</p>
              <p class="book-remaining">{{ book.remainingDays }}일 남음</p>
            </div>
          </div>
          <div v-if="book.isFavorite" class="favorite-icon">
            <img src="@/assets/icons/like.png" alt="Favorite" />
          </div>
        </article>
      </section>
    </div>

    <!-- 모달 컴포넌트 -->
    <BookDetailModal
      v-if="isModalVisible"
      :book="selectedBook"
      @close="closeModal"
    />
  </div>
</template>
  
  <script setup>
  import { ref, reactive } from 'vue';
  import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
  import BookDetailModal from './BookDetailModal.vue';
  
  // 사이드바 메뉴 아이템들 데이터
  const menuItems = ref([
    { title: '독서중', count: '1권', icon: 'openbook.png' },
    { title: '찜한도서', count: '1권', icon: 'bookmark.png' },
    { title: '완독', count: '2권', icon: 'closedbook.png' },
  ]);
  
  // 독서중인 도서 데이터
  const books = ref([
    { title: '보편의 단어', author: '이기주', progress: 30, remainingDays: 10, coverImage: 'book1.png', isFavorite: true },
    { title: '보편의 단어', author: '이기주', progress: 30, remainingDays: 10, coverImage: 'book1.png', isFavorite: false },
    { title: '보편의 단어', author: '이기주', progress: 30, remainingDays: 10, coverImage: 'book1.png', isFavorite: false },
    { title: '보편의 단어', author: '이기주', progress: 30, remainingDays: 10, coverImage: 'book1.png', isFavorite: false }
  ]);

  // 툴팁 상태 관리
  const tooltip = reactive({
    show: false,
    text: "상세보기",
    x: 0,
    y: 0,
  });

  // 툴팁 표시 함수
const showTooltip = (event) => {
  tooltip.show = true;
  // tooltip.text = text;
  tooltip.x = event.pageX + 10; // 마우스 위치 오른쪽으로 10px
  tooltip.y = event.pageY + 10; // 마우스 위치 아래로 10px
};

// 툴팁 숨김 함수
const hideTooltip = () => {
  tooltip.show = false;
};


// 모달 상태 및 선택된 책 데이터
const isModalVisible = ref(false);
const selectedBook = ref({});

// 모달 열기
const openModal = (book) => {
  selectedBook.value = book; // 선택된 책 데이터 설정
  isModalVisible.value = true; // 모달 표시
};

// 모달 닫기
const closeModal = () => {
  isModalVisible.value = false; // 모달 숨김
};
  </script>
  
  <style scoped>
  .library-container {
    display: flex;
    box-sizing: border-box;
    height: 100vh;
  }
  
  .sidebar2 {
    width: 250px;
    padding: 10px;
  }
  
  .sidebar-item {
    display: flex;
    align-items: center;
    padding: 10px 0px;
    margin-left: 10px;
    transition: background-color 0.3s ease;
  }

  .sidebar-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  }
  
  .sidebar-icon {
    width: 35px;
    height: 35px;
    margin-right: 10px;
  }
  
  .main-content {
    width: 60%;
    padding: 20px 0px;
  }
  
  .header {
    border-bottom: 1px solid #000;
    padding-bottom: 10px;
    margin-bottom: 20px;
  }
  
  .library-title {
    font-size: 25px;
    font-weight: 600;
    text-align: center;
    margin-bottom: 40px;
  }
  
  .booklist-title {
    font-size: 20px;
    font-weight: 400;
  }
  
  .book-list {
    display: grid;
    grid-template-columns: repeat(4, 1fr); /* 4개의 열을 동일한 크기로 */
    gap: 25px;
    justify-items: start;
  }
  
  .book-item {
    width: fit-content;
    position: relative;
    padding: 10px;
  }
  
  .book-item:hover {
    cursor: pointer;
  }
  
  .book-cover {
    width: 100%;
    object-fit: cover;
  }
  
  .book-details {
    margin-top: 10px;
  }
  
  .book-title {
    font-size: 16px;
    font-weight: bold;
    color: #1f1f1f;
    margin-bottom: 5px;
  }
  
  .book-author {
    font-size: 13px;
    color: #707070;
    margin-bottom: 5px;
  }
  
  .book-progress {
    font-size: 15px;
    color: #414141;
    margin-bottom: 2px;
  }
  
  .book-remaining {
    font-size: 15px;
    color: #ff5e00;
  }
  
  .favorite-icon {
    position: absolute;
    top: 17px;
    right: 15px;
    width: 24px;
    height: 24px;
  }
  
  .favorite-icon img {
    width: 100%;
    height: 100%;
  }

  .title-grid {
    min-height: 50px;
  }

  .progress-grid {
    display: flex;
    gap: 10px;
  }
  
  .menuitem-count {
    color: #383838;
    font-size: 14px;
  }

  .menuitem-grid {
    display: flex;
    gap: 3px;
    flex-flow: column;
  }

  .tooltip {
  position: absolute;
  z-index: 1000;
  background-color: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 12px;
  white-space: nowrap;
  pointer-events: none;
}
</style>