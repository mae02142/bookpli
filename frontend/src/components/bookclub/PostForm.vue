<template>
<div class="modal-wrap"
 v-show="modelValue"
  @click="closeModal">
    <div class="modal-content" @click.stop>
        <div class="modal-items">
            <header class="header">
                <h3>게시글 등록</h3>
                <hr>
            </header>
        <article class="post-form">
            <div class="preview">
                <!-- 이미지 미리보기 -->
                <div :v-if="imageSrc.length > 0 " 
                    v-for="img,index in imageSrc" 
                    :key="index"
                >
                    <div class="preImg-box">
                      <img 
                      @click="removeImg(index)"
                       src="@/assets/icons/close.png" 
                       alt="delete image" class="del-img"
                       >
                      <img :src="img.url" class="preview-img">
                    </div>
                </div>       
            </div>
            <textarea class="post-text" v-model="form.postContent" placeholder="책에 대한 이야기를 자유롭게 즐겨보세요."></textarea>
            <div class="icon-btn">
                <button class="open-file" 
                @click="triggerFileInput"
                >
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
                <input type="file" ref="fileInput" 
                style="display: none;"
                @change="onFileChange" 
                multiple accept="image/*"
                >
                <button @click="putUpPost" class="modal-btn">게시</button>
            </div>
        </article>
        </div>
    </div>
</div>
</template>
<script>
import apiClient from '@/api/axiosInstance';
import {onMounted, ref, toRaw} from "vue";
import {getStorage, ref as storageRef, uploadBytes, getDownloadURL} from "firebase/storage";
    export default {
        props: {
            modelValue : {
                type : Boolean,
                default : false,
            },
            userId : {
                type : Number,
                required : true,
            },
            bookclubId : {
                type : Number,
                required : true,
            }
        },
        emits: ['update:modelValue','close','reload'],
        setup(props,{emit}) {

            onMounted(()=> {
                console.log('bookclub : '+props.bookclubId);
                console.log('userId : '+props.userId);

            })
            const storage = getStorage();  // firebase storage 초기화
         
            const fileInput = ref(null); //input file 참조
            const imageSrc = ref([]);   //선택된 이미지의 경로
            const onFileChange = (event) =>{
                const files = event.target.files;

                if(files.length + imageSrc.value.length >3){
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
                        imageSrc.value.push({file,url:e.target.result}); // 선택된 파일의 url 
                    };
                    reader.readAsDataURL(file);
                    uploadImg.value= true;
                }
            };
            const triggerFileInput = () => {
                fileInput.value.click();  // input file 태그를 클릭하게끔 함
            };
            const removeImg = (index) => {
                imageSrc.value.splice(index, 1); // 배열에서 해당 인덱스 제거
            };

                        /* 게시글 등록 */

            const form = ref({
                userId : props.userId,
                bookClubId : props.bookclubId,
                postContent : "",

            });
            const putUpPost = async() => {
                console.log('게시 전 내용'+JSON.stringify(form.value));
                const rawForm = toRaw(form.value);
                console.log('게시 내용'+JSON.stringify(rawForm));

                const response = await apiClient.post("/api/post/insert", rawForm);
                if(response.data.data == true){
                    alert("게시글이 등록되었습니다");
                }else{
                    alert("게시글 등록에 실패하였습니다");
                }
                imageSrc.value = "";
                emit('close');
                closeModal();
                //mypost 다시 리로딩
            };

                    //모달 닫기
            const closeModal = () =>{ 
                console.log("모달창을 닫습니다.");
                imageSrc.value = "";
                emit("update:modelValue", false);
                emit("reload");
            }
            return{
                storage,
                closeModal,
                form,
                putUpPost,
                triggerFileInput,
                onFileChange,
                fileInput,
                imageSrc,
                removeImg,
            };
        },
    }
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
        justify-content: center;
    }
    .del-img {
        width: 15px;
        height: 15px;
        margin-left: auto;
    }
    .del-img:hover{
        cursor: pointer;
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
    .modal-btn {
        width: 50px;
        height: 35px;
        background-color: #fffdf1;
        border-radius: 8px;
        border: none;
        margin : 20px;
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