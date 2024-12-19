<template>
  <div class="edit-modal-wrap">
    <div class="edit-modal-content" @click.stop>
      <div class="edit-modal-items">
        <header class="header">
          <h3>게시글 수정</h3>
          <hr />
        </header>
        <article class="post-form">
          <div :v-if="post.imageUrl " 
               class="preview-section">
            <div class="preImg-box"
            v-for="img, index in imageSrc" 
            :key="index">
              <img
                @click="removeImg(index)"
                src="@/assets/icons/close.png"
                alt="delete image"
                class="del-img"
              />
              <img :src="img" class="preview-img" />
            </div>
          </div>
          <textarea
            v-model="post.postContent"
            class="post-text"
            placeholder="책에 대한 이야기를 자유롭게 즐겨보세요."
          />
          <div class="icon-btn">
            <button class="open-file" @click="triggerFileInput">
              <span class="file-wrapper">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 71 67">
                  <path
                    stroke-width="5"
                    stroke="black"
                    d="M41.7322 11.7678L42.4645 12.5H43.5H68.5V64.5H2.5V2.5H32.4645L41.7322 11.7678Z"
                  />
                </svg>
                <span class="file-front"></span>
              </span>
              Open file
            </button>
            <input
              type="file"
              ref="fileInput"
              style="display: none"
              @change="onFileChange"
              multiple
              accept="image/*"
            />
            <div>
              <button @click="confirmUpdate" class="edit-modal-btn">수정</button>
              <button @click="closeModal" class="edit-modal-btn">취소</button>
            </div>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosInstance';
import { ref, onMounted, } from 'vue';
import {ref as firebaseRef, uploadBytes, getDownloadURL} from "firebase/storage";
import { firebaseStorage } from "@/firebase/firebaseConfig";
import { useConfirmModalStore } from '@/stores/utilModalStore';
import { useUtilModalStore } from '@/stores/utilModalStore';

export default {
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    editId: {
      type: Number,
      required: true,
    },
  },
  emits: ['update:modelValue', 'close'],
  setup(props, { emit }) {
    const post = ref({});
    const fileInput = ref(null);
    const imageSrc = ref([]);   //미리보기 이미지 데이터 
    const selectedFiles = ref([]); // 실제 파일 데이터
    const utilModalStore = useUtilModalStore();

    onMounted(() => {
      loadPostData();
    });
    // 수정 데이터 불러오기
    const loadPostData = async () => {
  try {
    const response = await apiClient.get('/api/post/getOne', {
      params: { postId: props.editId },
    });
    post.value = response.data.data;

    // imageSrc 에 imageUrl만 저장
    imageSrc.value = post.value.imageUrl.map(img => img.imageUrl);
  } catch (error) {
    console.error('게시글 데이터를 불러오는 중 오류 발생:', error);
  }
};

    const onFileChange = (event) => {
      const files = event.target.files;
      if (files.length + imageSrc.value.length > 4) {      
        utilModalStore.showModal(
          '경고',
          '이미지는 최대 4장까지 업로드 가능합니다',
          'alert'
        )
        return;
      }
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (!file.type.match('image/.*')) {
          utilModalStore.showModal(
          '경고',
          '이미지 파일만 업로드 가능합니다.',
          'alert'
        );
        return;
        }
        const reader = new FileReader(); // 파일을 읽어오는 FileReader 생성
        reader.onload = (e) => {
          const fileDataUrl = e.target.result; // url 로드 수행

          imageSrc.value.push(fileDataUrl); 
        };
        reader.readAsDataURL(file); // 파일을 Data URL로 읽음
        selectedFiles.value.push(file);
      }
    };

    const triggerFileInput = () => {
      fileInput.value.click();
    };
    
    const removeImg = (index) => {
      imageSrc.value.splice(index, 1); // 배열에서 해당 인덱스 제거
      selectedFiles.value.splice(index, 1);
    };

    // 서버에 수정 요청
    const updatePost = async () => {
      try {
        // 이미지 업로드
        const uploadedUrls= await uploadImagesToFirebase(selectedFiles.value, 'path');

        post.value.imageUrl = imageSrc.value.map((url) => {
      // 기존 이미지와 매핑
      const existingImage = post.value.imageUrl.find(img => img.imageUrl === url);

      // 기존 이미지인 경우 imageId 유지
      if (existingImage) {
        return {
          imageId: existingImage.imageId,
          imageUrl: existingImage.imageUrl,
          postId: post.value.postId // 게시글 ID
        };
      }

      // 새로 추가된 이미지
      const newImageUrl = uploadedUrls.shift(); //순서대로 매핑-> List형태 
      return {
        imageId: null, // 새 이미지라서 ID 없음
        imageUrl: newImageUrl, // 새로 업로드한 URL
        postId: post.value.postId
      };
    });

        // 서버에 수정된 게시글 정보 전송
        const response = await apiClient.put('/api/post/edit', post.value);
        if(response.status ==200){
          closeModal();
        }
      } catch (error) {
        console.error('게시글 수정 실패', error);
      }
    };

    const confirmUpdate = () => {
      const confirmModalStore = useConfirmModalStore();
      confirmModalStore.showModal(
        '게시글 수정',
        '수정을 완료하시겠습니까?',
        '',
        '수정',
        '',
        () => updatePost()
      )
    };

    const closeModal = () => {
        emit('close');
        emit('update:modelValue', false);
    };

    // 파이어베이스 이미지 업로드 함수

    const uploadImagesToFirebase = async (files, path) => {
      const urls = [];
      for (const file of files) {
          const uniqueName = `${Date.now()}-${file.name}`; // 중복 이름 없게
          const storageRef = firebaseRef(firebaseStorage, `${path}/${uniqueName}`);
          try {
          await uploadBytes(storageRef, file);
          const url = await getDownloadURL(storageRef);
          urls.push(url);
          } catch (error) {
          console.error(`Error uploading image ${file.name}:`, error);
          }
      }
      return urls;
      };

    return {
      post,
      fileInput,
      imageSrc,
      selectedFiles,
      onFileChange,
      triggerFileInput,
      removeImg,
      updatePost,
      confirmUpdate,
      closeModal,
      uploadImagesToFirebase,
      useUtilModalStore,
    };
  },
};
</script>
  
  <style scoped>
      /* 모달 전체 스타일 */
      .edit-modal-wrap {
        position: fixed;
        left: 0;
        top : 0;
        width: 100%;
        height: 100%;
        background: rgb(0, 0, 0, 0.4);
        z-index: 1000;
    }
    .edit-modal-content {
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
  .preview-section{
    flex-wrap: wrap;
    display: flex;
    flex-direction: row;
    justify-content: center;
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
      margin-right: 10px;
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

    .edit-modal-btn {
        width: 50px;
        height: 35px;
        background-color: #fffdf1;
        border-radius: 8px;
        border: none;
        margin : 10px;
    }
    .edit-modal-btn:hover{
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