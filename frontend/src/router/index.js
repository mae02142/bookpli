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
    }
  ],
});

export default router;
