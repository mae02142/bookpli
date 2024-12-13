import { useRouter } from "vue-router";

export const gotoDetail = (isbn13) => {
  console.log("js 파일의 isbn>>>>>>>>>", isbn13);
  const router = useRouter(); // Vue Router 인스턴스
  router.push({
    path: `/main/book/${isbn13}`,
  });
};
