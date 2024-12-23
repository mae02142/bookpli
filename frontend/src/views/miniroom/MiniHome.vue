<template>
    <div class="dashboard">
        <LeftSidebar/>
        <section class="minihome-section">
            <div class="left-section">
            <!-- 사용자 프로필 -->
                <div class="home-user-profile">
                    <div class="user-profile-box">
                        <div class="user-info">
                        <img :src="userData.profilePath" class="avatar"></img>
                        <div class="book-plan">
                            <p class="userNm">{{userData.display_name}}님</p><br>
                            <div class="reading-count">
                                <img src="@/assets/icons/read_book.png">
                                <p style="margin-bottom: 5px;">{{ currentMonth }} 목표 권 수: <span>{{currentGoal}}권</span></p>
                            </div>
                            <div class="complete-count">
                                <img src="@/assets/icons/read_complete.png">
                                <p>{{ currentMonth }} 읽은 권 수: <span>{{currentRead}}권</span></p>
                            </div>
                        </div>
                        </div>
                        <div class="status-card">
                            <div class="most-read-month">
                                <div class="card-background"></div>
                                <div class="card-title">가장 많이 읽은달</div>
                                <div class="card-status">{{ mostReadInfo.month}}월 | {{mostReadInfo.count}}권</div>
                            </div>
                            <div class="yearly-read">
                                <div class="card-background"></div>
                                <div class="card-title">1년 동안</div>
                                <div class="card-status">{{yearCount}}권</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mini-divider"></div>
            <!-- 음악 플레이어 -->
            <div class="music-section">
                <h3 class="music-title">play music</h3>
                <p class="more-wrapper music-more" @click="gotoPlaylist">
                    <img src="../../assets/icons/add.png" class="sm-images" />더보기
                </p>
                <div class="music-player">
                    <MusicPlayer/>
                </div>
            </div>
            </div>

            <!-- 세로 구분선 -->
            <div class="vr full-height"></div>

            <div class="right-section">
                <h3 class="title-header">이번달 독서통계</h3>

                <div class="reading-status-box">
                    <div class="progress-legend">
                        <span class="goal"><div class="goal-bar"></div></span> 목표량
                        <span class="current"><div class="current-bar"></div></span> 현재 진행률
                    </div>
                    <ul v-if="readList.length > 0">
                        <div class="book-progress" v-for="(book, index) in readList" :key="index">
                        <p class="book-title" @click="openModal(book)">{{ book.title.split('-')[0] }}</p>
                        <p class="book-start-date">시작일 {{ book.startDate.split('T')[0] }}</p>
                        <!-- 종료일이 지나면 실패처리 -->
                        <div class="progress-wrapper" v-if="new Date(book.endDate) > new Date()">
                        <!-- Progress Bar -->
                        <!-- 목표량 Progress Bar -->
                        <div class="full-progress" max="100"></div>
                        <div class="goal-progress" :style="{ width: calculateGoalProgress[index]+ '%'}"></div>

                        <!-- 현재 Progress Bar -->
                        <div class="current-progress" :style="{ width: calInputPage[index]+ '%'}"
                            @mounted="changeToFail(book, index)"></div>
                            <div class="progress-info">
                                <span class="progress-percentage">{{ calInputPage[index] }}%</span>
                                <span class="page-info">
                                        <span v-if="isEditing[index]">
                                            <input type="number" v-model.number="currentPage[index]"
                                            @blur="stopEdit(index)" @keyup.enter="stopEdit(index)" class="edit-input"/>
                                            /{{ book.startindex }}
                                        </span>
                                        <span v-else>
                                            p.{{ currentPage[index] || 0 }}/{{ book.startindex }}
                                            <img src="../../assets/icons/bookmark2.png" class="sm-images" @click="startEdit(index)"/>
                                            <input type="button" value="완독" @click="clearReading(book)" class="complete-button"/>
                                        </span>
                                </span>
                            </div>
                        </div>
                        </div>
                    </ul>
                    <p v-else class="empty">읽고 있는 도서가 존재하지 않습니다.</p>
                </div>

                <h3 class="title-header">내가 읽고 있는 책</h3>
                <div>
                    <p class="more-wrapper book-more" @click="loadReadList">
                    <img src="../../assets/icons/add.png" class="sm-images"/>더보기
                    </p>
                    <div class="book-section">
                        <div v-if="readList.length > 0" class="book-covers">
                            <div class="book-item" v-for="rbook in pageinationRead.slice(0,5)" :key="rbook.isbn13">

                                <img class="book-cover" :src="rbook.cover" @click="gotoDetail(rbook.isbn13)"/> 
                                <div class="book-info">
                                    <div>
                                        <span class="book-icon" @click="openModal(rbook)">📖</span>
                                    </div>
                                    <div class="reading-book-grid">
                                        <span>{{ rbook.title.split('-')[0] }}</span>
                                        <span>{{ rbook.author }}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <p v-else class="empty">읽고 있는 도서가 존재하지 않습니다.</p>
                    </div>
                </div>




                <h3 class="title-header">내가 담아놓은 책</h3>
                <p class="more-wrapper book-more" @click="gotoLibrary">
                    <img src="../../assets/icons/add.png" class="sm-images"/>더보기
                </p>
                <div class="book-section">
                    <div v-if="addList.length > 0" class="book-covers">
                        <div class="book-item" v-for="wbook in pageinationWish.slice(0,5)" :key="wbook.isbn13">
                            <img class="book-cover" :src="wbook.cover" @click="gotoDetail(wbook.isbn13)"/>
                        </div>
                    </div>
                    <p v-else class="empty">담은 도서가 존재하지 않습니다.</p>
                </div>
            </div>
        </section>

        <ReadGoalModal
        :visible="showModal"
        :rbook="selectBook"
        @close="closeModal"
        @dropReading="updateReadList"
        />
    </div>

    </template>
    <script setup>
    import { ref, computed, onMounted } from "vue";
    import { useRouter } from "vue-router";
    import { useAuthStore } from "@/stores/auth";
    import { useUserStore } from "@/stores/user.js";
    import { useProgressStore } from "@/stores/readingProgressbar";
    import MusicPlayer from '@/components/layouts/musicPlayer.vue';
    import ReadGoalModal from "@/components/readGoal/ReadGoalModal.vue";
    import apiClient from "@/api/axiosInstance";
    import LeftSidebar from "@/components/layouts/LeftSidebar.vue";
    import { useUtilModalStore } from "@/stores/utilModalStore";
    import { useConfirmModalStore } from "@/stores/utilModalStore";

    import { useRouterUtils } from "@/router/routerUtils";
    
    const { gotoDetail } = useRouterUtils();
    
    const router = useRouter();
    const authStore = useAuthStore();
    const progressStore = useProgressStore();
    const userStore = useUserStore();

    const userData = ref({});
    const addList = ref([]);
    const readList = ref([]);
    const completedBooks = ref([]);
    const currentPage = ref([]);
    const isEditing = ref([]);
    const currentGoal = ref(0);
    const currentRead = ref(0);
    const yearCount = ref(0);
    const mostReadInfo = ref({ month: "0", count: 0 });
    const showModal = ref(false);
    const selectBook = ref({});
    const liked= ref("");

    const currentReading=ref(1);
    const currentWished=ref(1);
    const itemsPerPage= ref(5);
    const currentMonth = ref('');

    const modalStore= useConfirmModalStore();

    const pageinationRead = computed (() => {
        const startIndex= (currentReading.value -1) * itemsPerPage.value;
        const endIndex= startIndex+itemsPerPage.value;

        return readList.value.slice(startIndex, endIndex);
    });

    const pageinationWish = computed (() => {
        const startIndex= (currentWished.value -1) * itemsPerPage.value;
        const endIndex= startIndex+itemsPerPage.value;

        return addList.value.slice(startIndex, endIndex);
    });

    const getToken = async () => {
        try {
            if (authStore.isAuthenticated) {
                const spotifyId = authStore.user.spotifyId;
                const response = await fetch(`http://localhost:8081/tokens/accessToken?spotifyId=${spotifyId}`, {
                    credentials: "include",
            });

            if (!response.ok) {
                throw new Error("Failed to fetch access token");
            }

            const data = await response.json();
            const accessToken = data.access_token;
            userStore.setAccessToken(accessToken);
            } else {
                console.log("확인할 수 없는 회원입니다.");
            }
        } catch (error) {
            console.error(error.message);
        }
    };

    const loadReadList = () => {
        const utilModalStore = useUtilModalStore();

        if (currentReading.value * itemsPerPage.value < readList.value.length) {
        currentReading.value += 1;
        } else {
        utilModalStore.showModal(
            "알림",
            "마지막 페이지입니다.",
            "warning"
        );
        currentReading.value = 1;
        }
    };


    const gotoLibrary= () =>{
        router.push({
                path: `/mypage/mylibrary`,

            });
    }


    //목표기간 변경
    const updateStartDate= (value)=> {
        startDate.value=value;
        updateBookGoal();
    }

    const updateEndDate= (value)=> {
        endDate.value=value;
        updateBookGoal();
    }

    const updateBookGoal = () => {
        const bookIdx= readList.value.findIndex((b) => b.isbn13 === book.value.isbn13);
        if(bookIdx !== -1){
            readList.value[bookIdx].startDate=startDate.value;
            readList.value[bookIdx].endDate=endDate.value;
        }
    }

    const openModal = (book) => {
      selectBook.value = book;
      showModal.value = true;
    };

    const closeModal = () => {
      showModal.value = false;
    };

    // 독서 진행률 계산
    const calInputPage = computed(() =>
      readList.value.map((book, index) => {
        const current = currentPage.value[index] || 0;
        const total = book.startindex || 1;
        return Math.min(Math.max(Math.round((current / total) * 100), 0), 100);
      })
    );

    // 목표 진행률 계산
    const calculateGoalProgress = computed(() =>
      readList.value.map((book) => {
        const start = new Date(book.startDate);
        const end = new Date(book.endDate);
        const today = new Date();
        const totalDays = (end - start) / (1000 * 60 * 60 * 24);
        const elapsedDays = (today - start) / (1000 * 60 * 60 * 24);

        return Math.min(Math.max((elapsedDays / totalDays) * 100, 0), 100).toFixed(2);
      })
    );

    // 완료된 책 통계 계산
    const calculateCompletedStats = () => {
      const currentYear = new Date().getFullYear();

      // 1년 동안 읽은 책
      yearCount.value = completedBooks.value.filter((book) => {
        const bookYear = new Date(book.endDate).getFullYear();
        return bookYear === currentYear;
      }).length;

      // 가장 많이 읽은 달 계산
      const monthCounts = completedBooks.value.reduce((acc, book) => {
        const month = new Date(book.endDate).getMonth() + 1;
        acc[month] = (acc[month] || 0) + 1;
        return acc;
      }, {});

      const mostRead = Object.entries(monthCounts).reduce(
        (max, [month, count]) => (count > max.count ? { month, count } : max),
        { month: "0", count: 0 }
      );

      mostReadInfo.value = mostRead;
    };

    // 독서 데이터 로드
    const loadBooks = async (status, targetList) => {
      try {
        const { data } = await apiClient.get(`/api/miniroom/user/${authStore.user.userId}/book`, {
          params: { status },
        });
        targetList.value = data;
      } catch (error) {
        console.error(`${status} 상태의 책 로드 실패:`, error);
      }
    };

    // 사용자 정보 로드
    const loadUserProfile = async () => {
      try {
        const { data } = await apiClient.get(`/api/miniroom/user/${authStore.user.userId}/profile`);
        userData.value = data;
      } catch (error) {
        console.error("사용자 정보 로드 실패:", error);
      }
    };

    // 이번달 독서 목표 및 진행 계산
    const calculateMonthStats = () => {
      const currentMonth = new Date().getMonth() + 1;
      const currentYear = new Date().getFullYear();

      currentGoal.value = readList.value.filter((book) => {
        const bookDate = new Date(book.startDate);
        return bookDate.getFullYear() === currentYear && bookDate.getMonth() + 1 === currentMonth;
      }).length;

      currentRead.value = completedBooks.value.filter((book) => {
        const bookDate = new Date(book.endDate);
        return bookDate.getFullYear() === currentYear && bookDate.getMonth() + 1 === currentMonth;
      }).length;
    };

    const changeToFail = async (book, index) => {
      const utilModalStore = useUtilModalStore();

        const today = new Date();
        const endDate = new Date(book.endDate);

        if (today > endDate) {
        try {
            const response = await apiClient.put(`/api/miniroom/fail/${book.isbn13}`);

            // 통일된 Alert 모달 호출
            utilModalStore.showModal(
            "완독 실패 처리",
            `"${book.title}" 도서 완독이 실패 처리 되었습니다.`,
            "warning"
            );

            updateFailedBooks(index);
        } catch (error) {
            console.log("실패 처리 실패", error);
            utilModalStore.showModal(
            "오류 발생",
            "도서 실패 처리 중 오류가 발생했습니다.",
            "error"
            );
        }
        }
    };


    const updateFailedBooks = (index) => {
        if(index >= 0 && index < readList.value.length){
            readList.value.splice(index, 1);
        }
    };

    // ReadGoalModal에서 삭제된 도서를 readList에서 제거
    const updateReadList = (isbn13) => {
        const index = readList.value.findIndex(book => book.isbn13 === isbn13);
        if (index !== -1) {
            readList.value.splice(index, 1);
        }
    };


    // 독서 기록 저장
    const saveProgress = (index) => {
      const book = readList.value[index];
      if (!book || !book.isbn13) return;

      const progressData = {
        startDate: book.startDate,
        endDate: book.endDate,
        currentPage: currentPage.value[index],
        totalPages: book.startindex || 1,
        progressPercentage: Math.round((currentPage.value[index] / (book.startindex || 1)) * 100),
      };

      progressStore.saveProgress(book.isbn13, progressData);
    };

    // 편집 모드 제어
    const startEdit = (index) => {
      isEditing.value[index] = true;

       //값이 변경될때마다 저장
       saveProgress(index);
    };

    const stopEdit = (index) => {
      const book = readList.value[index];
      currentPage.value[index] = Math.max(0, Math.min(currentPage.value[index], book.startindex));
      isEditing.value[index] = false;
      saveProgress(index);
    };


    const clearReading = (book) => {
    const confirmModalStore = useConfirmModalStore();
    confirmModalStore.showModal(
        "도서 완독 처리",
        "도서를 완독 처리하시겠습니까?",
        "완독 처리 후 되돌릴 수 없습니다.",
        "완독 처리",
        "warning",
        async () => {
            try {
            const { status } = await apiClient.put(`/api/miniroom/clear/${book.isbn13}?status=completed`);
            if (status === 200) {
                alert("완독 처리되었습니다.");
                await loadBooks("reading", readList);
                await loadBooks("completed", completedBooks);
                calculateCompletedStats();
                calculateMonthStats();
            }
            } catch (error) {
            console.error("완독 처리 실패:", error);
            }
        }
        );
    };


    const likeordislike = async () => {
        try{
            const response= await apiClient.get(`/api/book/${authStore.user.userId}/${isbn13}`);
            liked.value=response.data;
            console.log(liked.data);
        }catch(error){
            console.log(error);
        }
    }
    
    const gotoPlaylist= () => {
        router.push({
            path: `/mypage/mypli`,
        });
    }

    // 로그인 직후 회원 정보 저장
    const getUserInfo = async() => {
      try {
        const response = await apiClient.get("/api/user-info");
        userData.value= response.data;
        if (response.data.data) {
          const userInfo = {
            spotifyId: response.data.data.spotifyId,
            userId: response.data.data.userId,
          };
          authStore.setUser(userInfo);
        }
      } catch (error) {
        console.error("사용자 정보 요청 실패:", error);
      }
    };

    // 컴포넌트 초기화
    onMounted(async () => {
      MusicPlayer;
      await getUserInfo();
      await loadUserProfile();
      await loadBooks("reading", readList);
      await loadBooks("completed", completedBooks);
      await loadBooks("wished", addList);
      const today = new Date();
      currentMonth.value = `${today.getMonth() + 1}월`;

    // 각 책의 편집 상태 초기화
    isEditing.value = readList.value.map(() => false);

    // 저장된 진행 상황 불러오기
    readList.value.forEach((book, index) => {
        const savedProgress = progressStore.getProgress(book.isbn13);
        if (savedProgress) {
            currentPage.value[index] = savedProgress.currentPage; // 저장된 현재 페이지
            book.progressPercentage = savedProgress.progressPercentage || 0; // 저장된 진행 퍼센트
        } else {
            book.progressPercentage = 0;
        }
    });

    // 실패 상태 처리
    readList.value.forEach((book, index) => {
        changeToFail(book, index); // 각 책에 대해 실패 상태 처리
    });

      calculateCompletedStats();
      calculateMonthStats();
      getToken()
    });
    </script>

    <style scoped>
    .dashboard {
        height: 100vh;
        display: grid;
        grid-auto-flow: column;
        grid-template-columns: 0fr 0.95fr;
    }

    .minihome-section{
        width: 100%;
        display: flex;
        margin: 20px;
        border: 1px solid #cecece52;
        border-radius: 20px;
        padding: 30px 10px;
        background-color: #fffffb;
    }

    .book-section{
        align-items: flex-start;
    }

    .left-section,
    .right-section {
    display: flex;
    flex-direction: column;
    background: white;
    border: 1px solid #cecece52;
    border-radius: 30px;
    }


    .left-section {
        align-items: center;
        gap: 20px;
        margin: 10px;
        padding: 10px;
    }

    .home-user-profile {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 20px;
        margin: 10px 20px;
    }

    .user-info {
        display: flex;
        align-items: center;
        gap: 15px;
        margin-bottom: 20px;
    }

    .avatar {
        width: 100px;
        height: 100px;
        background-color: #ddd;
        border-radius: 50%;
    }

    .status-card {
        display: flex;
        gap: 15px;
        margin-left: 15px;
        flex-direction: row;
        align-items: center;
        margin-right: 15px;
    }

    .most-read-month,
    .yearly-read {
        display: flex;
        width: 142px;
        height: 85px;
        text-align: center;
        background: #ffffff;
        border-radius: 20px;
        box-sizing: border-box;
        flex-flow: column;
        justify-content: center;
        gap: 6px;
        box-shadow: 1px 1px 2px 2px rgb(0 185 7 / 41%);
    }

    .mini-divider{
        border: none;
        width: 90%;
        border-top: 1px solid #ccc;
    }


    .right-section{
        margin : 10px;
        width: 80%;
        padding: 30px;
    }

    .reading-status-box {
    height: 300px;
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 8px;

    overflow-y: auto;
    overflow-x: hidden;
    border: 1px solid #e0e0e0;

    display: flex;
    flex-direction: column;
    gap: 20px;
    min-width: 500px;
    min-width: 250px;

    width: 80%;
    margin: auto;
    }

    .user-profile {
        height: 200px;
        border-bottom: 1px solid;
        display: grid;
        width: 100%;
        justify-items: center;
        align-items: center;
        height: fit-content;
        padding: 15px 0px;
    }


    .music-player,
    .book-section {
        margin-top: 20px;
    }

    .music-section{
        align-items: center;
        justify-content: center;
    }

    .music-player {
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
        margin-left: 40px;
        margin-top: 50px;
    }

    .reading-status ul,
    .book-section ul {
    list-style: none;
    padding: 0;
    }

    progress {
    width: 100%;
    margin: 5px 0;
    }

    .vertical-line {
        background-color: #ccc;
        grid-column: 2 / 3;
        grid-row: 1 / 4;
        width: 2px;
    }



    .book-covers {
        display: flex;
        gap: 20px;
    }



    .book-item {
        text-align: center;
        display: flex;
        flex-flow: column;
    }

    .book-cover {
        width: 150px;
        height: 200px;
        object-fit: cover;
        border-radius: 8px;
        margin-left: 19px;
    }



    .book-info {
        margin-top: 10px;
        font-size: 14px;
        display: inline-flex;
        width: 100%;
        align-items: center;
        gap: 7px;
    }

    .track-info {
        margin-left: 20px;
    }

    .controls {
        display: flex;
        justify-content: space-around;
        margin-top: 20px;
    }

    .control-button {
        width: 25px;
        height: 25px;
        cursor: pointer;
    }


    .music-progress {
        height: 8px;
        margin: 10px 0;
        border-radius: 5px;
    }

    .vr.full-height {
        height: 100%;
        background-color: #ccc;
        width: 2px;
    }

    .book-progress {
        margin: 0; /* 개별 요소 간 여백 제거 */
        padding: 10px 0; /* 내부 패딩 추가 */
        border-bottom: 1px solid #e0e0e0; /* 구분선 추가 */
    }

    .book-progress:last-child {
        border-bottom: none; /* 마지막 요소 구분선 제거 */
    }

    .progress-wrapper {
        position: relative;
        margin-top: 10px;
        width: 100%;
        height: 12px;
        margin-bottom: 18px;
    }

    .book-title {
        margin: 0;
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
    }

    .book-start-date {
        margin: 0;
        font-size: 14px;
        color: #666;
    }

    .goal-progress {
        position: absolute;
        top: 0;
        left: 0;
        height: 15px;
        background-color: rgb(2, 77, 42);
        border-radius: 6px;
        z-index: 1;
    }

    .current-progress {
        position: absolute;
        top: 0;
        left: 0;
        height: 15px;
        background-color: rgb(171, 235, 171);
        border-radius: 6px;
        z-index: 2;
    }

    .progress-percentage {
    font-size: 16px;
    font-weight: bold;
    color: #666;
    }

    .full-progress{
        top: 0;
        left: 0;
        height: 15px;
        background-color: #D9D9D9;
        border-radius: 6px;
    }

    .page-info {
    font-size: 14px;
    color: #666;
    }

    .music-title{
        margin: 20px 0;
        font-size: x-large;
    }

    .title-header {
        margin-bottom: 20px;
        margin-top: 20px;
        font-size: x-large;
    }

    .progress-info{
        display: inline-flex;
        width: 100%;
        justify-content: space-between;
        margin-top: 5px;
    }

    .more-wrapper,
    .track-title {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .sm-images{
        width: 15px;
        height: 15px;
        margin-right: 5px;
        cursor: pointer;
    }

    .more-wrapper {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        cursor: pointer;
        gap: 5px;
        font-size: 14px;
    }

    .music-more {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        gap: 5px;
        font-size: 14px;
        cursor: pointer;
        margin-top: -15px;
        width: 100%;
    }

    .more-wrapper.music-more {
        padding-right: 20px;
    }

    .reading-status {
    height: 436px;
    position: relative;
    background: #ffffff;
    width: 611px;
    padding: 20px;
    border-radius: 10px;
    }

    .profile-image {
    width: 129px;
    height: 159px;
    object-fit: cover;
    margin-bottom: 20px;
    }



    .card-title {
    font-size: 16px;
    font-weight: 400;
    color: #000000;
    }

    .card-status {
    font-size: 18px;
    font-weight: 400;
    color: #000000;
    font-weight: bold;
    }

    .sidebar {
        position: fixed;
        top: 0;
        left: 0;
        width: 60px;
        height: 100%;
        background-color: #fffdf1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 10px 0;
        box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
        z-index: 1000;
    }

    .empty{
        text-align: center;
        font-size: 18px;
        color: #909090;

    }

    .userNm{
        font-size: 18px;
        margin-left: 2px;
        font-weight: bold;
    }

    .book-icon {
        cursor: pointer;
    }

    .reading-book-grid {
        display: grid;
        justify-items: flex-start;
        gap: 4px;
    }

    .reading-book-grid span {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: block;
        max-width: 150px;
    }

    .reading-book-grid span:first-child {
        font-weight: bold;
        color: black;
        font-size: 15px;
    }

    .user-profile-box {
        display: grid;
        justify-items: center;
        padding: 20px;
        box-shadow: 1px 1px 1px 2px rgba(0, 0, 0, 0.1);
        border-radius: 17px;
    }

    .reading-count, .complete-count {
        display: flex;
        width: 200px;
        align-items: center;
        gap: 5px;
        padding: 6px;
    }

    .reading-count img, .complete-count img{
        width: 25px;
        height: 25px;
    }

    .complete-button {
        padding: 5px 9px;
        font-size: 15px;
        border: none;
        border-radius: 5px;
        background-color: #4CAF50;
        color: white;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }
    .current-bar{
        width: 30px;
        height: 10px;
        background-color: rgb(171, 235, 171);
    }
    .goal-bar{
        width: 30px;
        height: 10px;
        background-color: rgb(2, 77, 42);
    }
    .progress-legend {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        gap: 20px;
        margin-bottom: 10px;
        font-size: 14px;
        color: #666;
    }

    .legend-color {
        display: inline-block;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        margin-right: 5px;
    }
    </style>
