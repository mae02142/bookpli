<template>
  <div class="modal-container">
    <div class="modal-overlay"></div>
    <div class="modal-content">
      <img class="user-avatar" src="@/assets/images/default_image.png" alt="User Avatar" />
      <h2 class="modal-title">회원 정보 확인</h2>
      <div class="input-grid">
        <div class="info-section">
          <label class="info-label">이름</label>
          <div class="info-box">
            <input type="text" class="info-input" v-model="user.display_name" readonly />
          </div>
        </div>

        <div class="info-section">
          <label class="info-label">이메일</label>
          <div class="info-box">
            <input type="text" class="info-input" v-model="user.email" readonly />
          </div>
        </div>

        <div class="info-section">
          <label class="info-label">Spotify ID</label>
          <div class="info-box">
            <input type="text" class="info-input" v-model="user.id" readonly />
          </div>
        </div>

        <div class="info-section">
          <label class="info-label">닉네임</label>
          <div class="nickname-box">
            <div class="nickname-grid">
              <input 
                type="text" 
                v-model="user.userNickname" 
                :readonly="!isEditing" 
                class="nickname-input"
              />
              <img 
                v-if="!isEditing" 
                src="@/assets/icons/edit_black.png" 
                @click="startEditing" 
                class="edit-icon"
              />
              <img 
                v-if="isEditing" 
                src="@/assets/icons/double_check.png" 
                @click="duplicateCheckNickname" 
                class="edit-icon"
              />
            </div>
          </div>
          <div v-if="isEditing" class="edit-buttons">
            <button @click="saveEdit" class="save-btn">수정완료</button>
            <button @click="cancelEdit" class="cancel-btn">취소</button>
          </div>
        </div>

      </div>
      <button v-if="!isEditing" class="confirm-btn"  @click="closeModal">확인</button>
    </div>
  </div>
</template>

  
<script setup>
import { ref, onMounted, reactive } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import { defineEmits } from 'vue';

const authStore = useAuthStore();

const user = reactive({}); // 빈 객체로 초기화
const originalNickname = ref(""); // 초기값 저장
const isEditing = ref(false);
 // 부모로 이벤트 전달
  const emit = defineEmits(['close']);

const startEditing = () => {
  isEditing.value = true;
  originalNickname.value = user.userNickname; // 편집 시작 시 원본 값 저장
};

const saveEdit = async () => {
  isEditing.value = false;
  try {
    const request = {
      userId: authStore.user.userId,
      userNickname: user.userNickname
    }
    await axios.patch('/api/mypage', request);
    isEditing.value = false;
  } catch (error) {
    console.error("데이터 불러오기 오류:", error);
  }
};

const cancelEdit = () => {
  isEditing.value = false;
  user.userNickname = originalNickname.value; // 저장된 초기값으로 복구
};

const confirmChanges = async () => {
  try {
    const request = {
      userId: authStore.user.userId,
      userNickname: user.userNickname
    }
    await axios.patch('/api/mypage', request);
    isEditing.value = false;
  } catch (error) {
    console.error("데이터 불러오기 오류:", error);
  }
};

const closeModal = () => {
  emit('close'); // 부모로 close 이벤트 전달
};

const loadUserInfo = async () => {
  try {
    const response = await axios.get(`/api/mypage/${authStore.user.userId}`);
    Object.assign(user, response.data.data); // user 객체에 데이터 병합
  } catch (error) {
    console.error("데이터 불러오기 오류:", error);
  }
};

const duplicateCheckNickname = async () => {
  try {
    const request = {
      userId: authStore.user.userId,
      userNickname: user.userNickname
    }
    
    const response = await axios.patch('/api/mypage', request);
    console.log("바뀐 닉네임:", response.data.data);
  } catch (error) {
    console.error("데이터 불러오기 오류:", error);
  }
};

onMounted(() => {
  loadUserInfo();
  console.log("User ID:", authStore.user.userId);
});
</script>

  
  <style scoped>
  .modal-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.6);
    z-index: 1000;
  }
  
  .modal-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background: transparent;
  }
  
  .modal-content {
    background: #ffffff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
    z-index: 1001;
    width: 500px;
    display: grid;
    justify-items: center;
  }
  
  .user-avatar {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    margin: 20px 0px;
  }
  
  .modal-title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
  }
  
  .info-section {
    margin-bottom: 15px;
  }
  
  .info-label {
    font-size: 15px;
    font-weight: 500;
    color: #000;
    margin-bottom: 5px;
    display: block;
  }
  
 .nickname-box{
  position: relative;
  display: inline-block;
  width: 100%;
}

.edit-icon {
  position: absolute;
  top: 50%;
  right: 10px; /* input 오른쪽 내부 여백 */
  transform: translateY(-50%);
  height: 20px;
  cursor: pointer; /* 클릭 가능하도록 커서 추가 */
  pointer-events: auto; /* 클릭 가능하도록 변경 */
  width: 24px;
  height: 24px;
}


  .info-input {
    width: 360px;
    padding: 10px;
    border: 1px solid #555553;
    border-radius: 5px;
    font-size: 16px;
    color: #20201f;
    display: flex;
    align-items: center;
  }

  .input-grid {
    display: flex;
    flex-direction: column;
    place-self: center;
    gap: 7px;
  }

  .edit-icon:hover {
    cursor: pointer;
  }

  .confirm-btn {
    margin-top: 20px;
    padding: 10px 40px;
    border-radius: 20px;
    border: 1px solid gray;
    width: 150px;
    background: #fff8bb;
  }

  .edit-buttons{
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 20px;
  }

  .save-btn {
    margin-top: 20px;
    padding: 10px 40px;
    border-radius: 20px;
    border: 1px solid gray;
    width: 150px;
    background: #fff8bb;
  }
  
  .cancel-btn {
    margin-top: 20px;
    padding: 10px 40px;
    border-radius: 20px;
    border: 1px solid gray;
    width: 150px;
    background: #ffffff;;
  }

  .save-btn:hover, .cancel-btn:hover, .confirm-btn:hover {
    cursor: pointer;
  }

  .nickname-grid {
  padding: 0;
  width: 100%;
  border: 1px solid #555553;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nickname-input {
  width: 90%;
  height: 40px; /* 적절한 높이 설정 */
  border: none;
  padding: 0px 5px;
  font-size: 16px;
  color: #20201f;
  box-sizing: border-box;
  margin-left: 3px;
}
  </style>
  