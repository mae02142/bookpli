import {defineStore} from 'pinia';
import {ref} from 'vue';

export const useBookStore = defineStore('bookStore',() =>{

    const rbook = ref({});

    const setbook = (book) =>{
        rbook.value= {...book};
    };

    const updateStatus=(status) => {
        if(!rbook.value) rbook.value= {};
        rbook.value.status= status;
    };

    const updateDate = (startDate, endDate) => {
        if(!rbook.value) rbook.value= {};
        rbook.value.startDate= startDate;
        rbook.value.endDate = endDate;
    };

    return {rbook, setbook, updateStatus, updateDate}

});