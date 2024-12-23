
<template>
  <div v-if="loading.isLoading" class="loader">
    <p class="text">
    <span class="letter letter1">L</span>
    <span class="letter letter2">o</span>
    <span class="letter letter3">a</span>
    <span class="letter letter4">d</span>
    <span class="letter letter5">i</span>
    <span class="letter letter6">n</span>
    <span class="letter letter7">g</span>
    <span class="letter letter8">.</span>
    <span class="letter letter9">.</span>
    <span class="letter letter10">.</span>
    </p>
  </div>
</template>

<script setup>
import { useLoadingStore } from '@/stores/loading';

const loading = useLoadingStore();
</script>

<style scoped>
.loader {
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
  top: 50%;
  left: 45%;
  position: fixed;
}

  .text {
    color: black;
    font-weight: bolder;
  }

  @keyframes letter {
    0% {
      font-size: 25px;
    }

    50% {
      font-size: 30px;
    }

    100% {
      font-size: 25px;
    }
  }

  .letter {
    animation: letter 1s infinite;
  }

  .letter1 {
    animation-delay: 0s;
  }

  .letter2 {
    animation-delay: -0.9s;
  }

  .letter3 {
    animation-delay: -0.8s;
  }

  .letter4 {
    animation-delay: -0.7s;
  }

  .letter5 {
    animation-delay: -0.6s;
  }

  .letter6 {
    animation-delay: -0.5s;
  }

  .letter7 {
    animation-delay: -0.4s;
  }

  .letter8 {
    animation-delay: -0.3s;
  }

  .letter9 {
    animation-delay: -0.2s;
  }

  .letter10 {
    animation-delay: -0.1s;
  }
</style>

