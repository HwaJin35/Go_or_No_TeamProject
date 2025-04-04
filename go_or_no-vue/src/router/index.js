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

export default router;
