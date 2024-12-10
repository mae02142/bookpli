<template>
  <div>
    <header v-if="!isAuthLoginPage">
      <Header></Header>
    </header>
    <div class="wrapper">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { useRoute } from "vue-router";
import { computed } from "vue";

const route = useRoute();

// 현재 경로가 auth/login인지 확인
const isAuthLoginPage = computed(() => route.path === "/auth/login");

// 현재 경로가 메인 페이지인지 확인
const isMainPage = computed(() => route.path === "/");
</script>

<style scoped>
.main-thumbnail {
  width: 100%;
}
</style>
