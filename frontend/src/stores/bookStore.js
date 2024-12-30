import {defineStore} from 'pinia';
import {ref, computed} from 'vue';

export const useBookStore = defineStore('bookStore',() =>{

    const rbook = ref({});
    const isDataLoaded = ref(false); 

    const bookDetails = computed(() => rbook.value);

    const setbook = (book) =>{
        if(book && book.isbn13){
            rbook.value={...book};
            console.log("작가와 페이지수 확인",book.value.data);
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