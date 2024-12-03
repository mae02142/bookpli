<template>
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

const isMusicSection = ref(false); 
</script>

<style scoped>
/* Toggle Container */
.toggle-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 20px 0;
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
    width: 150px;
    height: 40px;
    position: absolute;
    cursor: pointer;
}

.slider {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ccc;
    border-radius: 5px;
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
    border-radius: 3px;
    transition: transform 0.4s;
}

input:checked + .slider {
    background-color: #f4f4b1;
}

input:checked + .slider:before {
    transform: translateX(75px);
}

.slider-label {
    position: absolute;
    font-size: 0.9rem;
    font-weight: bold;
    z-index: 3;
}

.left-label {
    left: 20px;

}

.right-label {
    right: 20px;

}

</style>
