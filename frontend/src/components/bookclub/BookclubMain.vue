<template>
    <div class="community">
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
            <input v-model="searchValue" class="search-guide" placeholder="찾으려는 커뮤니티의 도서명을 입력하세요"></input>
            <img class="search-community" @click="searchCommunity(searchValue)" src="@/assets/icons/searchW.png" alt="Search Community">
        </div>
      </header>
      <section v-if="searchList" class="search-content">
        <h2 class="list-title">“{{ searchValue }}” 에 해당하는 커뮤니티</h2>
        <div class="divider"></div>
      
      <article class="community-list">
        <div class="community-item" v-for="(item, index) in communities" :key="index">
          <div class="img-btn">
            <img :src="item.image" :alt="item.title" class="item-image" />
            <span class="add-community" @click="addCommunity">
                <img class="add-icon" src="@/assets/icons/add.png"><p>커뮤니티 추가</p>
             </span>
          </div>  
          <div class="item-details">
            <h3 class="item-title">{{ item.title }}</h3>
            <p class="item-author">{{ item.author }}</p>
          </div>
        </div>
      </article>
      </section>
    </div>
  </template>
  <script>
  import { ref } from "vue";
  
  export default {
    setup() {
      const communities = ref([
        {
          title: "소년이 온다",
          author: "한강",
          image: "image-40.png",
        },
        {
          title: "크리스마스로 불리는 소년",
          author: "서현숙",
          image: "content-10.png",
        },
        {
          title: "소년을 찾아서",
          author: "매트 헤이그",
          image: "boy-10.png",
        },
      ]);
      const searchValue = ref(""); //검색어 
      const searchList= ref(false); // 검색버튼 누르면 리스트 보일 수 있게
      const searchCommunity = (searchValue) => {
        console.log("찾으려는 커뮤니티 : "+searchValue);
        if(!searchValue) {
            return console.log("검색 결과 없음");
        }
                // axios 넣어야함 
        return searchList.value = true;
    }; 
      const addCommunity = () => {
        // 커뮤니티를 추가하면 해당 커뮤니티 url 이 사이드바에 저장되게 해야함
      };
      return {
        communities,
        searchValue,
        searchList,
        searchCommunity,
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
  box-sizing: border-box;
}

/* Main Container */
.community {
  max-width: 1200px;
  margin: 100px auto;
  padding: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
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
    background-color: #d9d9d9; /* 배경 색상 */
    border-radius: 50px;
    padding: 10px 20px;
    width: 100%;
    max-width: 600px;
    box-sizing: border-box;
    margin-top: 30px;
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
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
}
.list-title {
  font-size: 24px;
  font-weight: 400;
  color: #000;
  text-align: left;
  margin-bottom: 10px;
}
.divider {
  width: 100%;
  height: 1px;
  background: #000;
  margin-bottom: 20px;
}

/* Community Item */
.community-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px;
}
.item-image {
  width: 115px;
  height: 130px;
  object-fit: cover;
  border: 1px solid #c0c0c0;
  margin-right: 20px;
}
.item-details {
  flex: 1;
  text-align: left;
  line-height: 1.8;
  align-self: flex-start;
}
.item-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}
.item-description {
  font-size: 16px;
  color: #444;
}
.item-author {
  font-size: 14px;
  color: #909090;
}
.img-btn {
  display: flex;
  flex-direction: column;
  gap: 5px;  
}
.add-icon {
    width: 15px;
    height: 15px;
    margin-right: 7px;
}
.add-community:hover {
    cursor: pointer;
}
.add-community {
    display: flex;
}

</style>
  
  