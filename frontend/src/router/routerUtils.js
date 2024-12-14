import { useRouter } from "vue-router";

export const useRouterUtils = () => {
  const router = useRouter();

  const gotoDetail = (isbn13) => {
    router.push({
      path: `/main/book/${isbn13}`,
    });
  };

  return { gotoDetail };
};
