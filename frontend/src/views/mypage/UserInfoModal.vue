<template>
  <div class="profile-modal-container">
    <div class="profile-modal-overlay"></div>
    <div class="profile-modal-content">
      <img class="user-avatar"  :src="user.profilePath || '@/assets/images/default_image.png'" alt="User Avatar" />
      <h2 class="profile-modal-title">회원 정보 확인</h2>
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
            <div 
              class="nickname-grid" 
              :class="{ 'error-border': nicknameError }"
            >
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
          <span v-if="nicknameError" class="error-message">{{ errorMessage }}</span> <!-- 에러 메시지 표시 -->
          <span v-if="nicknameCheck" class="check-message">{{ checkMessage }}</span>
          <div v-if="isEditing" class="edit-buttons">
            <button @click="saveEdit" class="save-btn">수정완료</button>
            <button @click="cancelEdit" class="cancel-btn">취소</button>
          </div>
        </div>
        <div class="edit-buttons">
          <button v-if="!isEditing" class="confirm-btn" @click="closeModal">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>


  
<script setup>
import { ref, onMounted, reactive } from "vue";
import { useAuthStore } from "@/stores/auth";
import apiClient from "@/api/axiosInstance";

const authStore = useAuthStore();

const user = reactive({});
const originalNickname = ref("");
const isEditing = ref(false);
const nicknameError = ref(false);
const nicknameCheck = ref(false);
const errorMessage = ref("");
const checkMessage = ref("");
const emit = defineEmits(["close"]);

// 편집 시작
const startEditing = () => {
  isEditing.value = true;
  originalNickname.value = user.userNickname;
};

// 닉네임 저장
const saveEdit = async () => {
  try {
    // 닉네임 중복 체크 후 저장
    const duplicateResponse = await apiClient.get(`/api/mypage/nickname/${user.userNickname}`);
    if (duplicateResponse.data.data) {
      nicknameError.value = true;
      errorMessage.value = "이미 존재하는 닉네임입니다.";
      return;
    }

    // 닉네임 저장 요청
    const request = {
      userId: authStore.user.userId,
      userNickname: user.userNickname,
    };
    await apiClient.patch("/api/mypage", request);
    isEditing.value = false;
    nicknameError.value = false;
    nicknameCheck.value = false;
    errorMessage.value = "";
  } catch (error) {
    console.error("데이터 저장 오류:", error);
  }
};

// 닉네임 변경 취소
const cancelEdit = () => {
  isEditing.value = false;
  nicknameError.value = false;
  nicknameCheck.value = false;
  errorMessage.value = "";
  user.userNickname = originalNickname.value; // 원본 복원
};

// 모달 닫기
const closeModal = () => {
  emit("close-modal");
};

// 유저 정보 로드
const loadUserInfo = async () => {
  try {
    const response = await apiClient.get(`/api/mypage/${authStore.user.userId}`);
    Object.assign(user, response.data.data);
  } catch (error) {
    console.error("데이터 불러오기 오류:", error);
  }
};

// 닉네임 중복 확인
const duplicateCheckNickname = async () => {
  // 닉네임이 빈 값일 경우 처리
  if (!user.userNickname || user.userNickname.trim() === "") {
    nicknameError.value = true;
    errorMessage.value = "닉네임을 입력해 주세요.";
    nicknameCheck.value = false;
    return;  // 빈 값이면 API 요청을 하지 않음
  }

  try {
    const response = await apiClient.get(`/api/mypage/nickname/${user.userNickname}`);
    if (response.data.data) {
      // 이미 존재하는 닉네임
      nicknameError.value = true;
      nicknameCheck.value = false;
      errorMessage.value = "이미 존재하는 닉네임입니다.";
    } else if (nicknameCheck.value === null) {
      // 닉네임이 한글자 미만일 때
      errorMessage.value = "닉네임은 한글자 이상이어야 합니다.";
    } else {
      // 사용 가능한 닉네임
      nicknameError.value = false;
      nicknameCheck.value = true;
      checkMessage.value = "✅사용 가능한 닉네임입니다.";
    }
  } catch (error) {
    console.error("닉네임 중복 확인 오류:", error);
    nicknameError.value = true;
    errorMessage.value = "닉네임 확인 중 오류가 발생했습니다.";
  }
};


onMounted(() => {
  loadUserInfo();
});
</script>


  
  <style scoped>
  .profile-modal-container {
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
  
  .profile-modal-overlay {
    position: absolute;
    width: 100%;
    height: 100%;
    background: transparent;
  }
  
  .profile-modal-content {
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
  
  .profile-modal-title {
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
    border: 1px solid #b7b7b7;
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
    padding: 12px 20px;
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
    padding: 12px 20px;
    border-radius: 30px;
    border: 1px solid gray;
    width: 120px;
    background: #fff8bb;
  }
  
  .cancel-btn {
    margin-top: 20px;
    padding: 12px 20px;
    border-radius: 30px;
    border: 1px solid gray;
    width: 120px;
    background: #ffffff;
  }

  .save-btn:hover, .cancel-btn:hover, .confirm-btn:hover {
    cursor: pointer;
  }

  .nickname-grid {
  padding: 0;
  width: 100%;
  border: 1px solid #b7b7b7;;
  border-radius: 5px;
  display: flex;
  align-items: center;
  transition: border-color 0.3s ease;
}

.nickname-grid.error-border {
  border-color: red;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 4px;
}

.check-message {
  color: #00b707;
  font-size: 12px;
  margin-top: 4px;
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
  