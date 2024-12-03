<template>
<div class="modal-wrap" v-show="modelValue">
    <div class="modal-content" @click.stop>
        <div class="modal-items">
            <header class="header">
                <h3>게시글 수정</h3>
                <hr>
            </header>
            <article class="post-content">
                <div :v-show="uploadImg" v-for="img,index in imageSrc" :key="index" class="preview">
                    <img :src="img.url" class="preview-img">
                </div>
                <textarea v-model="postContent" class="post-text" placeholder="책에 대한 이야기를 자유롭게 즐겨보세요.">{{ postContent }}</textarea>

                <div class="icon-btn">
                    <img @click="triggerFileInput"  src="@/assets/icons/image-add.png" alt="click to upload">
                    <input type="file" ref="fileInput" style="display: none;" @change="onFileChange" multiple accept="image/*">
                    <div>
                        <button @click="updatePost" class="modal-btn">수정</button>
                        <button @click="closeModal" class="modal-btn">취소</button>
                    </div>
                </div>
            </article>
        </div>
    </div>
</div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  
  export default {
    props:  {
        modelValue: {
            type : Boolean,
            default : false,
        },
        editId : {
            type : Number,
            required : true,
        }
    },
    emits: ['update:modelValue'],
    setup(props,{emit}) {
      // 서버에서 불러온 게시글 내용
      const postContent = ref('');
    
      // 서버에서 불러온 이미지 URL 리스트
      const imageSrc = ref([]);

      const closeModal = () => {
        const result =confirm("수정을 완료하시겠습니까?");
        if(result){
            emit("update:modelValue",false);
        }else{
            return;
        }
      }
  
      // 컴포넌트가 마운트되면 데이터를 로딩하는 로직 (예시)
      onMounted(() => {
        // 서버에서 게시글 데이터와 이미지 데이터 불러오는 예시
        loadPostData();
      });
  
      // 서버에서 게시글 데이터 및 이미지 URL을 불러오는 함수
      const loadPostData = async () => {
        try {
          // 예시: 서버에서 게시글 내용과 이미지 URL을 받아온다고 가정
          postContent.value = `크리스마스로 불리는 소년은 크리스마스를 떠올리기에 딱 좋았던 책인 것 같다. 
          크리스마스를 좋아하는 내게 굉장히 흥미로웠고 영화를 먼저 봤었기 때문에 영화가 책의 내용을 잘 담아냈다고 생각한다.`;
          
          // 예시: 서버에서 이미지 URL을 받아오는 로직 (여기서는 더미 데이터 사용)
          imageSrc.value = [
            { url: 'https://t2.daumcdn.net/thumb/R720x0/?fname=http://t1.daumcdn.net/brunch/service/user/7KD/image/Twc6gpoHWkc7XDaoU_pZ3opJcWk.jpg' } // 이미지 URL을 불러와서 설정
          ];
        } catch (error) {
          console.error("게시글 데이터를 불러오는 중 오류 발생:", error);
        }
      };


      const updatePost = () => {
                console.log("자식컴포에서 확인 :"+props.editId);
                emit("edit-post", props.editId); //삭제 요청을 부모로 보내기
                closeModal();
            }
  
//       onMounted(async () => {
//   try {
//     const response = await axios.get('/api/get-post'); // 서버에서 데이터를 받아오는 요청
//     postContent.value = response.data.content; // 게시글 내용
//     imageSrc.value = response.data.images; // 이미지 URL 리스트
//   } catch (error) {
//     console.error("데이터 불러오기 오류:", error);
//   }
// });
 

      return { 
        postContent, 
        imageSrc,
        closeModal, 
        updatePost,
     };
    },
  };
  </script>
  
  <style scoped>
      /* 모달 전체 스타일 */
      .modal-wrap {
        position: fixed;
        left: 0;
        top : 0;
        width: 100%;
        height: 100%;
        background: rgb(0, 0, 0, 0.4);
        z-index: 1000;
    }
    .modal-content {
        font-family: "Inter-Regular", sans-serif;
        font-size: 18px;
        position: relative;
        top : 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 600px;
        height: 500px;
        background: #fff;
        border-radius: 20px;
        padding : 50px;
        box-sizing: border-box;
        overflow: scroll;
    }
    /*  헤더 스타일  */
    .header {
        text-align: center;
        font-weight: 600;
    }    
    .header h3 {
        margin-bottom: 30px;
    }
  .image-preview {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
  
  .preview-img {
    width: 150px;
    height: 150px;
    object-fit: cover;
    border-radius: 10px;
  }
  
     /* 컨텐츠 스타일 */
     .post-text {
        width: 100%;
        height: 300px;
        resize: none;
        border: none;
        outline: none;
    }
    .preview {
        width: auto;
        height: auto;
        display: flex;
        flex-direction: row;
    }
    .preview-img {
        width: 150px;
        height: 150px;
    }
    .modal-btn {
        width: 50px;
        height: 35px;
        background-color: #fffdf1;
        border-radius: 8px;
        border: none;
        margin : 10px;
    }
    .modal-btn:hover{
        cursor: pointer;
        background-color: beige;
        }

    .icon-btn {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>