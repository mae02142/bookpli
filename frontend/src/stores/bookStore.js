import {defineStore} from 'pinia';
import {ref} from 'vue';

export const useBookStore = defineStore('bookStore',() =>{

    const rbook = ref({});
    const isDataLoaded = ref(false); 

    const setbook = (book) =>{
        if(book && book.isbn13){
            rbook.value={...book};
            isDataLoaded.value = true;
        }else{
            rbook.value= {};
            isDataLoaded.value = false;
        }
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

    const setLoaded = (loaded) => {
        isDataLoaded.value = loaded;
    };

    return {rbook, setbook, updateStatus, updateDate, isDataLoaded}

});