<template>
  <div>
    <header v-if="!isAuthLoginPage">
      <Header></Header>
    </header>
    <div class="wrapper">
      <router-view></router-view>
      <ConfirmModal></ConfirmModal>
      <UtilModal></UtilModal>
      <LoginAlertModal></LoginAlertModal>
      <loadingSpinner></loadingSpinner>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from "vue-router";
import { computed } from "vue";
import ConfirmModal from "./components/modal/ConfirmModal.vue";
import UtilModal from "./components/modal/UtilModal.vue";
import LoginAlertModal from "./components/modal/LoginAlertModal.vue";
import loadingSpinner from "./components/loadingSpinner/loadingSpinner.vue";

const route = useRoute();

// 현재 경로가 /login인지 확인
const isAuthLoginPage = computed(() => route.path === "/login");

// 현재 경로가 메인 페이지인지 확인
const isMainPage = computed(() => route.path === "/main");
</script>

