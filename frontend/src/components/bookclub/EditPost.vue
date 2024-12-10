<template>
<div class="modal-wrap" v-show="modelValue">
    <div class="modal-content" @click.stop>
        <div class="modal-items">
            <header class="header">
                <h3>게시글 수정</h3>
                <hr>
            </header>
            <article class="post-form">
                <div :v-show="uploadImg" v-for="img,index in imageSrc" :key="index" class="preview">
                    <div class="preImg-box">
                      <img @click="removeImg(index)" src="@/assets/icons/close.png" alt="delete image" class="del-img">
                      <img :src="img.url" class="preview-img">
                    </div>
                </div>
                <textarea v-model="postContent" class="post-text" placeholder="책에 대한 이야기를 자유롭게 즐겨보세요.">{{ postContent }}</textarea>

                <div class="icon-btn">
                <button class="open-file" @click="triggerFileInput">
                <span class="file-wrapper">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 71 67">
                    <path
                        stroke-width="5"
                        stroke="black"
                        d="M41.7322 11.7678L42.4645 12.5H43.5H68.5V64.5H2.5V2.5H32.4645L41.7322 11.7678Z"
                    ></path>
                    </svg>
                    <span class="file-front"></span>
                </span>
                Open file
                </button>
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
  import axios from 'axios';
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
         const response = await axios.get("http://localhost:8081/api/post/getOne", {
          params : {postId : props.editId},
         });
         console.log(response.data);
        } catch (error) {
          console.error("게시글 데이터를 불러오는 중 오류 발생:", error);
        }
      };

      const uploadImg = ref(false);
      const fileInput = ref(null); //input file 참조
      const selectedImg = ref([]);   //선택된 이미지의 경로
      const onFileChange = (event) =>{
        const files = event.target.files;

          if(files.length + selectedImg.value.length >2){
              alert("이미지는 최대 2장까지 업로드 가능합니다.");
              return ;
          }
          for(let i=0; i<files.length ; i++){
              const file = files[i]
              if(!file.type.match("image/.*")){
                  alert("이미지 파일만 업로드 가능합니다.");
                  continue;
              }
              const reader = new FileReader(); //파일 읽기
              reader.onload = (e) => {
                  selectedImg.value.push({file,url:e.target.result}); // 선택된 파일의 url 
              };
              reader.readAsDataURL(file);
              uploadImg.value= true;
          }
        };
      const triggerFileInput = () => {
          fileInput.value.click();  // input file 태그를 클릭하게끔 함
      }
      const removeImg = (index) => {
          imageSrc.value.splice(index, 1); // 배열에서 해당 인덱스 제거
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
        selectedImg,
        onFileChange,
        triggerFileInput,
        uploadImg,
        fileInput,
        removeImg,
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
  .preImg-box {
      display: flex;
      flex-direction: column;
      width: 170px;
      height: 170px;
    }
  .del-img {
    width: 15px;
    height: 15px;
    margin-left: auto;
  }
  .del-img:hover{
    cursor: pointer;
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
    /* 이미지 파일 */
  .open-file {
    background-color: rgb(255, 255, 255);
    width: 140px;
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    border: 1px solid rgb(217, 217, 217);
    font-size: 15px;
    cursor: pointer;
    transition: all 0.3s;
    border-radius: 10px;
  }
  .file-wrapper {
    width: 15px;
    height: auto;
    display: flex;
    align-items: flex-end;
    justify-content: center;
    position: relative;
  }
  .file-wrapper svg {
    width: 100%;
  }
  .file-front {
      position: absolute;
      width: 85%;
      height: 60%;
      border: 2px solid rgb(0, 0, 0);
      border-bottom: 1px solid black;
      transform: skewX(-30deg);
      transform-origin: bottom right;
      background-color: white;
      transition: all 0.5s;
      bottom: 0;
  }
  .open-file:hover .file-front {
    height: 50%;
    transform-origin: bottom right;
    transform: skewX(-45deg);
  }
  .open-file:hover {
    box-shadow: 0px 10px 10px rgba(0, 0, 0, 0.048);
  }
</style>