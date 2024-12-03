import { reactive } from "vue";

export const modalState = reactive({
  isUserInfoModalOpen: false,
});

export const openUserInfoModal = () => {
  modalState.isUserInfoModalOpen = true;
};

export const closeUserInfoModal = () => {
  modalState.isUserInfoModalOpen = false;
};
