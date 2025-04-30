/*!

=========================================================
 * Vue Paper Dashboard - v1.0.1
=========================================================

 * Product Page: http://www.creative-tim.com/product/paper-dashboard
 * Copyright 2023 Creative Tim (http://www.creative-tim.com)
 * Licensed under MIT (https://github.com/creativetimofficial/paper-dashboard/blob/master/LICENSE.md)

=========================================================

 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 */
import Vue from "vue";
import App from "./App";
import router from "./router/index";

import PaperDashboard from "./plugins/paperDashboard";
import "vue-notifyjs/themes/default.css";
import "@/assets/sass/map-element.scss";
import Toasted from 'vue-toasted'
import { initializeLoginState } from "@/utils/loginState";

// 전역 등록
Vue.use(PaperDashboard);
Vue.use(Toasted, {
  position: 'top-center',   // 모든 컴포넌트에 기본 위치 설정
  duration: 3000,
  theme: 'bubble',          // (선택) 테마
  singleton: true,          // (선택) 중복 방지
  fullWidth: false          // (선택) 너무 긴 메시지 방지
})

// 앱 시작 시 로그인 상태 초기화
initializeLoginState();

/* eslint-disable no-new */
new Vue({
  router,
  render: (h) => h(App),
}).$mount("#app");
