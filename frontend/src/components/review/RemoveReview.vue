<template>
<div class="review-modal-wrap" v-if="isVisible" @click="closeModal">
    <div class="review-modal-content">
        <div class="review-modal-item">
            <h3 style="font-weight: 400; font-size: 20px;">리뷰 삭제</h3>
            <div class="review-modal-text">
                <hr>
                <p>리뷰를 삭제하겠습니까?</p>
                <p>리뷰를 삭제하면 복원할 수 없습니다.</p>
                <button class="review-modal-btn" @click="deleteList">삭제</button>
                <button class="review-modal-btn" @click="closeModal">취소</button>
            </div>
        </div>
    </div>
</div>
</template>
<script>

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
    emits : ["closeModal", "delete-review"],
    // setup 첫번째 매개변수 _ 는 props 근데 props를 밑에서 사용안하면 _ 로 사용하지 않는 매개변수라고 나타냄 
    setup(props, {emit}) {

        const closeModal = () =>{
            emit('closeModal');
            // 부모 컴포넌트에 상태 변경 요청하는 거
        };

        const deleteList = () => {
            emit('delete-review', props.deleteId, 'closeModal'); //삭제 요청을 부모로 보내기
        }
        
        return {
            closeModal,
            deleteList,
        };
    }
}

</script>
<style scoped>
    .review-modal-wrap {
        position: fixed;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.4);
    }

    .review-modal-content {
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
    .review-modal-item {
       text-align: center;
    }

    .review-modal-btn {
        width: 50px;
        height: 35px;
        background-color: #fffdf1;
        border-radius: 8px;
        border: none;
        margin : 20px;
    }
    .review-modal-btn:hover{
        cursor: pointer;
        background-color: beige;
    }
    .review-modal-text {
        color: #909090;
        line-height: 3.5;
    }
</style>