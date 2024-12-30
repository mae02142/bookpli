import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/main", // 기본 경로에서 /main으로 리다이렉트
    },
    {
      path: "/login",
      component: () => import("@/views/auth/LoginPage.vue"),
    },
    {
      path: "/mypage",
      meta: { requiresAuth: true },
      children: [
        {
          path: "mypli",
          component: () => import("@/views/mypage/MyPlaylist.vue"),
        },
        {
          path: "mylibrary",
          component: () => import("@/views/mypage/MyLibrary.vue"),
        },
      ],
    },
    {
      path: "/review",
      meta: { requiresAuth: true },
      children: [
        {
          path: "mylist",
          component: () => import("@/views/review/MyReviewList.vue"),
        },
        {
          path: "form",
          component: () => import("@/components/review/ReviewForm.vue"),
        },
        {
          path: "book",
          component: () => import("@/components/review/BookReview.vue"),
        },
      ],
    },
    {
      path: "/bookclub",
      meta: { requiresAuth: true },
      children: [
        {
          path: "",
          component: () => import("@/views/bookclub/BookclubMain.vue"),
        },
        {
          path: "community",
          component: () => import("@/views/bookclub/CommunityDetail.vue"),
        },
        {
          path: "mybookclub",
          component: () => import("@/views/bookclub/MyBookclub.vue"),
        },
      ],
    },

    {
      path: "/miniroom",
      children: [
        {
          path: "minihome",
          component: () => import("@/views/miniroom/MiniHome.vue"),
        },
      ],
    },

    {
      path: "/main",
      children: [
        {
          path: "",
          component: () => import("@/views/main/mainPage.vue"),
        },
        {
          path: "music",
          component: () => import("@/components/musicSection.vue"),
        },
        {
          path: "book/:isbn13",
          component: () => import("@/views/miniroom/BookDetail.vue"),
        },
      ],
    },

    {
      path: "/search-book",
      component: () => import("@/views/main/bookSearch.vue"),
    },

    {
      path: "/search-music",
      component: () => import("@/views/main/MusicSearch.vue"),
    },

    {
      path: "/artist/:id",
      component: () => import("@/views/main/ArtistDetails.vue"),
    },

    {
      path: "/details/:category",
      component: () => import("@/views/main/DetailsPage.vue"),
    },
  ],
});

// 전역 가드 추가
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore(); // Pinia 스토어에서 인증 상태 가져오기

  // `requiresAuth`가 true인 경우 인증 여부 확인
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // 인증되지 않은 경우 로그인 페이지로 리다이렉트
    next({ path: "/login" });
  } else {
    // 인증된 경우 또는 인증 불필요한 경로로 이동
    next();
  }
});

export default router;
