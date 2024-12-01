import "./assets/styles/main.css";
import "./assets/styles/font.css";

import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";
import Header from "./components/layouts/Header.vue";

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.component("Header", Header); //공용 컴포넌트 등록

app.mount("#app");
