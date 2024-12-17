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

export const useLoginAlertModalStore = defineStore("loginAlertModal", {
  state: () => ({
    isModalVisible: false,
    modalActionMessage: "",
    message: "",
    confirmCallback: null, // 명칭 수정
    cancelCallback: null, // 명칭 수정
  }),
  actions: {
    showModal(
      modalActionMessage,
      message,
      { onConfirm = null, onCancel = null } = {}
    ) {
      this.isModalVisible = true;
      this.modalActionMessage = modalActionMessage;
      this.message = message;
      this.confirmCallback = onConfirm; // 명칭 수정
      this.cancelCallback = onCancel; // 명칭 수정
    },
    confirm() {
      if (this.confirmCallback) this.confirmCallback();
      this.reset();
    },
    cancel() {
      if (this.cancelCallback) this.cancelCallback();
      this.reset();
    },
    reset() {
      this.isModalVisible = false;
      this.modalActionMessage = "";
      this.message = "";
      this.confirmCallback = null;
      this.cancelCallback = null;
    },
  },
});
