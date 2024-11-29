<template>
    <div class="library-container">
        <LeftSidebar/>
      <aside class="sidebar">
        <nav>
          <ul>
            <li class="sidebar-item" v-for="menuItem in menuItems" :key="menuItem.title">
              <img :src="`/src/assets/sidebar/${menuItem.icon}`" :alt="menuItem.title" class="sidebar-icon">
              <div>
                <h4>{{ menuItem.title }}</h4>
                <p>{{ menuItem.count }}</p>
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
            <img :src="`/src/assets/icons/${book.coverImage}`" alt="Book Cover" class="book-cover" />
            <div class="book-details">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-progress">{{ book.progress }}%</p>
              <p class="book-remaining">{{ book.remainingDays }}일 남음</p>
            </div>
            <div v-if="book.isFavorite" class="favorite-icon">
              <img src="@/assets/icons/like.png" alt="Favorite">
            </div>
          </article>
        </section>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
  
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
  </script>
  
  <style scoped>
  .library-container {
    display: flex;
    box-sizing: border-box;
    height: 100vh;
  }
  
  .sidebar {
    width: 20%;
    padding: 10px;
  }
  
  .sidebar-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
  }
  
  .sidebar-icon {
    width: 40px;
    height: 40px;
    margin-right: 10px;
  }
  
  .main-content {
    width: 80%;
    padding: 20px;
  }
  
  .header {
    border-bottom: 1px solid #000;
    padding-bottom: 10px;
    margin-bottom: 20px;
  }
  
  .library-title {
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 10px;
  }
  
  .booklist-title {
    font-size: 24px;
    font-weight: 400;
  }
  
  .book-list {
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
  }
  
  .book-item {
    width: 230px;
    position: relative;
    padding: 10px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
  }
  
  .book-cover {
    width: 100%;
    height: 300px;
    object-fit: cover;
    border-radius: 5px;
  }
  
  .book-details {
    margin-top: 10px;
  }
  
  .book-title {
    font-size: 18px;
    font-weight: bold;
    color: #1f1f1f;
    margin-bottom: 5px;
  }
  
  .book-author {
    font-size: 15px;
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
    top: 10px;
    right: 10px;
    width: 24px;
    height: 24px;
  }
  
  .favorite-icon img {
    width: 100%;
    height: 100%;
  }
  
</style>