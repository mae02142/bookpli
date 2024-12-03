<template>
<div class="modal-wrap" v-if="modelValue" @click="closeModal">
    <div class="modal-content" @click.stop>
        <div class="modal-items">
            <header class="header">
                <h3>게시글 등록</h3>
                <hr>
            </header>
        <article class="post-content">
            <div :v-show="uploadImg" v-for="img,index in imageSrc" :key="index" class="preview">
                <img :src="img.url" class="preview-img">
            </div>
            <textarea class="post-text" placeholder="책에 대한 이야기를 자유롭게 즐겨보세요."></textarea>
            <div class="icon-btn">
                <img @click="triggerFileInput"  src="@/assets/icons/image-add.png" alt="click to upload">
                <input type="file" ref="fileInput" style="display: none;" @change="onFileChange" multiple accept="image/*">
                <button @click="putUpPost" class="modal-btn">게시</button>
            </div>
        </article>
        </div>
    </div>
</div>
</template>
<script>
import {ref} from "vue";
    export default {
        props: {
            modelValue : {
                type : Boolean,
                default : false,
            },
        },
        emits : ["update:modelValue"],
        setup(_,{emit}) {
            const closeModal = () =>{ //모달 닫기
                emit("update:modelValue", false);
            }
            const uploadImg = ref(false);
            const fileInput = ref(null); //input file 참조
            const imageSrc = ref([]);   //선택된 이미지의 경로
            const onFileChange = (event) =>{
                const files = event.target.files;

                if(files.length + imageSrc.value.length >4){
                    alert("이미지는 최대 4장까지 업로드 가능합니다.");
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
            }
            const putUpPost = () => { //게시글 등록
                //axios 수행
                alert("게시글이 등록되었습니다");
                closeModal();
                //mypost 다시 리로딩
            }
            return{
                closeModal,
                putUpPost,
                triggerFileInput,
                onFileChange,
                fileInput,
                imageSrc,
                uploadImg,
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
</style>