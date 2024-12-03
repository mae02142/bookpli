<template>
    <div class="toggle-container">
        <!-- Toggle Switch -->
        <label class="switch">
            <input type="checkbox" v-model="isMusicSection" />
            <span class="slider">
                <span class="slider-label">{{ isMusicSection ? "Music" : "Books" }}</span>
            </span>
        </label>
    </div>

    <!-- Conditional rendering for bookSection and musicSection -->
    <div v-if="!isMusicSection">
        <book-section />
    </div>
    <div v-else>
        <music-section />
    </div>
</template>

<script setup>
import { ref } from "vue";
import bookSection from "@/components/bookSection.vue";
import musicSection from "@/components/musicSection.vue";

const isMusicSection = ref(false); // Default to 'Book Page'
</script>

<style scoped>
/* Toggle Container */
.toggle-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 20px 0;
    gap: 10px;
}

/* Toggle Switch Styles */
.switch {
    position: relative;
    display: inline-block;
    width: 80px;
    height: 35px;
}

.switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ccc;
    transition: 0.4s;
    border-radius: 35px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: bold;
    color: Black;
}

.slider:before {
    position: absolute;
    content: "";
    height: 25px;
    width: 25px;
    left: 5px;
    bottom: 5px;
    background-color: white;
    transition: 0.4s;
    border-radius: 50%;
}

input:checked + .slider {
    background-color: #007bff;
}

input:checked + .slider:before {
    transform: translateX(45px);
}

/* Page Label on Slider */
.slider-label {
    position: absolute;
    font-size: 0.8rem;
    font-weight: bold;
    pointer-events: none;
}
</style>
