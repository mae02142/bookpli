<template>
    <div class="community">

      <LeftSidebar />
    <div class="sidebar3">
    <div class="svg-container">
      <svg
        class="rotatable-svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        @click="openEdit"
      >
        <path
          d="M9.60803 1.75L9.10502 4.336C8.26922 4.64433 7.49158 5.09181 6.80503 5.6595L4.32003 4.803L1.92603 8.947L3.91603 10.677C3.75891 11.5528 3.75891 12.4497 3.91603 13.3255L1.92603 15.055L4.32053 19.197L6.80253 18.342C7.48887 18.9108 8.26765 19.3578 9.10502 19.6635L9.60753 22.25H14.392L14.8845 19.69C15.7175 19.3576 16.4961 18.9026 17.1945 18.34L19.679 19.197L22.071 15.055L20.083 13.3235C20.2418 12.4492 20.2418 11.5533 20.083 10.679L22.073 8.9455L19.6785 4.8035L17.1965 5.6585C16.5096 5.09047 15.731 4.64358 14.894 4.337L14.3915 1.751L9.60803 1.75ZM11.9565 8.853C12.7921 8.8678 13.5885 9.21013 14.1742 9.80629C14.7599 10.4025 15.0881 11.2048 15.0881 12.0405C15.0881 12.8762 14.7599 13.6785 14.1742 14.2747C13.5885 14.8709 12.7921 15.2132 11.9565 15.228C11.1112 15.228 10.3005 14.8923 9.70262 14.2946C9.10478 13.697 8.76879 12.8863 8.76853 12.041V12.0405C8.76853 11.6219 8.85099 11.2073 9.01121 10.8206C9.17143 10.4338 9.40626 10.0824 9.7023 9.78642C9.99834 9.49043 10.3498 9.25565 10.7366 9.09549C11.1234 8.93533 11.5379 8.85293 11.9565 8.853Z"
          stroke="black"
          stroke-width="0.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </div>
      <div class="bookclub-list">
        <!-- 북클럽 목록 -->
        <div
          class="bookclub-item"
          v-for="myclub in myclubList"
          :key="myclub.userClubId"
        >
        <RouterLink :to="{path : '/bookclub/community', 
        query : {cover : myclub.cover , title : myclub.title, 
        author : myclub.author , bookClubId : myclub.bookClubId, description : myclub.description}}">
          <div class="bookclub-details">
            <img :src="myclub.cover" alt="icon" class="note-icon" />
            <p class="bookclub-name">{{ myclub.title.replace(/\(.*?\)/g, '').trim() }}</p>
          </div>
          </RouterLink>
            <img @click="confirmRemove(myclub.userClubId)" 
            src="@/assets/icons/close.png" 
            alt="remove club" 
            class="close-icon" 
            v-show="showEdit"
            />
        </div>
      </div>
    </div>

    <div class ="main">
      <header class="header">
        <div class="title-quotes">
            <img class="quotes" src="@/assets/icons/double-quotes-l.png" alt="doble-quotes-l">
            <h1>북적북적</h1>
            <img class="quotes" src="@/assets/icons/double-quotes-r.png" alt="double-quotes-r">
        </div>
        <p class="subtitle">
          책을 사랑하는 사람들이 의견을 나누고, 추천하고, 독서 경험을 공유하는 공간
        </p>
        <div class="search-bar">
            <input v-model="searchValue" class="search-guide" @keyup.enter="searchBookClub(searchValue)" placeholder="찾으려는 커뮤니티의 도서명을 입력하세요"></input>
            <img class="search-community" @click="searchBookClub(searchValue)" src="@/assets/icons/search.png" alt="Search Community">
        </div>
      </header>
      <section v-if="searchList" class="search-content">
        <h2 class="list-title">“{{ searchValue }}” 에 해당하는 커뮤니티</h2>
      
      <article class="community-list">
        <div class="community-item" v-for="item in communities" :key="item.bookClubId">
          <div class="img-btn"  @click="addBookClub(item.isbn13)">
            <div class="add-community">
                <p class="add-icon" >+</p>
            </div>
          <div class="cover-one">
              <div class="cover-two">
                <img :src="item.cover" :alt="item.title" class="item-image" />
              </div>
            </div>
          </div>  
          <div class="item-details">
            <h3 class="item-title">{{ item.title.replace(/\(.*?\)/g, '').trim() }}</h3>
            <p class="item-author">{{ item.author.replace(/\(.*?\)/g, '').trim() }}</p>
          </div>
        </div>
      </article>
      </section>
      <section v-else class="search-content">
        <h2 class="list-title"> BestSeller's BookClub</h2>

        <article class="community-list">
        <div class="community-item" v-for="book in bestBooks" :key="book.isbn13">
          <div class="img-btn" @click="addBookClub(book.isbn13)">
            <div class="add-community">
                <p class="add-icon" >+</p>
            </div>
            <div class="cover-one">
              <div class="cover-two">
                <img :src="book.cover" :alt="book.title" class="item-image" />
              </div>
            </div>
          </div>  
          <div class="item-details">
            <h3 class="item-title">{{ book.title.replace(/\(.*?\)/g, '').trim() }}</h3>
            <p class="item-author">{{ book.author.replace(/\(.*?\)/g, '').trim() }}</p>
          </div>
        </div>
      </article>
      </section>
    </div>
    </div>
  </template>
  <script>
  import apiClient from "@/api/axiosInstance";
  import { onMounted, ref } from "vue";
  import { useAuthStore } from '@/stores/auth';
  import LeftSidebar from "@/components/layouts/LeftSidebar.vue";
  import { useUtilModalStore } from "@/stores/utilModalStore";
  import { useConfirmModalStore } from "@/stores/utilModalStore";

  export default {
    components: {LeftSidebar},
    setup() {
      const authStore = useAuthStore();
      const utilModalStore = useUtilModalStore();

      const communities = ref([]); // 서버에서 받아온 도서리스트
      const searchValue = ref(""); //검색어 
      const searchList= ref(false); // 검색버튼 누르면 리스트 보일 수 있게

      const searchBookClub = async(searchValue) => {
        console.log('찾으려는 북클럽 keyword : '+ searchValue);
        try{
          console.log('겟요청 보내기');
          const response = await apiClient.get("/api/bookclub/search", {
            params : {keyword : searchValue},
          });
            console.log('겟 요청 후');
            if(response.status == 200){
              communities.value = response.data;
              console.log(JSON.stringify(communities.value));
            }
        }catch(error){
          console.error(error, '오류 발생');
        }
        searchList.value = true;
      }

      //커뮤니티 추가
      const addBookClub = async(isbn13) => {
        try{

          // 로그인 여부 확인
          if (!authStore.user?.userId) {
            alert("로그인이 필요합니다!");
            window.location.href = "http://localhost:3000/auth/login";
            return;
          }

        const response = await apiClient.post(
  `/api/userbookclub/add/bookclub?isbn13=${encodeURIComponent(isbn13)}&userId=${authStore.user.userId}`
);
        console.log(response.data);
        if(response.data.data === true){
          utilModalStore.showModal(
            '안내',
            '북클럽이 추가되었습니다.',
            'alert'
          )
        }else{
          utilModalStore.showModal(
            '안내',
            '이미 추가된 북클럽입니다.',
            'alert'
          )
        }
      }catch(error){
        console.log(error, "!!!!!오류 발생");
      }
      readMyClubs();
    };

      // 유저 북클럽 리스트 가져오기 

    const  myclubList = ref([]);
    const readMyClubs = async() => {
      try{
      const response = await apiClient.get(
        "/api/userbookclub/mybookclubs",
       {params : {userId :authStore.user.userId}
      });
      myclubList.value = response.data.data;
      console.log(JSON.stringify(myclubList.value));
      }catch(error){
        console.error(error, "리스트 가져오는 중 에러 발생!")
      }
    };

      // 북클럽 리스트 삭제 버튼 오픈
    const showEdit = ref(false);
    const openEdit = () => {
      if(showEdit.value === true){
        showEdit.value=false;
      }else{
        showEdit.value = true;
      }
    }

    const confirmRemove = (userClubId)=>{
      const confirmModalStore = useConfirmModalStore();
      confirmModalStore.showModal(
        '북클럽 삭제',
        '북클럽을 삭제하겠습니까?',
        '삭제 후 복원은 어렵습니다.',
        '삭제하기',
        '',
        () => removeClub(userClubId)
      )
    };

      // 북클럽 삭제
    const removeClub = async(userClubId) => {
        try{
        const response = await apiClient.delete(
          "/api/userbookclub/remove/myclub", {
            params : {userClubId : userClubId},
          });
          if(response.status === 200){
            utilModalStore.showModal(
            '안내',
            '북클럽이 삭제되었습니다.',
            'alert'
          )
          }
      }catch(error){
        console.error(error, "에러 발생");
      }
      readMyClubs();
      };

        // Best Seller 리스트

    const apiUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
    const ttbKey = "ttbyoungjae.bae1809001";
        
    const bestBooks = ref([]);
        
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
                isbn13: book.isbn13,
                description : book.description
            }));
        } catch (error) {
            console.error("Error fetching Best Books data:", error);
        }
        console.log(bestBooks.value);
    };


    onMounted(()=>{
      console.log('회원아이디 :'+ authStore.user.userId);
      readMyClubs();
      fetchBestBooks();
    })
      return {
        communities,
        searchValue,
        searchList,
        searchBookClub,
        addBookClub,
        myclubList,
        readMyClubs,
        showEdit,
        openEdit,
        confirmRemove,
        removeClub,
        fetchBestBooks,
        bestBooks,
      };
    },
  };
  </script>
<style scoped>
/* Global Styles */
body {
  font-family: "Inter", sans-serif;
  margin: 0;
  padding: 0;

}

/* Main Container */
.community {
  height: 100vh;
  display: flex;
}

/* sidebar */

.svg-container {
    display: flex;
    justify-content: end;
  }

  .rotatable-svg {
    transition: transform 0.5s ease;
  }

  .svg-container:hover .rotatable-svg {
    transform: rotate(360deg);
    cursor: pointer;
  }



.sidebar3 {
  width: 250px;
  padding: 10px 20px;
}

.bookclub-list {
  margin-top: 20px;
}

.bookclub-item {
  display: flex;
  align-items: center;
  height: 50px;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.bookclub-item:hover {
  cursor: pointer;
  background-color: rgba(214, 214, 214, 0.6);
  border-radius: 5px;
  margin-top: 20px;
  }

.note-icon {
  width: 25px;
  height: 25px;
  margin-right: 13px;
  border-radius: 50%;
}

.bookclub-details {
  display: flex;
}

.bookclub-name {
  font-size: 16px;
  font-weight: bold;
  margin :auto
}

.close-icon {
  width: 13px;
  height: 13px;
  margin: auto;
  margin-left: 5px;
}

/* main */
.main{
  display: flex;
  margin-top: 50px;
  width: 60%;
  align-items: center;
  flex-direction: column;
}

/* Header Section */
.header {
 display: flex;
 flex-direction: column;
 align-items: center;
 justify-content: center;
  margin-bottom: 40px;
  line-height: 2;
}
.header h1 {
  font-size: 33px;
  font-weight: 400;
  margin: 20px 0;
}
.title-quotes {
    display: flex;
    align-items: center;
    gap : 20px;
}
.quotes {
    padding-bottom: 30px;
}
.header .subtitle {
  font-size: 18px;
  color: #444;
}

.search-bar {
    display: flex;
    align-items: center;
    background-color: transparent;
    border: 1px solid #909090;
    border-radius: 50px;
    padding: 10px 20px;
    width: 100%;
    max-width: 600px;
    box-sizing: border-box;
    margin-top: 70px;
    box-shadow: 1px 1.5px rgba(0, 0, 0, 0.2);
}
.search-bar input {
    flex: 1;
    border : none;
    outline: none;
    background-color: transparent;
    font-size: 16px;
    text-align: center;
    color: #909090;
}
.search-bar img {
    width: 24px;
    height: 24px;
    margin-left: 10px;
    cursor: pointer;
}

.search-guide:focus {
  text-align: left;
}


/* Community List Section */
.search-content {
  width: 100%;
  max-width: 900px;
  margin-top: 90px;
}
.community-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 한 줄에 3개 */
  gap: 20px; /* 아이템 간의 간격 */
}
.list-title {
  font-size: 20px;
  font-weight: 400;
  color: #909090;
  text-align: left;
  margin-bottom: 30px;
}

/* Community List */

.community-item {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 10px;
  background-color: #fff;
  width: 125px;
}

.item-details {
  flex: 1;
  text-align: left;
  line-height: 1.4;
  align-self: flex-start;
}
.item-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0;
}
.item-description {
  font-size: 15px;
  color: #444;
}
.item-author {
  font-size: 13px;
  color: #909090;
}
.item-image {
  width: 100%;
  height: 100%;
  border: 1px double #c0c0c0;
  margin-right: 1px;
  border-radius: 4px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2); /* 그림자 추가 */
  transform-style: preserve-3d; /* 3D 변환 활성화 */
  transition: transform 0.5s ease;
}

/* 커뮤니티 추가 버튼 */
.img-btn {
  position: relative;
  width: 145px;
  height: 180px;
  margin-bottom: 10px;
}

.add-icon {
  font-size: 60px;
  font-weight: 700;
  color: #fff;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* 중앙 정렬 */
}

.add-community {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* 중앙 정렬 */
  z-index: 10;
  opacity: 0; /* 기본적으로 숨김 */
  visibility: hidden; /* 기본적으로 숨김 */
  transition: opacity 0.3s ease, visibility 0.3s ease; /* 부드러운 전환 */
}

.img-btn:hover .add-community {
  opacity: 1; /* 호버 시 보이게 */
  visibility: visible; /* 호버 시 보이게 */
  cursor: pointer;
}

.cover-one {
  position: relative;
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin: 10px;
}

.cover-two {
  position: relative;
  border: 1px double gainsboro;
  border-left: none;
  border-top: none;
  border-radius: 4px;
  margin-right: 1px;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.2);
}

.img-btn:hover .cover-one {
  transform: scale(1.05); /* 마우스 오버 시 이미지 크기 확대 */
  filter: brightness(0.5);
  cursor: pointer;
}


</style>
  
  