import { defineStore } from "pinia";

export const useUtilModalStore = defineStore("utilModal", {
  state: () => ({
    isModalVisible: false,
    modalAction: "",
    message: "",
    type: "",
  }),
  actions: {
    showModal(modalAction, message, type) {
      this.isModalVisible = true;
      this.modalAction = modalAction;
      this.message = message;
      this.type = type;
    },
    closeModal() {
      this.isModalVisible = false;
      this.modalAction = "";
      this.message = "";
      this.type = "";
    },
  },
});

export const useConfirmModalStore = defineStore("confirmModal", {
  state: () => ({
    isModalVisible: false,
    modalActionMessage: "",
    questionMessage: "",
    warningMessage: "",
    cofirmButton: "",
    type: "",
    confirmCallback: null, // 확인 버튼을 눌렀을 때 실행할 콜백 함수
  }),
  actions: {
    showModal(
      modalActionMessage,
      questionMessage,
      warningMessage,
      cofirmButton,
      type,
      callback = null
    ) {
      this.isModalVisible = true;
      this.modalActionMessage = modalActionMessage;
      this.questionMessage = questionMessage;
      this.warningMessage = warningMessage;
      this.cofirmButton = cofirmButton;
      this.type = type;
      this.confirmCallback = callback; // 콜백 함수 저장
    },
    closeModal() {
      this.isModalVisible = false;
      this.modalActionMessage = "";
      this.questionMessage = "";
      this.warningMessage = "";
      this.cofirmButton = "";
      this.type = "";
      this.confirmCallback = null; // 모달이 닫히면 콜백 함수 초기화
    },
  },
});
