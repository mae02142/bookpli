import "./assets/styles/main.css";
import "./assets/styles/font.css";

import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import Header from "./components/layouts/Header.vue";
import axios from "axios";
import UserInfoModal from "./views/mypage/UserInfoModal.vue";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

axios.defaults.baseURL = import.meta.env.VITE_API_URL;

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

//공용 컴포넌트 등록
app.component("Header", Header);
app.component("UserInfoModal", UserInfoModal);

app.config.globalProperties.axios = axios;
app.mount("#app");