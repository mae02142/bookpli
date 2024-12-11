<template>
    <div class="modal-wrap" @click="closeModal">
        <div class="modal-content">
            <div class="modal-item">
                <h3 style="font-weight: 400; font-size: 20px;">게시글 삭제</h3>
                <div class="modal-text">
                    <hr>
                    <p>게시글을 삭제하겠습니까?</p>
                    <p>게시글을 삭제하면 복원할 수 없습니다.</p>
                    <button class="modal-btn" @click="deleteList">삭제</button>
                    <button class="modal-btn" @click="closeModal">취소</button>
                </div>
            </div>
        </div>
    </div>
    </template>
    <script>
import apiClient from '@/api/axiosInstance';
import { onMounted } from 'vue';

    
    export default {
        props : {
            isVisible : {
                type : Boolean,
                default : false,
            },
            deleteId: {
                type : Number,
                required: true, //부모컴포에서 항상 값이 전달되어야만 함.
            }
        },
        emits : ["update:isVisible", "delete-post"],
        // setup 첫번째 매개변수 _ 는 props 근데 props를 밑에서 사용안하면 _ 로 사용하지 않는 매개변수라고 나타냄 
        setup(props, {emit}) {
            
            onMounted(()=>{
                console.log('삭제할 게시글 : '+ props.deleteId);
            })
    
            const closeModal = () =>{
                emit("delete-post"); //해당 게시글 제거
                emit("update:isVisible",false);
                // 부모 컴포넌트에 상태 변경 요청하는 거
            };
    
            // 서버로 삭제 요청
            const deleteList = async() => {
            try{
                console.log("자식컴포에서 확인 :"+props.deleteId);
                const response = await apiClient.delete("/api/post/delete", {
                    params : {postId :props.deleteId },
                });
                if(response.data.data == true){
                    closeModal();
                } 
            }catch(error){
                console.error(error +'에러발생 !');
            }}
            
            return {
                closeModal,
                deleteList,
            };
        }
    }
    
    </script>
    <style scoped>
        .modal-wrap {
            position: fixed;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.4);
        }
    
        .modal-content {
            font-family: "Inter-Regular", sans-serif;
            font-size: 18px;
            position: relative;
            top : 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 400px;
            height: 270px;
            background: #fff;
            border-radius: 20px;
            padding : 20px;
            box-sizing: border-box;
        }
        .modal-item {
           text-align: center;
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
        .modal-text {
            color: #909090;
            line-height: 3.5;
        }
    </style>