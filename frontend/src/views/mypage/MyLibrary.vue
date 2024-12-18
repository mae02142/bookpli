<template>
  <div class="library-container">
    <LeftSidebar />
    <aside class="sidebar2">
      <nav>
        <ul>
          <li class="sidebar-item" v-for="menuItem in menuItems" :key="menuItem.title"
          :class="{ active: selectedStatus === menuItem.route }"
            @click="selectStatus(menuItem.route)">
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
        <h2 class="booklist-title"> {{ menuItems.find(item => item.route === selectedStatus)?.title || '전체 도서 목록' }}</h2>
      </header>
      <section class="book-list">
         <!-- 선택된 상태에 맞는 도서 목록 렌더링 -->
         <article
          class="book-item"
          v-for="(book) in filteredBooks"
          :key="book.libraryId"
        >
        <div class="bool-like-grid">
          <img
            :src="book.cover"
            alt="Book Cover"
            class="book-cover"
            @click="openModal(book)"
            @mouseover="showTooltip"
            @mouseleave="hideTooltip"
          />
          <img src="@/assets/icons/heart_circle_noline.png" class="book-like-icon" v-if="isBookLiked(book.isbn13)">
        </div>
          <div class="book-details-block">
            <div class="title-grid">
              <h3 class="book-title">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
            </div>
            <div class="progress-grid" v-if="selectedStatus === 'reading'">
              <p class="book-progress">{{ progressStore.getProgress(book.isbn13)?.progressPercentage || 0 }}%</p>
              <p class="book-remaining">{{ calculateRemainingDays(book.endDate) }}일 남음</p>
            </div>
          </div>
          <div v-if="book.isFavorite" class="favorite-icon">
            <img src="@/assets/icons/like.png" alt="Favorite" />
          </div>
        </article>
      </section>
      <!-- 리뷰 조회 -->
    <MyReviewList 
    v-if="selectedStatus === 'myreview'"
    />

       <!-- 리뷰 모달  -->
    <ReviewForm :isVisible="showForm"
     :userId = authStore.user.userId
     :bookId="selectedBook"
     @update:isVisible="showForm = $event" />
    </div>
    <!-- 모달 컴포넌트 -->
    <BookDetailModal
      v-if="isModalVisible"
      :book="selectedBook"
      @close="closeModal"
      @openForm="showForm=true"
      @delete-library="deleteLibrary"
      @book-like-status="toggleBookLike"
    />
  </div>
</template>
  
  <script setup>
  import { ref, reactive, onMounted, computed } from 'vue';
  import LeftSidebar from '@/components/layouts/LeftSidebar.vue';
  import BookDetailModal from './BookDetailModal.vue';
  import ReviewForm from '@/components/review/ReviewForm.vue';
  import { useAuthStore } from '@/stores/auth';
  import apiClient from '@/api/axiosInstance';
  import { useProgressStore } from '@/stores/readingProgressbar';
  import MyReviewList from '../review/MyReviewList.vue';
  const authStore = useAuthStore();
  const menuItems = ref([]);
  const books = ref([]);
  const selectedStatus = ref('reading');
  const progressStore = useProgressStore();
  // 모달 상태 및 선택된 책 데이터
  const isModalVisible = ref(false);
  const selectedBook = ref({});
  const likedBooks = ref([]); // 좋아요된 책의 ISBN 목록
  // 리뷰 작성 모달 상태
  const showForm = ref(false);
 
const getMyLibrary = async () => {
  try {
    const response = await apiClient.get(`/api/library/${authStore.user.userId}`);
    books.value = response.data.data;
    updateMenuItems();
  } catch (error) {
    console.error('Error loading inquiries:', error);
  }
};
//그룹이 존재하지 않을 때 새 그룹을 생성하는 조건식
const groupedData = computed(() => {
      return books.value.reduce((acc, item) => {
        (acc[item.status] || (acc[item.status] = [])).push(item);
        return acc;
      }, {});
    });
const updateMenuItems = () => {
      menuItems.value = [
        { title: '독서중', count: (groupedData.value.reading?.length || 0) +' 권', icon: 'openbook.png', route: 'reading' },
        { title: '담은 도서', count: (groupedData.value.wished?.length || 0) +' 권', icon: 'bookmark.png', route: 'wished' },
        { title: '완독', count:( groupedData.value.completed?.length || 0 )+' 권', icon: 'closedbook.png', route: 'completed' },
        { title: '좋아요한 도서', count:( likedBooks.value.length)+' 권', icon: 'book_heart.png', route: 'liked' },
        { title: '나의 리뷰',  icon: 'review.png', route: 'myreview'}
      ];
    };
// 현재 선택된 상태에 따른 책 필터링
const filteredBooks = computed(() => {
  if (selectedStatus.value === 'liked') {
    return books.value.filter((book) => isBookLiked(book.isbn13)); // 좋아요된 책만 필터링
  }
  return groupedData.value[selectedStatus.value] || [];
});
// 선택된 상태 변경
const selectStatus = (status) => {
  selectedStatus.value = status;
  updateMenuItems();
};
  // 툴팁 상태 관리
  const tooltip = reactive({
    show: false,
    text: "",
    x: 0,
    y: 0,
  });
  // 툴팁 표시 함수
const showTooltip = (event) => {
  tooltip.show = true;
  tooltip.text = "상세보기";
  tooltip.x = event.pageX + 10; // 마우스 위치 오른쪽으로 10px
  tooltip.y = event.pageY + 10; // 마우스 위치 아래로 10px
};
// 툴팁 숨김 함수
const hideTooltip = () => {
  tooltip.show = false;
};
// 모달 열기
const openModal = (book) => {
  selectedBook.value = book; // 선택된 책 데이터 설정
  isModalVisible.value = true; // 모달 표시
};
// 모달 닫기
const closeModal = () => {
  isModalVisible.value = false; // 모달 숨김
};
// books 데이터에 progress와 remainingDays 추가
const prepareBooksData = (books) => {
  return books.map((book) => {
    const progressData = progressStore.getProgress(book.isbn13);
    const progressPercentage = progressData?.progressPercentage || 0; // 기본값 0 설정
    return {
      ...book,
      progress: progressPercentage,
      remainingDays: calculateRemainingDays(book.endDate),
    };
  });
};
// 남은 일 수 계산 함수
const calculateRemainingDays = (endDate) => {
  if (!endDate) return 0; // endDate가 없는 경우 0 반환
  const today = new Date();
  const end = new Date(endDate);
  const diffTime = end.getTime() - today.getTime(); // 밀리초 차이 계산
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); // 일 단위로 변환
  return diffDays > 0 ? diffDays : 0; // 음수일 경우 0 반환
};
const deleteLibrary = async (libraryId) => {
   // 삭제된 항목을 로컬 리스트에서 제거
   try {
      await apiClient.delete(`/api/library`, {
      data: {
        userId: authStore.user.userId,
        libraryId: libraryId,
      },
    });
    getMyLibrary();
    } catch (error) {
      console.log(error);
    }
}
const getBookLikeStatus = async () => {
  try {
    const response = await apiClient.get(`/api/library/book-like/${authStore.user.userId}`);
    likedBooks.value = response.data.data.map((like) => like.isbn13);
    } catch (error) {
      console.log(error);
    }
}
// 좋아요 여부 확인
const isBookLiked = (isbn13) => likedBooks.value.includes(isbn13);
// 좋아요 상태 업데이트
const toggleBookLike = (isbn13) => {
  if (likedBooks.value.includes(isbn13)) {
    // 이미 좋아요 되어 있는 경우 -> 목록에서 제거
    likedBooks.value = likedBooks.value.filter((isbn) => isbn !== isbn13);
  } else {
    // 좋아요가 안 되어 있는 경우 -> 목록에 추가
    likedBooks.value.push(isbn13);
  }
  updateMenuItems();
};
onMounted(async() => {
  await getMyLibrary();
  await getBookLikeStatus();
  books.value = prepareBooksData(books.value);
  updateMenuItems();
});
</script>
  
  <style scoped>
  .library-container {
    height: 100vh;
    display: grid;
    grid-auto-flow: column;
    grid-template-columns: 0fr 0fr 1fr;
  }
  
  .sidebar2 {
    width: 250px;
    padding: 10px;
  }
  
  .sidebar-item {
    display: flex;
    align-items: center;
    padding: 20px 0px;
    margin-left: 20px;
    transition: background-color 0.3s ease;
  }
  .sidebar-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  }
  
  .sidebar-icon {
    width: 30px;
    height: 30px;
    margin-right: 10px;
  }
  
  .main-content {
    width: 70%;
    padding: 20px 0px;
    margin-left: 8%;
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
    grid-template-columns: repeat(5, 1fr);
    justify-items: start;
  }
  
  .book-item {
    display: grid;
    width: min-content;
    justify-self: center;
  }
  
  .book-item:hover {
    cursor: pointer;
  }
  
  .book-cover {
    width: 143px;
    height: 210px;
    object-fit: cover;
  }
  
  .book-details-block {
    margin-top: 10px;
  }
  
  .book-title {
    font-size: 15px;
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
.bool-like-grid {
  position: relative; /* 부모 컨테이너를 기준으로 위치 설정 */
  display: inline-block;
}
.book-like-icon {
  position: absolute; /* 부모 컨테이너를 기준으로 절대 위치 설정 */
  top: 7px; /* 상단으로부터의 거리 */
  right: 7px; /* 오른쪽으로부터의 거리 */
  width: 24px; /* 아이콘 크기 */
  height: 24px;
}
.menuitem-grid h4 {
  font-weight: bold;
}
.review-icon {
  margin-left: 4px;
  margin-right: 8px;
  width: 28px;
}
.review-title{
  font-weight: bold;
}
</style>