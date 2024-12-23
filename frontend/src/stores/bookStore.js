import {defineStore} from 'pinia';
import {ref} from 'vue';

export const useBookStore = defineStore('bookStore',() =>{

    const rbook = ref({});

    const setbook = (book) =>{
        rbook.value= book;
    };

    const updateStatus=(status) => {
        rbook.value.status= status;
    };

    const updateDate = (startDate, endDate) => {
        rbook.value.startDate= startDate;
        rbook.value.endDate = endDate;
    };

    return {rbook, setbook, updateStatus, updateDate}

});