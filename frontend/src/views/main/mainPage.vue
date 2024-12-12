<template>
  <img src="@/assets/banners/thumbnail2.png" class="main-thumbnail">
  <div class="toggle-container">
    <!-- Toggle Switch -->
    <label class="switch">
      <input type="checkbox" v-model="isMusicSection" />
      <span class="slider">
                <span class="slider-label left-label" v-show="isMusicSection">Music</span>
                <span class="slider-label right-label" v-show="!isMusicSection">Book</span>
      </span>
    </label>
  </div>

  <!-- Conditional rendering for bookSection and musicSection -->
  <div v-if="!isMusicSection" style="padding-bottom: 100px;">
    <book-section />
  </div>
  <div v-else style="padding-bottom: 100px;">
    <music-section />
  </div>
</template>

<script setup>
  import { ref, onMounted } from "vue";
  import { useUserStore } from "@/stores/user.js";
  import { useAuthStore } from '@/stores/auth.js';
  import bookSection from "@/components/bookSection.vue";
  import musicSection from "@/components/musicSection.vue";

  const isMusicSection = ref(false);
  const authStore = useAuthStore();

  const getToken = async() => {
    const spotifyId = authStore.user.spotifyId;

    const response = await fetch(`http://localhost:8081/tokens/accessToken?spotifyId=${spotifyId}`, {
        credentials: 'include',
    });
    
    if (!response.ok) {
        throw new Error('Failed to fetch access token');
    }

    const data = await response.json();
    const accessToken = data.access_token;
    
    console.log("토큰 받아오기 성공띠");
    // Get the user store instance here
    const userStore = useUserStore(); 
    
    userStore.setAccessToken(accessToken);
    console.log("토큰 전달 성공띠");
  };

  onMounted(async () => {
    getToken();
  });
</script>

<style scoped>
/* Toggle Container */
.toggle-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px 0;
}

/* Toggle Switch Styles */
.switch {
  position: relative;
  display: inline-block;
  width: 150px;
  height: 40px;
}

.switch input {
  opacity: 0;
  width: 150px; /* Ensures input is clickable */
  height: 40px;
  position: absolute; /* Match slider position */
  z-index: 2; /* Ensure it's above the slider */
  cursor: pointer;
}

.slider {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  border-radius: 20px;
  transition: background-color 0.4s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: black;
}

.slider:before {
  position: absolute;
  content: "";
  height: 34px;
  width: 70px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  border-radius: 17px;
  transition: transform 0.4s;
}

input:checked + .slider {
  background-color: #67de86;
}

input:checked + .slider:before {
  transform: translateX(75px);
}

/* Page Labels */
.slider-label {
  position: absolute;
  font-size: 0.9rem;
  font-weight: bold;
  user-select: none;
  transition: opacity 0.4s;
}

/* Specific positions for labels */
.left-label {
  left: 23px; /* Align to the left */
}

.right-label {
  right: 23px; /* Align to the right */
}
</style>
