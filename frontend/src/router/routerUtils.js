import { useRouter } from "vue-router";

export const gotoDetail = (isbn13) => {
  const router = useRouter(); // Vue Router 인스턴스
  router.push({
    path: `/miniroom/book/${isbn13}`,
  });
};
