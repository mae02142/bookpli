import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/auth",
      children: [
        {
          path: "login",
          component: () => import("@/views/auth/LoginPage.vue"),
        },
      ],
    },
    {
      path: "/mypage",
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
      path: "/miniroom",
      children: [
        {
          path: "book",
          component: () => import("@/views/miniroom/BookDetail.vue"),
        },
        {
          path: "minihome",
          component: () => import("@/views/miniroom/MiniHome.vue"),
        },
        {
          path: "goal",
          component: () => import("@/views/miniroom/ReadingGoal.vue"),
        },
      ],
    },

    {
      path: "/main",
      children: [
        {
          path: "",
          component: () => import("@/views/main/mainPage.vue")
        },
        {
          path: "music",
          component: () => import("@/components/musicSection.vue")
        },
      ]
    },
    
    {
      path: "/bookclub",
      children: [
        {
          path: "",
          component: () => import("@/views/bookclub/BookclubMain.vue"),
        },
        {
          path : "community",
          component: () => import("@/views/bookclub/CommunityDetail.vue"),
        },
        {
          path : "mybookclub",
          component : () => import("@/views/bookclub/MyBookclub.vue"),
        },
      ]
    },
  ],
});

export default router;