import Vue from "vue";
import VueRouter from "vue-router";
import routes from "./routes";
Vue.use(VueRouter);

// configure router
const router = new VueRouter({
  // history: createWebHistory(), // hash 모드 대신 history 모드 사용
  routes, // short for routes: routes
  linkActiveClass: "active",
});

// 전역 가드 설정(페이지 접근 권한 설정)
const publicPages = ['/login', '/signup', '/maps']
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken');

  if(publicPages.includes(to.path)) {
    next();
  } else {
    if(token) {
      next()  // 토큰 있으면 통과
    } else {
      alert('로그인이 필요합니다!');
      next('/login'); // 토큰이 없으면 로그인 페이지로 이동
    }
  }
})

export default router;
